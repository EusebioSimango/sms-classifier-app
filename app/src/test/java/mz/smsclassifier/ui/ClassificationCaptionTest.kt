package mz.smsclassifier.ui

import mz.smsclassifier.sms.IncomingSms
import mz.smsclassifier.sms.SmsCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class ClassificationCaptionTest {
    @Test
    fun caption_showsStoredCategoryAndPercent() {
        val sms = IncomingSms("M-Pesa", "Recebeu 250 MT", 1L, SmsCategory.MOBILE_WALLET, 0.624f)
        assertEquals("Carteira móvel · 62%", classificationCaption(sms))
    }

    @Test
    fun caption_withoutConfidenceIsProvisional() {
        val sms = IncomingSms("M-Pesa", "Recebeu 250 MT", 1L, SmsCategory.MOBILE_WALLET, null)
        assertEquals("Carteira móvel · provisória", classificationCaption(sms))
    }

    @Test
    fun caption_withoutCategoryUsesHeuristic() {
        val sms = IncomingSms("Vodacom", "Saldo de dados", 1L)
        assertEquals("Operadora · provisória", classificationCaption(sms))
    }
}
