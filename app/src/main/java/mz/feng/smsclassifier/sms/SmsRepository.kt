package mz.feng.smsclassifier.sms

import kotlinx.coroutines.flow.StateFlow

interface SmsRepository {
    val messages: StateFlow<List<IncomingSms>>
    suspend fun loadInbox()
    fun addIncoming(sms: IncomingSms)
}
