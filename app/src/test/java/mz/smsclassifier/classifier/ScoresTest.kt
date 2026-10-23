package mz.smsclassifier.classifier

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScoresTest {
    @Test
    fun softmax_sumsToOneAndKeepsArgmax() {
        val scores = floatArrayOf(-0.94f, -0.50f, -0.70f, 0.08f, -1.22f)
        val probabilities = softmax(scores)

        assertEquals(1f, probabilities.sum(), 1e-5f)
        assertEquals(scores.indices.maxBy { scores[it] }, probabilities.indices.maxBy { probabilities[it] })
    }

    @Test
    fun confidenceOf_equalScoresIsUniform() {
        assertEquals(0.25f, confidenceOf(FloatArray(4) { 1f })!!, 1e-6f)
    }

    @Test
    fun confidenceOf_emptyIsNull() {
        assertNull(confidenceOf(FloatArray(0)))
    }
}
