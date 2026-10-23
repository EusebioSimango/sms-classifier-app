package mz.smsclassifier.ui

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import mz.smsclassifier.sms.IncomingSms

private val PT = Locale.forLanguageTag("pt-PT")

internal fun formatTimestamp(millis: Long): String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(millis))

internal fun formatClock(millis: Long): String =
    DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(millis))

private val SHORT_MONTHS = arrayOf(
    "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
    "Jul", "Ago", "Set", "Out", "Nov", "Dez",
)

internal fun formatListStamp(
    millis: Long,
    zone: ZoneId = ZoneId.systemDefault(),
    today: LocalDate = LocalDate.now(zone),
): String {
    val dateTime = Instant.ofEpochMilli(millis).atZone(zone)
    val day = dateTime.toLocalDate()
    return when (day) {
        today -> DateTimeFormatter.ofPattern("HH:mm").format(dateTime)
        today.minusDays(1) -> "Ontem"
        else -> "${day.dayOfMonth} ${SHORT_MONTHS[day.monthValue - 1]}"
    }
}

internal fun formatDayLabel(
    day: LocalDate,
    today: LocalDate = LocalDate.now(),
): String = when (day) {
    today -> "Hoje"
    today.minusDays(1) -> "Ontem"
    else -> {
        val pattern = if (day.year == today.year) "EEEE, d 'de' MMMM" else "EEEE, d 'de' MMMM 'de' yyyy"
        DateTimeFormatter.ofPattern(pattern, PT).format(day).replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(PT) else char.toString()
        }
    }
}

internal sealed interface ChatRow {
    data class Day(val label: String) : ChatRow
    data class Bubble(val sms: IncomingSms) : ChatRow
}

/**
 * Newest message first, for a list with reverseLayout.
 * The day label is placed after that day's bubbles, so it appears above them on screen.
 */
internal fun chatRows(
    messagesNewestFirst: List<IncomingSms>,
    zone: ZoneId = ZoneId.systemDefault(),
    today: LocalDate = LocalDate.now(zone),
): List<ChatRow> {
    val rows = mutableListOf<ChatRow>()
    var openDay: LocalDate? = null
    for (sms in messagesNewestFirst) {
        val day = Instant.ofEpochMilli(sms.receivedAtMillis).atZone(zone).toLocalDate()
        if (openDay != null && day != openDay) {
            rows += ChatRow.Day(formatDayLabel(openDay, today))
        }
        rows += ChatRow.Bubble(sms)
        openDay = day
    }
    if (openDay != null) rows += ChatRow.Day(formatDayLabel(openDay, today))
    return rows
}
