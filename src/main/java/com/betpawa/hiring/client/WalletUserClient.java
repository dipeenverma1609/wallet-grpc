package com.betpawa.hiring.client;

import com.betpawa.hiring.CreateWalletUserServiceGrpc;
import com.betpawa.hiring.WalletUser;
import com.betpawa.hiring.bean.Currency;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.betpawa.hiring.Constants.CREATE_WALLET_SERVICE_PORT;

public class WalletUserClient {

    private Logger logger = LoggerFactory.getLogger(WalletUserClient.class);

    public List<String> createWalletUsers(int count) {
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", CREATE_WALLET_SERVICE_PORT)
                .usePlaintext()
                .build();
        final List<String> userIds = new ArrayList<>(count);
        try {

            final CreateWalletUserServiceGrpc.CreateWalletUserServiceBlockingStub stub = CreateWalletUserServiceGrpc.newBlockingStub(channel);
            int counter = 0;
            do {
                final String userid = RandomStringUtils.randomAlphanumeric(10);

                for (Currency currency : Currency.values()) {
                    final WalletUser.CreateWalletUserRequest request = WalletUser.CreateWalletUserRequest.newBuilder()
                            .setUserId(userid).setCurrency(currency.name()).build();

                    logger.info("creating wallet with currency {} for user {}", currency, userid);
                    final WalletUser.CreateWalletUserResponse response = stub.create(request);
                    logger.info("Response for creating wallet with currency {} for user {} :: [{}] ",
                            currency, userid, response.getResult());
                }

                userIds.add(userid);
                counter++;
            } while (count > counter);
        } finally {
            if(channel != null) channel.shutdown();
        }
        return userIds;
    }
}
