package com.betpawa.hiring.service;

import com.betpawa.hiring.bean.UserWalletInfo;
import com.betpawa.hiring.bean.WalletTransactionInfo;
import com.betpawa.hiring.dao.WalletTxnDataService;
import com.betpawa.hiring.dao.WalletUserInfoService;
import com.betpawa.hiring.exceptions.TransactionFailedException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WalletServiceImplTest {

    @Mock
    private WalletTxnDataService txnDataService = Mockito.mock(WalletTxnDataService.class);

    @Mock
    private WalletUserInfoService walletUserInfoService = Mockito.mock(WalletUserInfoService.class);

    @Test
    public void testListWalletInfo() {

        List<UserWalletInfo> expectedOutput = Arrays.asList(new UserWalletInfo());

        Mockito.when(walletUserInfoService.getAllUsers()).thenReturn(expectedOutput);

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);

        final List<UserWalletInfo> actualOutput = testClass.listWalletInfo();

        assertNotNull(actualOutput);
        assertEquals(expectedOutput, actualOutput);

    }

    @Test
    public void testListWallets() {

        String userid = "userid";

        List<UserWalletInfo> expectedOutput = Arrays.asList(new UserWalletInfo());

        Mockito.when(walletUserInfoService.getWallets(userid)).thenReturn(expectedOutput);

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);

        final List<UserWalletInfo> actualOutput = testClass.listWallets(userid);

        assertNotNull(actualOutput);
        assertEquals(expectedOutput, actualOutput);

    }

    @Test
    public void testGetWallet() {

        long walletid = 1;

        UserWalletInfo expectedOutput = new UserWalletInfo();

        Mockito.when(walletUserInfoService.getWallet(walletid)).thenReturn(expectedOutput);

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);

        final UserWalletInfo actualOutput = testClass.getWalletInfo(walletid);

        assertNotNull(actualOutput);
        assertEquals(expectedOutput, actualOutput);

    }

    @Test
    public void testGetWalletBalance() {

        long walletid = 1;
        double expectedOutput = 100;

        UserWalletInfo wallet = new UserWalletInfo();
        wallet.setBalance(expectedOutput);

        Mockito.when(walletUserInfoService.getWallet(walletid)).thenReturn(wallet);

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);

        final double actualOutput = testClass.getWalletBalance(walletid);

        assertTrue(expectedOutput == actualOutput);

    }

    @Test
    public void testGetWalletBalance_InvalidWalletId() {

        long walletid = 2;
        double expectedOutput = 0;

        UserWalletInfo wallet = new UserWalletInfo();
        wallet.setBalance(expectedOutput);

        Mockito.when(walletUserInfoService.getWallet(walletid)).thenReturn(null);

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);

        final double actualOutput = testClass.getWalletBalance(walletid);

        assertTrue(expectedOutput == actualOutput);

    }

    @Test
    public void testCredit() throws TransactionFailedException {

        long walletid = 1;
        double initialBalance = 100;
        double expectedNewBalance = 200;

        UserWalletInfo wallet = new UserWalletInfo();
        wallet.setBalance(initialBalance);

        Mockito.when(walletUserInfoService.getWallet(walletid)).thenReturn(wallet);
        Mockito.doNothing().when(txnDataService).addTransaction(Mockito.any(WalletTransactionInfo.class));
        Mockito.doNothing().when(walletUserInfoService).updateBalance(wallet);

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);

        double creditAmount = 100;
        testClass.credit(1, creditAmount);

        assertTrue(expectedNewBalance == initialBalance + creditAmount);
        Mockito.verify(txnDataService, Mockito.times(1)).addTransaction(Mockito.any(WalletTransactionInfo.class));
        Mockito.verify(walletUserInfoService, Mockito.times(1)).updateBalance(wallet);
    }

    @Test
    public void testCredit_TxnFailed() throws TransactionFailedException {

        long walletid = 1;
        double initialBalance = 100;
        double expectedNewBalance = 100;

        UserWalletInfo wallet = new UserWalletInfo();
        wallet.setBalance(initialBalance);

        Mockito.when(walletUserInfoService.getWallet(walletid)).thenReturn(wallet);
        Mockito.doThrow(new TransactionFailedException("")).when(txnDataService).addTransaction(Mockito.any(WalletTransactionInfo.class));

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);
        double creditAmount = 100;

        try {
            testClass.credit(1, creditAmount);
            fail("Expecting exception");
        } catch (Exception e) { }

        assertTrue(expectedNewBalance == initialBalance);
        Mockito.verify(txnDataService, Mockito.times(1)).addTransaction(Mockito.any(WalletTransactionInfo.class));
    }

    @Test
    public void testDebit() throws TransactionFailedException {

        long walletid = 1;
        double initialBalance = 200;
        double expectedNewBalance = 100;

        UserWalletInfo wallet = new UserWalletInfo();
        wallet.setBalance(initialBalance);

        Mockito.when(walletUserInfoService.getWallet(walletid)).thenReturn(wallet);
        Mockito.doNothing().when(txnDataService).addTransaction(Mockito.any(WalletTransactionInfo.class));
        Mockito.doNothing().when(walletUserInfoService).updateBalance(wallet);

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);

        double debitAmount = 100;
        testClass.debit(1, debitAmount);

        assertTrue(expectedNewBalance == initialBalance - debitAmount);
        Mockito.verify(txnDataService, Mockito.times(1)).addTransaction(Mockito.any(WalletTransactionInfo.class));
        Mockito.verify(walletUserInfoService, Mockito.times(1)).updateBalance(wallet);
    }

    @Test
    public void testDebit_TxnFailed() throws TransactionFailedException {

        long walletid = 1;
        double initialBalance = 100;
        double expectedNewBalance = 100;

        UserWalletInfo wallet = new UserWalletInfo();
        wallet.setBalance(initialBalance);

        Mockito.when(walletUserInfoService.getWallet(walletid)).thenReturn(wallet);
        Mockito.doThrow(new TransactionFailedException("")).when(txnDataService).addTransaction(Mockito.any(WalletTransactionInfo.class));

        WalletServiceImpl testClass = new WalletServiceImpl(txnDataService, walletUserInfoService);
        double debitAmount = 100;

        try {
            testClass.debit(1, debitAmount);
            fail("Expecting exception");
        } catch (Exception e) { }

        assertTrue(expectedNewBalance == initialBalance);
        Mockito.verify(txnDataService, Mockito.times(1)).addTransaction(Mockito.any(WalletTransactionInfo.class));
    }
}
