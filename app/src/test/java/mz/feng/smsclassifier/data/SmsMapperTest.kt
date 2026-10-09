package mz.feng.smsclassifier.data

import mz.feng.smsclassifier.sms.IncomingSms
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsMapperTest {
    @Test
    fun mapper_roundTripKeepsSenderBodyAndTime() {
        val sms = IncomingSms("Vodacom", "codigo 1234", 1_700_000_000_000L)
        val restored = sms.toEntity().toIncomingSms()

        assertTrue(sms.isSameAs(restored))
        assertEquals(sms.sender, restored.sender)
        assertEquals(sms.body, restored.body)
        assertEquals(sms.receivedAtMillis, restored.receivedAtMillis)
    }

    @Test
    fun toEntity_leavesCategoryEmptyForPhase2() {
        val entity = IncomingSms("VM", "ok", 10L).toEntity()
        assertEquals(null, entity.category)
        assertEquals(null, entity.confidence)
    }
}
