package mz.smsclassifier.nlp

import java.text.Normalizer
import java.util.Locale

const val URL_TOKEN = "__url__"
const val MONEY_TOKEN = "__money__"
const val PHONE_TOKEN = "__phone__"
const val NUM_TOKEN = "__num__"

private val COMBINING_MARKS = Regex("\\p{Mn}+")
private val URL = Regex(
    "(?:https?://|www\\.)\\S+" +
        "|\\b[a-z0-9-]+(?:\\.[a-z0-9-]+)*\\.(?:com|net|org|info|ly|mz)\\b(?:/\\S*)?",
)
private const val AMOUNT = "(?:\\d{1,3}(?:[.,]\\d{3})+|\\d+)(?:[.,]\\d{1,2})?"
private val MONEY = Regex(
    "$AMOUNT\\s?(?:mt|mzn|meticais)\\b|\\b(?:mt|mzn)\\s?$AMOUNT",
)
private val PHONE = Regex("(?<!\\d)(?:\\+?258)?8[2-7]\\d{7}(?!\\d)")
private val NUMBER = Regex("\\d+(?:[.,]\\d+)*")
private val INNER_HYPHEN = Regex("(?<=\\p{L})-(?=\\p{L})")
private val NON_WORD = Regex("[^\\p{L}\\p{N}_]+")
private val SPACES = Regex("\\s+")

/**
 * Must stay identical to the Python preprocessing used to train the model,
 * otherwise inference sees different features than training did.
 */
class TextNormalizer {
    fun normalize(text: String): String {
        val folded = stripAccents(text.lowercase(Locale.ROOT))
        return folded
            .replace(URL, " $URL_TOKEN ")
            .replace(MONEY, " $MONEY_TOKEN ")
            .replace(PHONE, " $PHONE_TOKEN ")
            .replace(NUMBER, " $NUM_TOKEN ")
            .replace(INNER_HYPHEN, "")
            .replace(NON_WORD, " ")
            .replace(SPACES, " ")
            .trim()
    }

    private fun stripAccents(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD).replace(COMBINING_MARKS, "")
}
