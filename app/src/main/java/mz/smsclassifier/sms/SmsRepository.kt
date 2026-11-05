package mz.smsclassifier.sms

import kotlinx.coroutines.flow.Flow

interface SmsRepository {
    val messages: Flow<List<IncomingSms>>
    suspend fun addIncoming(sms: IncomingSms): Boolean
    suspend fun addOutgoing(sms: IncomingSms)
    suspend fun upsertAll(messages: List<IncomingSms>)
    suspend fun markRead(senders: Collection<String>)
}
