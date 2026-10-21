package mz.feng.smsclassifier.nlp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextPreprocessorTest {
    private val preprocessor = TextPreprocessor()

    @Test
    fun process_keepsNegation() {
        assertEquals(
            listOf("nao", "partilhe", "pin", "ninguem"),
            preprocessor.process("Não partilhe o seu PIN com ninguém"),
        )
    }

    @Test
    fun process_emptyOrPunctuationGivesNoTokens() {
        assertEquals(emptyList<String>(), preprocessor.process(""))
        assertEquals(emptyList<String>(), preprocessor.process("!!! ..."))
    }

    @Test
    fun process_matchesSharedCases() {
        val cases = loadCases()
        assertTrue(cases.isNotEmpty())
        cases.forEach { (input, expected) ->
            assertEquals("input: $input", expected, preprocessor.process(input))
        }
    }

    @Test
    fun process_thousandMessagesIsFast() {
        val messages = loadCases().map { it.first }
        val batch = List(1_000) { messages[it % messages.size] }
        batch.forEach(preprocessor::process)

        val started = System.nanoTime()
        batch.forEach(preprocessor::process)
        val elapsedMillis = (System.nanoTime() - started) / 1_000_000

        assertTrue("took ${elapsedMillis}ms", elapsedMillis < 1_000)
    }

    private fun loadCases(): List<Pair<String, List<String>>> {
        val stream = checkNotNull(javaClass.getResourceAsStream("/nlp/preprocessing_cases.tsv"))
        return stream.bufferedReader(Charsets.UTF_8).useLines { lines ->
            lines.filter { it.isNotBlank() && !it.startsWith("#") }
                .map { line ->
                    val (input, expected) = line.split('\t')
                    input to expected.split(' ').filter { it.isNotEmpty() }
                }
                .toList()
        }
    }
}
