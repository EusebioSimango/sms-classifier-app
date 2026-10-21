package mz.feng.smsclassifier.nlp

object Stopwords {
    private const val PT_MZ_RESOURCE = "/nlp/stopwords_pt_mz.txt"

    val PT_MZ: Set<String> by lazy { load(PT_MZ_RESOURCE) }

    fun parse(lines: Sequence<String>): Set<String> =
        lines.map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .toSet()

    private fun load(resource: String): Set<String> {
        val stream = checkNotNull(Stopwords::class.java.getResourceAsStream(resource)) {
            "Missing resource $resource"
        }
        return stream.bufferedReader(Charsets.UTF_8).useLines(::parse)
    }
}
