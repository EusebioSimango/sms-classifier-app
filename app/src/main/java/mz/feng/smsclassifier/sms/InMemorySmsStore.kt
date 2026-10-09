package mz.feng.smsclassifier.sms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemorySmsStore {
    private val _messages = MutableStateFlow<List<IncomingSms>>(emptyList())
    val messages: StateFlow<List<IncomingSms>> = _messages.asStateFlow()

    fun setInbox(inbox: List<IncomingSms>) {
        val liveOnly = _messages.value.filter { live ->
            inbox.none { it.isSameAs(live) }
        }
        _messages.value = (liveOnly + inbox).sortedByDescending { it.receivedAtMillis }
    }

    fun prepend(sms: IncomingSms) {
        val current = _messages.value
        if (current.any { it.isSameAs(sms) }) return
        _messages.value = listOf(sms) + current
    }
}
