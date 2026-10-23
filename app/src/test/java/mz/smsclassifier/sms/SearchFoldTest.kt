package mz.smsclassifier.sms

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFoldTest {
    @Test
    fun containsFolded_ignoresAccentsAndCase() {
        assertTrue("Cecília".containsFolded("cecilia"))
        assertTrue("Promoção no M-Pesa".containsFolded("promocao"))
        assertTrue("841234567".containsFolded("84123"))
        assertFalse("Cecília".containsFolded("ana"))
    }
}