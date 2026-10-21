package mz.feng.smsclassifier.nlp

class TextPreprocessor(
    private val normalizer: TextNormalizer = TextNormalizer(),
    private val tokenizer: Tokenizer = Tokenizer(),
    private val stopwords: Set<String> = Stopwords.PT_MZ,
) {
    fun process(text: String): List<String> =
        tokenizer.tokenize(normalizer.normalize(text)).filterNot { it in stopwords }
}
