package mz.smsclassifier.sms

data class SenderThread(
    val sender: String,
    val messages: List<IncomingSms>,
) {
    val lastMessage: IncomingSms get() = messages.first()
    val count: Int get() = messages.size
    val unreadCount: Int get() = messages.count { it.unread && !it.outgoing }
}

fun groupBySender(messages: List<IncomingSms>): List<SenderThread> =
    messages
        .groupBy { it.sender }
        .map { (sender, group) ->
            SenderThread(
                sender = sender,
                messages = group.sortedByDescending { it.receivedAtMillis },
            )
        }
        .sortedByDescending { it.lastMessage.receivedAtMillis }

fun String.asPreview(maxLength: Int = 80): String =
    if (length <= maxLength) this else take(maxLength - 1).trimEnd() + "…"
