package mz.smsclassifier.nlp

import org.junit.Assert.assertEquals
import org.junit.Test

class TokenizerTest {
    private val tokenizer = Tokenizer()

    @Test
    fun tokenize_dropsSingleCharacters() {
        assertEquals(listOf("pacote", "__num__", "gb"), tokenizer.tokenize("pacote __num__ gb x"))
    }

    @Test
    fun tokenize_emptyInputGivesNoTokens() {
        assertEquals(emptyList<String>(), tokenizer.tokenize(""))
    }
}
