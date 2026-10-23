package mz.smsclassifier.data

import mz.smsclassifier.sms.IncomingSms
import mz.smsclassifier.sms.SmsCategory
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
    fun toEntity_leavesClassificationPending() {
        val entity = IncomingSms("VM", "ok", 10L).toEntity()
        assertEquals(null, entity.category)
        assertEquals(null, entity.confidence)
        assertEquals(null, entity.modelVersion)
    }

    @Test
    fun toIncomingSms_restoresStoredCategory() {
        val entity = SmsEntity(
            sender = "M-Pesa",
            body = "Recebeu 250 MT",
            receivedAtMillis = 1L,
            category = SmsCategory.MOBILE_WALLET.name,
            confidence = 0.4f,
            modelVersion = "sms-mz-2026.10.10",
        )
        val sms = entity.toIncomingSms()
        assertEquals(SmsCategory.MOBILE_WALLET, sms.category)
        assertEquals(0.4f, sms.confidence)
    }

    @Test
    fun toIncomingSms_unknownStoredCategoryIsNull() {
        val sms = SmsEntity(sender = "x", body = "y", receivedAtMillis = 1L, category = "REMOVED").toIncomingSms()
        assertEquals(null, sms.category)
    }
}
