package com.betpawa.hiring.client.requests;

import com.betpawa.hiring.WalletBalance;
import com.betpawa.hiring.WalletCreditTxn;
import com.betpawa.hiring.WalletDebitTxn;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum TxnRequestRound {

    ROUND_A(Arrays.asList(
        WalletCreditTxn.CreditTxnRequest.newBuilder().setAmount(100).setCurrency("USD"),
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(200).setCurrency("USD"),
        WalletCreditTxn.CreditTxnRequest.newBuilder().setAmount(100).setCurrency("EUR"),
        WalletBalance.WalletBalanceRequest.newBuilder(),
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(100).setCurrency("USD"),
        WalletBalance.WalletBalanceRequest.newBuilder(),
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(100).setCurrency("USD")
    )),
    ROUND_B(Arrays.asList(
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(100).setCurrency("GBP"),
        WalletCreditTxn.CreditTxnRequest.newBuilder().setAmount(300).setCurrency("GPB"),
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(100).setCurrency("GBP"),
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(100).setCurrency("GBP"),
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(100).setCurrency("GBP")
    )),
    ROUND_C(Arrays.asList(
        WalletBalance.WalletBalanceRequest.newBuilder(),
        WalletCreditTxn.CreditTxnRequest.newBuilder().setAmount(100).setCurrency("USD"),
        WalletCreditTxn.CreditTxnRequest.newBuilder().setAmount(100).setCurrency("USD"),
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(100).setCurrency("USD"),
        WalletCreditTxn.CreditTxnRequest.newBuilder().setAmount(100).setCurrency("USD"),
        WalletBalance.WalletBalanceRequest.newBuilder(),
        WalletDebitTxn.DebitTxnRequest.newBuilder().setAmount(200).setCurrency("USD"),
        WalletBalance.WalletBalanceRequest.newBuilder()
    ));

    private List<Object> requests;

    TxnRequestRound(List requests) {
        this.requests = requests;
    }

    public List getRequests(final String userId) {
        return requests.stream().map(b -> {
            if (b instanceof WalletBalance.WalletBalanceRequest.Builder) {
                return ((WalletBalance.WalletBalanceRequest.Builder) b).setUser(userId).build();
            }
            if (b instanceof WalletCreditTxn.CreditTxnRequest.Builder) {
                return ((WalletCreditTxn.CreditTxnRequest.Builder) b).setUser(userId).build();
            }
            if (b instanceof WalletDebitTxn.DebitTxnRequest.Builder) {
                return ((WalletDebitTxn.DebitTxnRequest.Builder) b).setUser(userId).build();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
