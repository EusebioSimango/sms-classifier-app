package mz.smsclassifier.sms

import java.text.Normalizer
import java.util.Locale

private val COMBINING_MARKS = Regex("\\p{Mn}+")

fun String.foldForSearch(): String =
    Normalizer.normalize(lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .replace(COMBINING_MARKS, "")

fun String.containsFolded(needle: String): Boolean =
    foldForSearch().contains(needle.foldForSearch())
