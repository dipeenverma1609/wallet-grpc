package com.betpawa.hiring.dao;

import com.betpawa.hiring.bean.WalletTransactionInfo;
import com.betpawa.hiring.exceptions.TransactionFailedException;

import java.util.List;

public interface WalletTxnDataService {

    public void addTransaction(WalletTransactionInfo txnInfo) throws TransactionFailedException;

    public List<WalletTransactionInfo> getAllTransactions(String walletId);

}
