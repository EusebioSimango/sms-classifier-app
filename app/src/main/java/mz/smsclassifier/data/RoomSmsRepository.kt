package mz.smsclassifier.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mz.smsclassifier.classifier.Classification
import mz.smsclassifier.classifier.ClassificationStore
import mz.smsclassifier.classifier.PendingSms
import mz.smsclassifier.sms.DUPLICATE_WINDOW_MILLIS
import mz.smsclassifier.sms.IncomingSms
import mz.smsclassifier.sms.SmsRepository
import mz.smsclassifier.sms.nearDuplicateOf

class RoomSmsRepository(
    private val dao: SmsDao,
) : SmsRepository, ClassificationStore {
    override val messages: Flow<List<IncomingSms>> =
        dao.observeAll().map { rows -> rows.map { it.toIncomingSms() } }

    override suspend fun addIncoming(sms: IncomingSms): Boolean {
        if (isNearDuplicate(sms)) return false
        dao.insert(sms.toEntity())
        return true
    }

    override suspend fun addOutgoing(sms: IncomingSms) {
        if (isNearDuplicate(sms)) return
        dao.insert(sms.copy(outgoing = true, unread = false).toEntity())
    }

    override suspend fun upsertAll(messages: List<IncomingSms>) {
        val fresh = mutableListOf<IncomingSms>()
        for (sms in messages) {
            if (isNearDuplicate(sms) || fresh.any { it.nearDuplicateOf(sms) }) continue
            fresh += sms
        }
        if (fresh.isNotEmpty()) dao.insertAll(fresh.map { it.toEntity() })
    }

    override suspend fun markRead(senders: Collection<String>) {
        if (senders.isEmpty()) return
        dao.markRead(senders)
    }

    private suspend fun isNearDuplicate(sms: IncomingSms): Boolean =
        dao.receivedBetween(
            sms.receivedAtMillis - DUPLICATE_WINDOW_MILLIS,
            sms.receivedAtMillis + DUPLICATE_WINDOW_MILLIS,
        ).any { sms.nearDuplicateOf(it.toIncomingSms()) }

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
