package mz.smsclassifier.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mz.smsclassifier.classifier.Classification
import mz.smsclassifier.classifier.ClassificationStore
import mz.smsclassifier.classifier.PendingSms
import mz.smsclassifier.sms.IncomingSms
import mz.smsclassifier.sms.SmsRepository

class RoomSmsRepository(
    private val dao: SmsDao,
) : SmsRepository, ClassificationStore {
    override val messages: Flow<List<IncomingSms>> =
        dao.observeAll().map { rows -> rows.map { it.toIncomingSms() } }

    override suspend fun addIncoming(sms: IncomingSms) {
        dao.insert(sms.toEntity())
    }

    override suspend fun addOutgoing(sms: IncomingSms) {
        dao.insert(sms.copy(outgoing = true, unread = false).toEntity())
    }

    override suspend fun upsertAll(messages: List<IncomingSms>) {
        dao.insertAll(messages.map { it.toEntity() })
    }

    override suspend fun markRead(sender: String) {
        dao.markRead(sender)
    }

    override suspend fun pendingClassification(modelVersion: String, limit: Int): List<PendingSms> =
        dao.pendingClassification(modelVersion, limit).map { PendingSms(it.id, it.toIncomingSms()) }

    override suspend fun saveClassification(id: Long, classification: Classification) {
        dao.updateClassification(
            id = id,
            category = classification.category.name,
            confidence = classification.confidence,
            modelVersion = classification.modelVersion,
        )
    }
}
