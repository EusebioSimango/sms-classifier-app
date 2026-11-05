package mz.smsclassifier.sms

import org.junit.Assert.assertEquals
import org.junit.Test

class SenderThreadsTest {
    @Test
    fun groupBySender_ordersThreadsAndMessagesByNewest() {
        val older = IncomingSms("Vodacom", "saldo", 1_000L)
        val newer = IncomingSms("Vodacom", "dados", 3_000L)
        val other = IncomingSms("VM", "promo", 2_000L)

        val threads = groupBySender(listOf(older, other, newer))

        assertEquals(2, threads.size)
        assertEquals("Vodacom", threads[0].sender)
        assertEquals(2, threads[0].count)
        assertEquals("dados", threads[0].lastMessage.body)
        assertEquals("VM", threads[1].sender)
    }

    @Test
    fun unreadCount_ignoresOutgoingAndReadMessages() {
        val thread = groupBySender(
            listOf(
                IncomingSms("Ana", "oi", 1L, unread = true),
                IncomingSms("Ana", "olá", 2L, unread = true),
                IncomingSms("Ana", "ok", 3L, outgoing = true, unread = true),
                IncomingSms("Ana", "lido", 4L),
            ),
        ).single()
        assertEquals(2, thread.unreadCount)
    }

    @Test
    fun groupBySender_mergesTheSameNumber() {
        val threads = groupBySender(
            listOf(
                IncomingSms("+258841234567", "um", 1_000L),
                IncomingSms("841234567", "dois", 2_000L),
            ),
        )
        assertEquals(1, threads.size)
        assertEquals(2, threads[0].count)
        assertEquals("841234567", threads[0].sender)
    }

    @Test
    fun collapseDuplicates_dropsTheSameTextInsideTheWindow() {
        val thread = groupBySender(
            listOf(
                IncomingSms("M-Pesa", "saldo", 1_000L, unread = true),
                IncomingSms("MPESA", "saldo", 20_000L),
            ),
        ).single()
        assertEquals(1, thread.count)
        assertEquals(true, thread.messages.single().unread)
    }

    @Test
    fun collapseDuplicates_keepsTheSameTextAfterTheWindow() {
        val thread = groupBySender(
            listOf(
                IncomingSms("MPESA", "saldo", 1_000L),
                IncomingSms("MPESA", "saldo", 40_000L),
            ),
        ).single()
        assertEquals(2, thread.count)
    }

    @Test
    fun asPreview_truncatesLongBody() {
        val body = "a".repeat(90)
        assertEquals("a".repeat(79) + "…", body.asPreview(80))
    }
}
