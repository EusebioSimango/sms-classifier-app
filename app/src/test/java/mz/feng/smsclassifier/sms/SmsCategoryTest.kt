package mz.feng.smsclassifier.sms

import org.junit.Assert.assertEquals
import org.junit.Test

class SmsCategoryTest {
    @Test
    fun categorize_walletAndOperator() {
        assertEquals(
            SmsCategory.MOBILE_WALLET,
            categorize(IncomingSms("M-Pesa", "Recebeu 250 MT", 1L)),
        )
        assertEquals(
            SmsCategory.OPERATOR,
            categorize(IncomingSms("Vodacom", "O seu codigo e 1234", 2L)),
        )
        assertEquals(
            SmsCategory.OPERATOR,
            categorize(IncomingSms("VM", "Saldo de dados", 3L)),
        )
    }

    @Test
    fun categorize_personalNumberGoesToPersonal() {
        assertEquals(
            SmsCategory.PERSONAL,
            categorize(IncomingSms("+258841234567", "ja cheguei", 4L)),
        )
    }
}
