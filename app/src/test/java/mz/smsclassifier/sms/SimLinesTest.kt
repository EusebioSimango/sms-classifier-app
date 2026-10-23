package mz.smsclassifier.sms

import org.junit.Assert.assertEquals
import org.junit.Test

class SimLinesTest {
    @Test
    fun label_usesSlotAndCarrier() {
        assertEquals("SIM 1 · Vodacom", SimLine(7, 0, "Vodacom").label())
        assertEquals("SIM 2", SimLine(8, 1, "").label())
    }
}
