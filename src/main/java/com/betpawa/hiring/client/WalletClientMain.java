package com.betpawa.hiring.client;

import com.betpawa.hiring.CreditTxnServiceGrpc;
import com.betpawa.hiring.DebitTxnServiceGrpc;
import com.betpawa.hiring.WalletBalance;
import com.betpawa.hiring.WalletBalanceServiceGrpc;
import com.betpawa.hiring.WalletCreditTxn;
import com.betpawa.hiring.WalletDebitTxn;
import com.betpawa.hiring.client.requests.TxnRequestRound;
import com.betpawa.hiring.CreditTxnServiceGrpc.CreditTxnServiceBlockingStub;
import com.betpawa.hiring.DebitTxnServiceGrpc.DebitTxnServiceBlockingStub;
import com.betpawa.hiring.WalletBalanceServiceGrpc.WalletBalanceServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import static com.betpawa.hiring.Constants.WALLET_BALANCE_SERVICE_PORT;
import static com.betpawa.hiring.Constants.WALLET_CREDIT_SERVICE_PORT;
import static com.betpawa.hiring.Constants.WALLET_DEBIT_SERVICE_PORT;

public class WalletClientMain {

    private static Logger logger = LoggerFactory.getLogger(WalletClientMain.class);

    private static ExecutorService es;
    private static ManagedChannel creditTxnchannel;
    private static ManagedChannel debitTxnchannel;
    private static ManagedChannel walletBalancechannel;

    public static void main(String[] args) {
        if (args.length != 3) {
            logger.error("Expecting 3 argument in CLI namely: <number of users> <number of threads per user> <number of round per thread>");
            System.exit(1);
        }

        int users = 0;
        int threadPerUser = 0;
        int roundsPerUser = 0;
        try {
            users = Integer.parseInt(args[0]);
            threadPerUser = Integer.parseInt(args[1]);
            roundsPerUser = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            logger.error("Error parsing integer arguments", e);
            System.exit(1);
        }

        if (users==0 || threadPerUser==0 || roundsPerUser==0) {
            logger.error("Exiting.... Passed arguments cannot be zero");
            System.exit(1);
        }

        try {
            new WalletClientMain().execute(users, threadPerUser, roundsPerUser);
        } catch (InterruptedException e) {
            logger.error("Error executing tasks", e);
        } finally {
            shutdown();
        }
    }

    private static void shutdown() {
        if (creditTxnchannel != null) creditTxnchannel.shutdown();
        if (debitTxnchannel != null) debitTxnchannel.shutdown();
        if (walletBalancechannel != null) walletBalancechannel.shutdown();
        if (es != null) es.shutdown();
    }

    private void execute(int users, int threadPerUser, int roundsPerThread) throws InterruptedException {
        es = Executors.newFixedThreadPool(users + users * threadPerUser);

        walletBalancechannel = ManagedChannelBuilder.forAddress("localhost", WALLET_BALANCE_SERVICE_PORT).usePlaintext().build();
        creditTxnchannel = ManagedChannelBuilder.forAddress("localhost", WALLET_CREDIT_SERVICE_PORT).usePlaintext().build();
        debitTxnchannel = ManagedChannelBuilder.forAddress("localhost", WALLET_DEBIT_SERVICE_PORT).usePlaintext().build();

        final List<String> walletUsers = new WalletUserClient().createWalletUsers(users);
        final CountDownLatch countDown = new CountDownLatch(walletUsers.size());
        for (String userid : walletUsers) {
            es.submit(new WalletServiceRequestor(userid, threadPerUser, roundsPerThread, countDown));
        }

        countDown.await();
    }

    class WalletServiceRequestor implements Runnable {

        private String userid;
        private int threadPerUser;
        private int roundsPerThread;
        private CountDownLatch countDown;

        public WalletServiceRequestor(String userid, int threadPerUser, int roundsPerThread, CountDownLatch countDown) {
            this.userid = userid;
            this.threadPerUser = threadPerUser;
            this.roundsPerThread =  roundsPerThread;
            this.countDown = countDown;
        }

        public void run() {
            final WalletBalanceServiceBlockingStub walletBalanceServiceStub = WalletBalanceServiceGrpc.newBlockingStub(walletBalancechannel);
            final CreditTxnServiceBlockingStub creditTxnServiceStub = CreditTxnServiceGrpc.newBlockingStub(creditTxnchannel);
            final DebitTxnServiceBlockingStub debitTxnServiceStub = DebitTxnServiceGrpc.newBlockingStub(debitTxnchannel);

            do {
                final TxnRequestRound[] requestRounds = TxnRequestRound.values();
                final int round = RandomUtils.nextInt(0, requestRounds.length - 1);

                final TxnRequestRound requestRound = requestRounds[round];
                logger.info("Executing {}", requestRound.name());
                final List requests = requestRound.getRequests(userid);

                final int requestsCount = requests.size();
                try {
                    int startIndex = 0;
                    int endIndex = 0;
                    do {
                        startIndex = endIndex;
                        endIndex = ((endIndex+threadPerUser) < requestsCount) ? (endIndex+threadPerUser) :  requestsCount;

                        final List reqList = requests.subList(startIndex, endIndex);
                        final CountDownLatch roundsCountDown = new CountDownLatch(reqList.size());

                        for(Object req : reqList) {
                            es.submit(() -> {
                                if (req instanceof WalletBalance.WalletBalanceRequest) {
                                    logger.info("Get Balance for user {}", userid);
                                    final WalletBalance.WalletBalanceResponse response = walletBalanceServiceStub.transact((WalletBalance.WalletBalanceRequest) req);
                                    logger.info(response.getResult());

                                } else
                                if (req instanceof WalletCreditTxn.CreditTxnRequest) {
                                    final WalletCreditTxn.CreditTxnRequest creditReq = (WalletCreditTxn.CreditTxnRequest) req;
                                    logger.info("Deposit {} {} for user {}", creditReq.getAmount(), creditReq.getCurrency(),  userid);
                                    final WalletCreditTxn.CreditTxnResponse response = creditTxnServiceStub.transact(creditReq);
                                    logger.info(response.getResult());

                                } else
                                if (req instanceof WalletDebitTxn.DebitTxnRequest) {
                                    final WalletDebitTxn.DebitTxnRequest debitReq = (WalletDebitTxn.DebitTxnRequest) req;
                                    logger.info("Withdraw {} {} for user {}", debitReq.getAmount(), debitReq.getCurrency(),  userid);
                                    final WalletDebitTxn.DebitTxnResponse response = debitTxnServiceStub.transact(debitReq);
                                    logger.info(response.getResult());

                                } else
                                    logger.error("Unknown request found, Ignoring :: {}", req);

                                roundsCountDown.countDown();
                            });
                        }

                        roundsCountDown.await();
                    } while(endIndex != requestsCount);

                } catch (InterruptedException e) {
                    logger.error("Interrupt error for requesting round {} for user {}", requestRound, userid, e);
                }

                roundsPerThread--;
            } while (roundsPerThread > 0);

            this.countDown.countDown();
        }
    }
}
