package mz.feng.smsclassifier.sms

import kotlinx.coroutines.flow.Flow

interface SmsRepository {
    val messages: Flow<List<IncomingSms>>
    suspend fun addIncoming(sms: IncomingSms)
    suspend fun upsertAll(messages: List<IncomingSms>)
}
