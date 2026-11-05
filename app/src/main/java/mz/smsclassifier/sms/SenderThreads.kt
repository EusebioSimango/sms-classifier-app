package mz.smsclassifier.sms

data class SenderThread(
    val sender: String,
    val messages: List<IncomingSms>,
    val senders: List<String> = listOf(sender),
) {
    val lastMessage: IncomingSms get() = messages.first()
    val count: Int get() = messages.size
    val unreadCount: Int get() = messages.count { it.unread && !it.outgoing }
}

const val DUPLICATE_WINDOW_MILLIS = 30_000L

fun IncomingSms.nearDuplicateOf(other: IncomingSms): Boolean =
    outgoing == other.outgoing &&
        partyKey(sender) == partyKey(other.sender) &&
        body.trim() == other.body.trim() &&
        kotlin.math.abs(receivedAtMillis - other.receivedAtMillis) <= DUPLICATE_WINDOW_MILLIS

fun collapseDuplicates(messages: List<IncomingSms>): List<IncomingSms> {
    val kept = mutableListOf<IncomingSms>()
    for (sms in messages.sortedByDescending { it.receivedAtMillis }) {
        val index = kept.indexOfFirst { it.nearDuplicateOf(sms) }
        if (index < 0) {
            kept += sms
        } else {
            val newer = kept[index]
            kept[index] = if (newer.category != null) {
                newer.copy(unread = newer.unread || sms.unread)
            } else {
                newer.copy(
                    category = sms.category,
                    confidence = sms.confidence,
                    unread = newer.unread || sms.unread,
                )
            }
        }
    }
    return kept
}

fun groupBySender(messages: List<IncomingSms>): List<SenderThread> =
    collapseDuplicates(messages)
        .groupBy { partyKey(it.sender) }
        .map { (_, group) ->
            val sorted = group.sortedByDescending { it.receivedAtMillis }
            SenderThread(
                sender = sorted.first().sender,
                messages = sorted,
                senders = sorted.map { it.sender }.distinct(),
            )
        }
        .sortedByDescending { it.lastMessage.receivedAtMillis }

fun String.asPreview(maxLength: Int = 80): String =
    if (length <= maxLength) this else take(maxLength - 1).trimEnd() + "…"
