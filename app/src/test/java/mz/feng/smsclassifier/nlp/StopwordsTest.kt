package mz.feng.smsclassifier.nlp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StopwordsTest {
    @Test
    fun ptMz_containsCommonFunctionWords() {
        assertTrue(Stopwords.PT_MZ.containsAll(listOf("de", "para", "com", "que")))
    }

    @Test
    fun ptMz_keepsWordsThatSignalFraud() {
        listOf("nao", "nunca", "urgente", "aqui", "agora", "bloqueada").forEach {
            assertFalse("'$it' must not be a stopword", it in Stopwords.PT_MZ)
        }
    }

    @Test
    fun ptMz_entriesAreLowercaseWithoutAccents() {
        val normalizer = TextNormalizer()
        Stopwords.PT_MZ.forEach { assertEquals(it, normalizer.normalize(it)) }
    }

    @Test
    fun parse_skipsCommentsAndBlankLines() {
        assertEquals(setOf("de"), Stopwords.parse(sequenceOf("# nota", "", "  de  ")))
    }
}
