package com.betpawa.hiring.service;

import com.betpawa.hiring.bean.TransactionType;
import com.betpawa.hiring.bean.UserWalletInfo;
import com.betpawa.hiring.bean.WalletTransactionInfo;
import com.betpawa.hiring.dao.WalletTxnDataService;
import com.betpawa.hiring.dao.WalletTxnDataServiceImpl;
import com.betpawa.hiring.dao.WalletUserInfoService;
import com.betpawa.hiring.dao.WalletUserInfoServiceImpl;
import com.betpawa.hiring.exceptions.InvalidTransactionException;

import com.betpawa.hiring.exceptions.TransactionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WalletServiceImpl implements WalletService {

    private Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    private WalletTxnDataService txnDataService;
    private WalletUserInfoService walletUserInfoService;

    public WalletServiceImpl() {
        this.txnDataService = WalletTxnDataServiceImpl.getInstance();
        this.walletUserInfoService = WalletUserInfoServiceImpl.getInstance();
    }

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

        AtomicBoolean isTxnSuccess = new AtomicBoolean(false);
        try {
            txnDataService.addTransaction(creditTxn);
            isTxnSuccess.set(true);
            user.setBalance(user.getBalance() + amount);
            walletUserInfoService.updateBalance(user);

        } catch (TransactionFailedException e) {
            String msg= String.format("Deposit %s %s failed", amount, user.getCurrency());
            logger.error(msg, e);
            throw new InvalidTransactionException(msg, e);

        } catch (Throwable e) {
            try {
                if(isTxnSuccess.get()) {
                    final WalletTransactionInfo creditReverseTxn = new WalletTransactionInfo.Builder().amount(amount).time(new Date())
                            .txnType(TransactionType.DEBIT).userId(user).build();
                    txnDataService.addTransaction(creditReverseTxn);
                }
            } catch (TransactionFailedException e1) {
                logger.error("Error in reversing credit txn for wallet :: ", user, e1);
            }

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

        AtomicBoolean isTxnSuccess = new AtomicBoolean(false);
        try {
            txnDataService.addTransaction(debitTxn);
            isTxnSuccess.set(true);
            user.setBalance(oldBalance - amount);
            walletUserInfoService.updateBalance(user);

        } catch (TransactionFailedException e) {
            String msg= String.format("Withdraw %s %s failed", amount, user.getCurrency());
            logger.error(msg, e);
            throw new InvalidTransactionException(msg, e);

        } catch (Throwable e) {
            try {
                if (isTxnSuccess.get()) {
                    final WalletTransactionInfo debitReverseTxn = new WalletTransactionInfo.Builder().amount(amount).time(new Date())
                            .txnType(TransactionType.CREDIT).userId(user).build();
                    txnDataService.addTransaction(debitReverseTxn);
                }

            } catch (TransactionFailedException e1) {
                logger.error("Error in reversing debit txn for wallet :: ", user, e1);
            }

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
