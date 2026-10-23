package mz.smsclassifier.sms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IncomingSmsNotifierTest {
    @Test
    fun sameParty_matchesCountryCodeAndLocalNumber() {
        assertTrue(sameParty("+258841234567", "841234567"))
        assertTrue(sameParty("Vodacom", "Vodacom"))
        assertFalse(sameParty("Vodacom", "841234567"))
        assertFalse(sameParty(null, "841234567"))
    }

    @Test
    fun notificationId_isStableAndNonNegative() {
        val first = notificationId("+258841234567", 1_700_000_000_000)
        assertEquals(first, notificationId("+258841234567", 1_700_000_000_000))
        assertTrue(first >= 0)
        assertTrue(first != notificationId("841234567", 1_700_000_000_000))
    }
}
