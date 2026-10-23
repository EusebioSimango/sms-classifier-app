package mz.smsclassifier.classifier

import kotlinx.coroutines.runBlocking
import mz.smsclassifier.sms.IncomingSms
import mz.smsclassifier.sms.SmsCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class ClassifyPendingSmsTest {
    private class FakeStore(rows: List<PendingSms>) : ClassificationStore {
        private val unclassified = rows.toMutableList()
        val saved = mutableMapOf<Long, Classification>()

        override suspend fun pendingClassification(modelVersion: String, limit: Int): List<PendingSms> =
            unclassified.filter { saved[it.id]?.modelVersion != modelVersion }.take(limit)

        override suspend fun saveClassification(id: Long, classification: Classification) {
            saved[id] = classification
        }
    }

    private val rows = listOf(
        PendingSms(1, IncomingSms("M-Pesa", "Recebeu 250 MT", 1L)),
        PendingSms(2, IncomingSms("Vodacom", "Saldo de dados", 2L)),
        PendingSms(3, IncomingSms("+258841234567", "ja cheguei", 3L)),
    )

    @Test
    fun savesModelResultsInBatches() = runBlocking {
        val store = FakeStore(rows)
        val classifier = SmsClassifier { texts ->
            texts.map { Classification(SmsCategory.PERSONAL, 0.9f, ModelInfo.VERSION) }
        }

        ClassifyPendingSms(store, classifier, batchSize = 2)()

        assertEquals(setOf(1L, 2L, 3L), store.saved.keys)
        assertEquals(ModelInfo.VERSION, store.saved.getValue(3).modelVersion)
    }

    @Test
    fun modelFailureFallsBackToHeuristic() = runBlocking {
        val store = FakeStore(rows)
        val failing = SmsClassifier { error("model unavailable") }

        ClassifyPendingSms(store, failing)()

        assertEquals(SmsCategory.MOBILE_WALLET, store.saved.getValue(1).category)
        assertEquals(SmsCategory.OPERATOR, store.saved.getValue(2).category)
        assertEquals(ModelInfo.HEURISTIC_VERSION, store.saved.getValue(1).modelVersion)
    }
}
