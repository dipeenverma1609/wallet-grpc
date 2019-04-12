package com.betpawa.hiring.server;

import com.betpawa.hiring.*;
import com.betpawa.hiring.bean.Currency;
import com.betpawa.hiring.bean.UserWalletInfo;
import com.betpawa.hiring.dao.WalletUserInfoServiceImpl;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class WalletServerIntegrationTest {

    private WalletBalanceServiceGrpc.WalletBalanceServiceBlockingStub balanceStub;
    private CreditTxnServiceGrpc.CreditTxnServiceBlockingStub creditStub;
    private DebitTxnServiceGrpc.DebitTxnServiceBlockingStub debitStub;
    private UserWalletInfo user;

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Before
    public void setup() throws Exception {
        final String walletBalanceServerName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder
                .forName(walletBalanceServerName).directExecutor().addService(new WalletBalanceGrpcImpl()).build().start());
        balanceStub = WalletBalanceServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(walletBalanceServerName).directExecutor().build()));

        final String creditTxnServerName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder
                .forName(creditTxnServerName).directExecutor().addService(new WalletCreditTxnGrpcImpl()).build().start());
        creditStub = CreditTxnServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(creditTxnServerName).directExecutor().build()));

        final String debitTxnServerName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder
                .forName(debitTxnServerName).directExecutor().addService(new WalletDebitTxnGrpcImpl()).build().start());
        debitStub = DebitTxnServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(debitTxnServerName).directExecutor().build()));

        user = new UserWalletInfo.Builder().setCurrency(Currency.USD).setCreationDate(new Date()).setBalance(0).setUserId("testuser").build();
        WalletUserInfoServiceImpl.getInstance().addUser(user);

    }

    @Test
    public void testTransactions() {

        double zeroBalance = 0.0;
        WalletBalance.WalletBalanceRequest balanceRequest = WalletBalance.WalletBalanceRequest.newBuilder().setUser(user.getUserId()).build();
        WalletBalance.WalletBalanceResponse balanceResponse = balanceStub.transact(balanceRequest);
        assertEquals(zeroBalance+" USD", balanceResponse.getResult());

        double debitAmount = 100;
        WalletDebitTxn.DebitTxnRequest debitRequest = WalletDebitTxn.DebitTxnRequest.newBuilder().setUser(user.getUserId()).setAmount(debitAmount).setCurrency("USD").build();
        WalletDebitTxn.DebitTxnResponse debitResponse = debitStub.transact(debitRequest);
        assertEquals("insufficient funds", debitResponse.getResult());

        double creditAmount = 100;
        WalletCreditTxn.CreditTxnRequest creditRequest = WalletCreditTxn.CreditTxnRequest.newBuilder().setUser(user.getUserId()).setAmount(creditAmount).setCurrency("INR").build();
        WalletCreditTxn.CreditTxnResponse creditResponse = creditStub.transact(creditRequest);
        assertEquals("Unknown Currency", creditResponse.getResult());

        creditRequest = WalletCreditTxn.CreditTxnRequest.newBuilder().setUser(user.getUserId()).setAmount(creditAmount).setCurrency("USD").build();
        creditResponse = creditStub.transact(creditRequest);
        assertEquals("ok", creditResponse.getResult());

        balanceRequest = WalletBalance.WalletBalanceRequest.newBuilder().setUser(user.getUserId()).build();
        balanceResponse = balanceStub.transact(balanceRequest);
        assertEquals(creditAmount+" USD", balanceResponse.getResult());

        debitResponse = debitStub.transact(debitRequest);
        assertEquals("ok", debitResponse.getResult());

        balanceRequest = WalletBalance.WalletBalanceRequest.newBuilder().setUser(user.getUserId()).build();
        balanceResponse = balanceStub.transact(balanceRequest);
        assertEquals(zeroBalance+" USD", balanceResponse.getResult());
    }
}
