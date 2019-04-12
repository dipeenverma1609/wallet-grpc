package com.betpawa.hiring.server;

import com.betpawa.hiring.CreateWalletUserServiceGrpc;
import com.betpawa.hiring.WalletUser;
import com.betpawa.hiring.bean.Currency;
import com.betpawa.hiring.bean.UserWalletInfo;
import com.betpawa.hiring.dao.WalletUserInfoService;
import com.betpawa.hiring.dao.WalletUserInfoServiceImpl;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class CreateWalletUserGrpcImpl extends CreateWalletUserServiceGrpc.CreateWalletUserServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(CreateWalletUserGrpcImpl.class);
    private WalletUserInfoService userInfoService ;

    public CreateWalletUserGrpcImpl() {
        this.userInfoService = WalletUserInfoServiceImpl.getInstance();
    }

    public CreateWalletUserGrpcImpl(WalletUserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public void create(WalletUser.CreateWalletUserRequest request, StreamObserver<WalletUser.CreateWalletUserResponse> responseObserver) {
        logger.info("Got wallet balance request :: {}", request);
        String responseString = null;

        final WalletUser.CreateWalletUserResponse.Builder responseBuilder = WalletUser.CreateWalletUserResponse.newBuilder();
        final Currency currency = Currency.valueByName(request.getCurrency());
        if (currency != null) {
            final UserWalletInfo userWallet = new UserWalletInfo.Builder().setUserId(request.getUserId())
                    .setBalance(0).setCreationDate(new Date()).setCurrency(currency).build();
            userInfoService.addUser(userWallet);
            responseString = "created user wallet";
            responseBuilder.setResult(responseString);
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } else {
            responseString = "Unsupported currency";
            responseBuilder.setResult(responseString);
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onError(new IllegalArgumentException(responseString));
        }
    }

}
