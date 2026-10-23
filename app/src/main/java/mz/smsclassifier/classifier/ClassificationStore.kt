package mz.smsclassifier.classifier

import mz.smsclassifier.sms.IncomingSms

data class PendingSms(val id: Long, val sms: IncomingSms)

interface ClassificationStore {
    suspend fun pendingClassification(modelVersion: String, limit: Int): List<PendingSms>
    suspend fun saveClassification(id: Long, classification: Classification)
}
