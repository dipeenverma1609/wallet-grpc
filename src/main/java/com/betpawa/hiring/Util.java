package com.betpawa.hiring;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Util {

    public static final boolean isAmountValid(Double amount) {
        boolean isValid = true;

        if (amount == null || amount.isNaN()) isValid &= false;

        if (isValid) {
            isValid &= BigDecimal.valueOf(amount).scale() <= 2;
        }

        return isValid;
    }
}
