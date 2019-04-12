package com.betpawa.hiring.server;

import com.betpawa.hiring.WalletBalance;
import com.betpawa.hiring.WalletBalanceServiceGrpc;
import com.betpawa.hiring.bean.UserWalletInfo;
import com.betpawa.hiring.service.WalletService;
import com.betpawa.hiring.service.WalletServiceImpl;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WalletBalanceGrpcImpl extends WalletBalanceServiceGrpc.WalletBalanceServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(WalletBalanceGrpcImpl.class);

    private WalletService walletService;

    public WalletBalanceGrpcImpl() {
        this.walletService = new WalletServiceImpl();
    }

    public WalletBalanceGrpcImpl(WalletService walletService) {
        this.walletService = walletService;
    }

    @Override
    public void transact(final WalletBalance.WalletBalanceRequest request,
                         final StreamObserver<WalletBalance.WalletBalanceResponse> responseObserver) {

        logger.info("Got wallet balance request :: {}", request);
        String responseString = null;
        try {
            final List<UserWalletInfo> wallets = walletService.listWallets(request.getUser());

            if (!wallets.isEmpty()) {
                List<String> resStr = new ArrayList<>(wallets.size());
                for (UserWalletInfo wallet : wallets) {
                    final double balance = walletService.getWalletBalance(wallet.getId());
                    resStr.add(Double.toString(balance) + " " + wallet.getCurrency());
                }
                responseString = String.join(",", resStr);

            } else {
                responseString = "no wallets found";
            }
        } catch (Throwable e) {
            logger.error("Error getting for request :: [{}]", request, e);
            responseObserver.onError(e);
        }

        final WalletBalance.WalletBalanceResponse response = WalletBalance.WalletBalanceResponse.newBuilder()
                .setResult(responseString)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
