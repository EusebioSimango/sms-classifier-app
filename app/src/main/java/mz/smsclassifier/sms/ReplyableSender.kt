package mz.smsclassifier.sms

private const val MIN_REPLY_DIGITS = 8

/** Phone numbers can be answered. Short codes and names such as Vodacom cannot. */
fun isReplyable(sender: String): Boolean {
    val trimmed = sender.trim()
    if (trimmed.isEmpty()) return false
    val withoutFormatting = trimmed.filterNot { it.isDigit() || it == '+' || it.isWhitespace() || it == '-' || it == '(' || it == ')' }
    if (withoutFormatting.isNotEmpty()) return false
    return trimmed.count { it.isDigit() } >= MIN_REPLY_DIGITS
}

/** Digits, with a leading plus when the sender had one. */
fun replyAddress(sender: String): String {
    val digits = sender.filter { it.isDigit() }
    return if (sender.trim().startsWith("+")) "+$digits" else digits
}

/** Last 9 digits, so +258841234567 and 841234567 refer to the same person. */
fun phoneKey(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    if (digits.length < 8) return ""
    return if (digits.length > 9) digits.takeLast(9) else digits
}
