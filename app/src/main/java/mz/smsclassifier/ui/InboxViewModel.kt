package mz.smsclassifier.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mz.smsclassifier.SmsClassifierApp
import mz.smsclassifier.settings.ThemeMode
import mz.smsclassifier.sms.ContactDirectory
import mz.smsclassifier.sms.IncomingSms
import mz.smsclassifier.sms.SimChoice
import mz.smsclassifier.sms.SimDirectory
import mz.smsclassifier.sms.SimLine
import mz.smsclassifier.sms.IncomingSmsNotifier
import mz.smsclassifier.sms.VisibleConversation
import mz.smsclassifier.sms.SavedContact
import mz.smsclassifier.sms.SmsCategory
import mz.smsclassifier.sms.SmsPermissions
import mz.smsclassifier.sms.groupBySender
import mz.smsclassifier.sms.isReplyable
import mz.smsclassifier.sms.phoneKey
import mz.smsclassifier.sms.replyAddress

class InboxViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as SmsClassifierApp
    private val repository = app.smsRepository
    private val inboxSync = app.inboxSync
    private val classifyPendingSms = app.classifyPendingSms
    private val themePreferences = app.themePreferences
    private val smsSender = app.smsSender
    val replySending = MutableStateFlow(false)
    val replyFailed = MutableStateFlow(false)
    val simLines = MutableStateFlow<List<SimLine>>(emptyList())
    val selectedSimId = MutableStateFlow<Int?>(null)
    private val composing = MutableStateFlow(false)
    private val contacts = MutableStateFlow<List<SavedContact>>(emptyList())
    private val hasPermission = MutableStateFlow(SmsPermissions.areGranted(application))
    private val selectedSender = MutableStateFlow<String?>(null)
    private val selectedCategory = MutableStateFlow<SmsCategory?>(null)
    private val currentTab = MutableStateFlow(AppTab.MESSAGES)
    private val searchQuery = MutableStateFlow("")

    init {
        selectedSender
            .onEach { sender ->
                VisibleConversation.sender = sender
                if (sender != null) IncomingSmsNotifier.cancel(getApplication(), sender)
            }
            .launchIn(viewModelScope)
        refreshSims()
    }

    val uiState: StateFlow<InboxUiState> = combine(
        combine(repository.messages, hasPermission, selectedSender) { messages, permitted, sender ->
            Triple(messages, permitted, sender)
        },
        combine(selectedCategory, currentTab, searchQuery) { category, tab, query ->
            Triple(category, tab, query)
        },
        themePreferences.themeMode,
        combine(composing, contacts) { writing, saved -> writing to saved },
    ) { first, second, themeMode, composeState ->
        val (messages, permitted, sender) = first
        val (category, tab, query) = second
        val (writing, saved) = composeState
        InboxUiState(
            hasPermission = permitted,
            threads = groupBySender(messages),
            selectedSender = sender,
            selectedCategory = category,
            currentTab = tab,
            searchQuery = query,
            themeMode = themeMode,
            composing = writing,
            contacts = saved,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InboxUiState(hasPermission = hasPermission.value),
    )

    fun refreshPermission() {
        val granted = SmsPermissions.areGranted(getApplication())
        hasPermission.value = granted
        if (granted) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    inboxSync.sync()
                    classifyPendingSms()
                    contacts.value = ContactDirectory(getApplication()).load()
                } catch (_: Exception) {
                    // Mantém o que já está na base local.
                }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        currentTab.value = tab
        selectedSender.value = null
        if (tab != AppTab.CATEGORIES && tab != AppTab.MESSAGES) {
            selectedCategory.value = null
        }
        if (tab != AppTab.SEARCH && tab != AppTab.MESSAGES) {
            searchQuery.value = ""
        }
    }

    fun openCategory(category: SmsCategory) {
        selectedCategory.value = category
        selectedSender.value = null
    }

    fun closeCategory() {
        selectedCategory.value = null
        selectedSender.value = null
    }

    fun openThread(sender: String) {
        composing.value = false
        selectedSender.value = sender
        viewModelScope.launch(Dispatchers.IO) {
            repository.markRead(sender)
        }
    }

    fun closeThread() {
        selectedSender.value = null
        replyFailed.value = false
    }

    fun openComposer() {
        selectedSender.value = null
        selectedCategory.value = null
        replyFailed.value = false
        composing.value = true
    }

    fun closeComposer() {
        composing.value = false
        replyFailed.value = false
    }

    override fun onCleared() {
        VisibleConversation.sender = null
        super.onCleared()
    }

    fun refreshSims() {
        val lines = SimDirectory(getApplication()).lines()
        simLines.value = lines
        val chosen = SimChoice.resolve(getApplication(), lines)
        selectedSimId.value = chosen
        if (chosen != null) SimChoice.save(getApplication(), chosen)
    }

    fun cycleSim() {
        val lines = simLines.value
        if (lines.size < 2) return
        val index = lines.indexOfFirst { it.subscriptionId == selectedSimId.value }
        val next = lines[(index + 1).mod(lines.size)]
        selectedSimId.value = next.subscriptionId
        SimChoice.save(getApplication(), next.subscriptionId)
    }

    fun refreshContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            contacts.value = ContactDirectory(getApplication()).load()
        }
    }

    fun sendReply(body: String) {
        val sender = selectedSender.value ?: return
        transmit(sender, body.trim()) {}
    }

    fun startConversation(number: String, body: String) {
        val typed = number.trim()
        if (!isReplyable(typed)) return
        val known = uiState.value.threads.firstOrNull { phoneKey(it.sender) == phoneKey(typed) }?.sender
        val sender = known ?: replyAddress(typed)
        transmit(sender, body.trim()) {
            composing.value = false
            selectedSender.value = sender
        }
    }

    private fun transmit(sender: String, text: String, onSent: () -> Unit) {
        if (text.isEmpty() || !isReplyable(sender)) return
        viewModelScope.launch(Dispatchers.IO) {
            replySending.value = true
            replyFailed.value = false
            val sent = runCatching {
                smsSender.send(sender, text, selectedSimId.value)
            }.getOrDefault(false)
            if (sent) {
                repository.addOutgoing(
                    IncomingSms(
                        sender = sender,
                        body = text,
                        receivedAtMillis = System.currentTimeMillis(),
                        outgoing = true,
                    ),
                )
                onSent()
            } else {
                replyFailed.value = true
            }
            replySending.value = false
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { themePreferences.setThemeMode(mode) }
    }
}
