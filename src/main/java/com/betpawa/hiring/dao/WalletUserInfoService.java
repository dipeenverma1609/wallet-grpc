package com.betpawa.hiring.dao;

import com.betpawa.hiring.bean.UserWalletInfo;

import java.util.List;

public interface WalletUserInfoService {

    public void addUser(UserWalletInfo user);

    public List<UserWalletInfo> getAllUsers();

    public UserWalletInfo getWallet(long id);

    public List<UserWalletInfo> getWallets(String userid);

    public void updateBalance(UserWalletInfo walletInfo);

}
