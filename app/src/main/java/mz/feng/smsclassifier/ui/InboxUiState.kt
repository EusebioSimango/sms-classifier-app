package mz.feng.smsclassifier.ui

import mz.feng.smsclassifier.sms.SenderThread

data class InboxUiState(
    val hasPermission: Boolean = false,
    val threads: List<SenderThread> = emptyList(),
    val selectedSender: String? = null,
) {
    val selectedThread: SenderThread? =
        selectedSender?.let { sender -> threads.firstOrNull { it.sender == sender } }
}
