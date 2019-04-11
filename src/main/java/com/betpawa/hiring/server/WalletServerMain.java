package com.betpawa.hiring.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.betpawa.hiring.Constants.*;

public class WalletServerMain {

    private static final Logger logger = LoggerFactory.getLogger(WalletServerMain.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        Server createWalletUserServer = ServerBuilder.forPort(CREATE_WALLET_SERVICE_PORT).addService(new CreateWalletUserGrpcImpl()).build();
        Server walletBalanceServer = ServerBuilder.forPort(WALLET_BALANCE_SERVICE_PORT).addService(new WalletBalanceGrpcImpl()).build();
        Server walletCreditTxnServer = ServerBuilder.forPort(WALLET_CREDIT_SERVICE_PORT).addService(new WalletCreditTxnGrpcImpl()).build();
        Server walletDebitTxnServer = ServerBuilder.forPort(WALLET_DEBIT_SERVICE_PORT).addService(new WalletDebitTxnGrpcImpl()).build();

        createWalletUserServer.start();
        logger.info("Started create wallet server");
        walletBalanceServer.start();
        logger.info("Started wallet balance server");
        walletCreditTxnServer.start();
        logger.info("Started wallet credit txn server");
        walletDebitTxnServer.start();
        logger.info("Started wallet debit txn server");

        createWalletUserServer.awaitTermination();
        walletBalanceServer.awaitTermination();
        walletCreditTxnServer.awaitTermination();
        walletDebitTxnServer.awaitTermination();
    }
}
