package mz.feng.smsclassifier.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mz.feng.smsclassifier.SmsClassifierApp
import mz.feng.smsclassifier.settings.ThemeMode
import mz.feng.smsclassifier.sms.SmsCategory
import mz.feng.smsclassifier.sms.SmsPermissions
import mz.feng.smsclassifier.sms.groupBySender

class InboxViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as SmsClassifierApp
    private val repository = app.smsRepository
    private val inboxSync = app.inboxSync
    private val themePreferences = app.themePreferences
    private val hasPermission = MutableStateFlow(SmsPermissions.areGranted(application))
    private val selectedSender = MutableStateFlow<String?>(null)
    private val selectedCategory = MutableStateFlow<SmsCategory?>(null)
    private val currentTab = MutableStateFlow(AppTab.MESSAGES)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<InboxUiState> = combine(
        combine(repository.messages, hasPermission, selectedSender) { messages, permitted, sender ->
            Triple(messages, permitted, sender)
        },
        combine(selectedCategory, currentTab, searchQuery) { category, tab, query ->
            Triple(category, tab, query)
        },
        themePreferences.themeMode,
    ) { first, second, themeMode ->
        val (messages, permitted, sender) = first
        val (category, tab, query) = second
        InboxUiState(
            hasPermission = permitted,
            threads = groupBySender(messages),
            selectedSender = sender,
            selectedCategory = category,
            currentTab = tab,
            searchQuery = query,
            themeMode = themeMode,
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
            viewModelScope.launch {
                try {
                    inboxSync.sync()
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
        selectedSender.value = sender
    }

    fun closeThread() {
        selectedSender.value = null
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { themePreferences.setThemeMode(mode) }
    }
}
