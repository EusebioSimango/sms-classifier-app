package mz.feng.smsclassifier.sms

data class IncomingSms(
    val sender: String,
    val body: String,
    val receivedAtMillis: Long,
    val category: SmsCategory? = null,
    val confidence: Float? = null,
) {
    fun isSameAs(other: IncomingSms): Boolean =
        sender == other.sender &&
            body == other.body &&
            receivedAtMillis == other.receivedAtMillis
}
