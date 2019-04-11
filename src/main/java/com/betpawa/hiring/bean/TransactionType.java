package com.betpawa.hiring.bean;

public enum TransactionType {

    CREDIT("Deposit [0-9]+ [A-Z]+{3}"),
    DEBIT("Withdraw [0-9]+ [A-Z]+{3}"),
    BALANCE("Get Balance");

    private String regex;

    TransactionType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

}
