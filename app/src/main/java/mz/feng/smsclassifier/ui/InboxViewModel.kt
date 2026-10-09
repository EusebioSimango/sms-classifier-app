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
import mz.feng.smsclassifier.sms.SmsPermissions
import mz.feng.smsclassifier.sms.groupBySender

class InboxViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as SmsClassifierApp).smsRepository
    private val hasPermission = MutableStateFlow(SmsPermissions.areGranted(application))
    private val selectedSender = MutableStateFlow<String?>(null)

    val uiState: StateFlow<InboxUiState> = combine(
        repository.messages,
        hasPermission,
        selectedSender,
    ) { messages, permitted, sender ->
        InboxUiState(
            hasPermission = permitted,
            threads = groupBySender(messages),
            selectedSender = sender,
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
            viewModelScope.launch { repository.loadInbox() }
        }
    }

    fun openThread(sender: String) {
        selectedSender.value = sender
    }

    fun closeThread() {
        selectedSender.value = null
    }
}
