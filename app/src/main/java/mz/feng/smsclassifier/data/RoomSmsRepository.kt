package mz.feng.smsclassifier.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mz.feng.smsclassifier.classifier.Classification
import mz.feng.smsclassifier.classifier.ClassificationStore
import mz.feng.smsclassifier.classifier.PendingSms
import mz.feng.smsclassifier.sms.IncomingSms
import mz.feng.smsclassifier.sms.SmsRepository

class RoomSmsRepository(
    private val dao: SmsDao,
) : SmsRepository, ClassificationStore {
    override val messages: Flow<List<IncomingSms>> =
        dao.observeAll().map { rows -> rows.map { it.toIncomingSms() } }

    override suspend fun addIncoming(sms: IncomingSms) {
        dao.insert(sms.toEntity())
    }

    override suspend fun upsertAll(messages: List<IncomingSms>) {
        dao.insertAll(messages.map { it.toEntity() })
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
