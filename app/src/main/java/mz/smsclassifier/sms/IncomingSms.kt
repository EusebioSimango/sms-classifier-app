package mz.smsclassifier.sms

data class IncomingSms(
    val sender: String,
    val body: String,
    val receivedAtMillis: Long,
    val category: SmsCategory? = null,
    val confidence: Float? = null,
    val outgoing: Boolean = false,
    val unread: Boolean = false,
) {
    fun isSameAs(other: IncomingSms): Boolean =
        sender == other.sender &&
            body == other.body &&
            receivedAtMillis == other.receivedAtMillis
}
