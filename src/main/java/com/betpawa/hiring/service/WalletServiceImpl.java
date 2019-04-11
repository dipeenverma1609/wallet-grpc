package com.betpawa.hiring.service;

import com.betpawa.hiring.bean.TransactionType;
import com.betpawa.hiring.bean.UserWalletInfo;
import com.betpawa.hiring.bean.WalletTransactionInfo;
import com.betpawa.hiring.dao.WalletTxnDataService;
import com.betpawa.hiring.dao.WalletUserInfoService;
import com.betpawa.hiring.exceptions.InvalidTransactionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class WalletServiceImpl implements WalletService {

    private Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    private WalletTxnDataService txnDataService = WalletTxnDataService.getInstance();
    private WalletUserInfoService walletUserInfoService = WalletUserInfoService.getInstance();

    public WalletServiceImpl() {}

    public WalletServiceImpl(WalletTxnDataService txnDataService, WalletUserInfoService walletUserInfoService) {
        this.txnDataService = txnDataService;
        this.walletUserInfoService = walletUserInfoService;
    }

    @Override
    public List<UserWalletInfo> listWalletInfo() {
        return walletUserInfoService.getAllUsers();
    }

    public List<UserWalletInfo> listWallets(String userid) {
        return walletUserInfoService.getWallets(userid);
    }

    @Override
    public UserWalletInfo getWalletInfo(long id) {
        return walletUserInfoService.getWallet(id);
    }

    @Override
    public void credit(long id, double amount) {
        final UserWalletInfo user = walletUserInfoService.getWallet(id);
        if (user == null) {
            String msg = String.format("Wallet id %s doesn't exist", id);
            throw new IllegalArgumentException(msg);
        }

        final WalletTransactionInfo creditTxn = new WalletTransactionInfo.Builder().amount(amount).time(new Date())
                                                .txnType(TransactionType.CREDIT).userId(user).build();
        try {
            txnDataService.addTransaction(creditTxn);
            user.setBalance(user.getBalance() + amount);
            walletUserInfoService.updateBalance(user);

        } catch (Throwable e) {
            logger.error("Error in credit txn for wallet :: ", user, e);
            throw e;
        }
    }

    @Override
    public void debit(long id, double amount) throws InvalidTransactionException {
        final UserWalletInfo user = walletUserInfoService.getWallet(id);
        if (user == null) {
            String msg = String.format("Wallet id %s doesn't exist", id);
            throw new IllegalArgumentException(msg);
        }
        final double oldBalance = user.getBalance();
        if (oldBalance < amount) throw new InvalidTransactionException("insufficient funds");

        final WalletTransactionInfo debitTxn = new WalletTransactionInfo.Builder().amount(amount).time(new Date())
                                                .txnType(TransactionType.DEBIT).userId(user).build();
        try {
            txnDataService.addTransaction(debitTxn);
            user.setBalance(oldBalance - amount);
            walletUserInfoService.updateBalance(user);

        } catch (Throwable e) {
            logger.error("Error in debit txn for wallet :: ", user, e);
            throw e;
        }
    }

    @Override
    public double getWalletBalance(long id) {
        final UserWalletInfo wallet = walletUserInfoService.getWallet(id);
        if (wallet == null) return 0;

        return wallet.getBalance();
    }
}
