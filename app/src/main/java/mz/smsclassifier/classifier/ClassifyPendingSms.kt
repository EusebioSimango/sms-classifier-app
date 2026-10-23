package mz.smsclassifier.classifier

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mz.smsclassifier.sms.categorize

/**
 * Runs after the SMS is already stored, so a model failure never loses a message:
 * the batch falls back to the heuristic and is retried on the next run.
 */
class ClassifyPendingSms(
    private val store: ClassificationStore,
    private val classifier: SmsClassifier,
    private val modelVersion: String = ModelInfo.VERSION,
    private val batchSize: Int = 100,
) {
    private val mutex = Mutex()

    suspend operator fun invoke() = mutex.withLock {
        while (true) {
            val pending = store.pendingClassification(modelVersion, batchSize)
            if (pending.isEmpty()) return@withLock

            val results = runCatching { classifier.classify(pending.map { it.sms.body }) }
                .getOrNull()
                ?.takeIf { it.size == pending.size }

            if (results == null) {
                pending.forEach { store.saveClassification(it.id, heuristic(it)) }
                return@withLock
            }
            pending.zip(results).forEach { (row, result) -> store.saveClassification(row.id, result) }
        }
    }

    private fun heuristic(row: PendingSms) = Classification(
        category = categorize(row.sms),
        confidence = null,
        modelVersion = ModelInfo.HEURISTIC_VERSION,
    )
}
