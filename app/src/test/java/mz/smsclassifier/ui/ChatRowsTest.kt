package mz.smsclassifier.ui

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import mz.smsclassifier.sms.IncomingSms
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRowsTest {
    private val zone = ZoneId.of("Africa/Maputo")
    private val today = LocalDate.of(2026, 10, 23)

    @Test
    fun formatDayLabel_recentAndDistant() {
        assertEquals("Hoje", formatDayLabel(today, today))
        assertEquals("Ontem", formatDayLabel(today.minusDays(1), today))
        val distant = formatDayLabel(LocalDate.of(2026, 3, 30), today)
        assertEquals("Segunda-feira, 30 de março", distant)
        val otherYear = formatDayLabel(LocalDate.of(2025, 3, 28), today)
        assertTrue(otherYear.startsWith("Sexta-feira, 28 de março de 2025"))
        assertFalse(formatDayLabel(LocalDate.of(2026, 3, 30), today).contains("2026"))
    }

    @Test
    fun chatRows_dayLabelFollowsThatDaysBubblesInTheReversedList() {
        val rows = chatRows(
            messagesNewestFirst = listOf(
                sms("hoje tarde", today.atTime(14, 56)),
                sms("hoje manha", today.atTime(9, 15)),
                sms("ontem", today.minusDays(1).atTime(21, 40)),
            ),
            zone = zone,
            today = today,
        )
        assertEquals(
            listOf("hoje tarde", "hoje manha", "Hoje", "ontem", "Ontem"),
            rows.map { row ->
                when (row) {
                    is ChatRow.Day -> row.label
                    is ChatRow.Bubble -> row.sms.body
                }
            },
        )
    }

    private fun sms(body: String, time: LocalDateTime) =
        IncomingSms("841234567", body, time.atZone(zone).toInstant().toEpochMilli())
}
