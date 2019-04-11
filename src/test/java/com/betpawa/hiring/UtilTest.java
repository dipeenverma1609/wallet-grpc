package com.betpawa.hiring;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilTest {

    @Test
    public void testValidAmount() {
        assertFalse(Util.isAmountValid(null));
        assertTrue(Util.isAmountValid(Double.valueOf(1)));
        assertTrue(Util.isAmountValid(Double.valueOf("1.0")));
        assertFalse(Util.isAmountValid(Double.valueOf("13.323")));
        assertTrue(Util.isAmountValid(Double.valueOf("13.000")));
    }
}
