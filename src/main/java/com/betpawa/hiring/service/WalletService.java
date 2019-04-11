package com.betpawa.hiring.service;

import com.betpawa.hiring.bean.UserWalletInfo;
import com.betpawa.hiring.exceptions.InvalidTransactionException;

import java.util.List;

public interface WalletService {

    public List<UserWalletInfo> listWalletInfo();

    public List<UserWalletInfo> listWallets(String userid);

    public UserWalletInfo getWalletInfo(long id);

    public void credit(long id, double amount);

    public void debit(long id, double amount) throws InvalidTransactionException;

    public double getWalletBalance(long userid);

}
