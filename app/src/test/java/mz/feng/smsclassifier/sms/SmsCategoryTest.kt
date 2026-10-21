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
    fun fromModelLabel_mapsAllModelClasses() {
        val modelClasses = listOf(
            "Bancário", "Carteira móvel", "Jogos e apostas", "Mensagens suspeitas",
            "Operadora", "Outros", "Pessoal", "Publicidade",
        )
        assertEquals(
            SmsCategory.entries.toSet(),
            modelClasses.map { SmsCategory.fromModelLabel(it) }.toSet(),
        )
        assertEquals(SmsCategory.OTHER, SmsCategory.fromModelLabel("Desconhecida"))
    }

    @Test
    fun categorizeThread_prefersStoredCategory() {
        val thread = SenderThread(
            sender = "M-Pesa",
            messages = listOf(IncomingSms("M-Pesa", "Recebeu 250 MT", 1L, SmsCategory.SUSPICIOUS)),
        )
        assertEquals(SmsCategory.SUSPICIOUS, categorizeThread(thread))
    }

    @Test
    fun categorize_personalNumberGoesToPersonal() {
        assertEquals(
            SmsCategory.PERSONAL,
            categorize(IncomingSms("+258841234567", "ja cheguei", 4L)),
        )
    }
}
