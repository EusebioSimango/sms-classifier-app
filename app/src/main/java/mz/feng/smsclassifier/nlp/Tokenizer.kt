package mz.feng.smsclassifier.nlp

class Tokenizer(private val minLength: Int = 2) {
    fun tokenize(normalized: String): List<String> =
        normalized.split(' ').filter { it.length >= minLength }
}
