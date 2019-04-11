package com.betpawa.hiring.bean;

public enum Currency {
    USD(1),
    EUR(0.89f),
    GBP(0.77f);

    private float fxRate;

    Currency(float fxRate) {
        this.fxRate = fxRate;
    }

    public float getFxRate() {
        return fxRate;
    }

    public static Currency valueByName(String name) {
        for (Currency cur: Currency.values()) {
            if (name.toUpperCase().equals(cur.name())) return cur;
        }
        return null;
    }
}
