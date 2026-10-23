package mz.smsclassifier.sms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReplyableSenderTest {
    @Test
    fun phoneNumbersAreReplyable() {
        assertTrue(isReplyable("+258841234567"))
        assertTrue(isReplyable("841234567"))
        assertTrue(isReplyable("+258 84 123 4567"))
    }

    @Test
    fun namesAndShortCodesAreNotReplyable() {
        assertFalse(isReplyable("Vodacom"))
        assertFalse(isReplyable("M-Pesa"))
        assertFalse(isReplyable("VM"))
        assertFalse(isReplyable("84123"))
        assertFalse(isReplyable(""))
    }

    @Test
    fun replyAddressKeepsCountryCode() {
        assertEquals("+258841234567", replyAddress("+258 84 123 4567"))
        assertEquals("841234567", replyAddress("84 123 4567"))
    }

    @Test
    fun phoneKey_treatsCountryCodeAndLocalNumberAsTheSamePerson() {
        assertEquals("841234567", phoneKey("+258 84 123 4567"))
        assertEquals(phoneKey("+258841234567"), phoneKey("841234567"))
        assertEquals("", phoneKey("Vodacom"))
    }

    @Test
    fun nameFor_matchesASavedContact() {
        val contacts = listOf(SavedContact("Ana", "+258841234567"))
        assertEquals("Ana", contacts.nameFor("84 123 4567"))
        assertNull(contacts.nameFor("Vodacom"))
        val withPhoto = listOf(SavedContact("Ana", "+258841234567", "content://contacts/1"))
        assertEquals("content://contacts/1", withPhoto.photoFor("841234567"))
        assertNull(contacts.photoFor("841234567"))
    }
}
