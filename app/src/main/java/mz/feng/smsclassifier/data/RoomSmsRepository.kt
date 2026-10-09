package mz.feng.smsclassifier.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mz.feng.smsclassifier.sms.IncomingSms
import mz.feng.smsclassifier.sms.SmsRepository

class RoomSmsRepository(
    private val dao: SmsDao,
) : SmsRepository {
    override val messages: Flow<List<IncomingSms>> =
        dao.observeAll().map { rows -> rows.map { it.toIncomingSms() } }

    override suspend fun addIncoming(sms: IncomingSms) {
        dao.insert(sms.toEntity())
    }

    override suspend fun upsertAll(messages: List<IncomingSms>) {
        dao.insertAll(messages.map { it.toEntity() })
    }
}
