package mz.smsclassifier.nlp

import org.junit.Assert.assertEquals
import org.junit.Test

class TextNormalizerTest {
    private val normalizer = TextNormalizer()

    @Test
    fun normalize_lowercasesAndStripsAccents() {
        assertEquals("transferencia concluida", normalizer.normalize("TRANSFERÊNCIA Concluída!"))
    }

    @Test
    fun normalize_replacesUrls() {
        assertEquals("clique aqui __url__ ja", normalizer.normalize("Clique aqui: https://bit.ly/Ab12 já"))
        assertEquals("visite __url__ hoje", normalizer.normalize("Visite premio.co.mz/x hoje"))
        assertEquals("__url__", normalizer.normalize("www.loja.co.mz"))
    }

    @Test
    fun normalize_replacesMozambicanAmounts() {
        assertEquals("recebeu __money__", normalizer.normalize("Recebeu 1.500,00MT"))
        assertEquals("__money__", normalizer.normalize("MZN 250"))
        assertEquals("__money__", normalizer.normalize("500 meticais"))
    }

    @Test
    fun normalize_replacesMozambicanPhones() {
        assertEquals("__phone__", normalizer.normalize("+258841234567"))
        assertEquals("ligue __phone__", normalizer.normalize("Ligue 871234567"))
    }

    @Test
    fun normalize_replacesOtherNumbers() {
        assertEquals("codigo __num__", normalizer.normalize("Codigo 1234"))
        assertEquals("__num__", normalizer.normalize("123456789"))
    }

    @Test
    fun normalize_joinsHyphenatedBrandNames() {
        assertEquals("mpesa e emola", normalizer.normalize("M-Pesa e e-Mola"))
    }

    @Test
    fun normalize_blankOrPunctuationBecomesEmpty() {
        assertEquals("", normalizer.normalize(""))
        assertEquals("", normalizer.normalize("!!! ... ???"))
    }
}
