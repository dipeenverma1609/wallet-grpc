package com.betpawa.hiring.server;

import com.betpawa.hiring.CreditTxnServiceGrpc;
import com.betpawa.hiring.WalletCreditTxn;
import com.betpawa.hiring.bean.Currency;
import com.betpawa.hiring.bean.UserWalletInfo;
import com.betpawa.hiring.exceptions.InvalidRequestException;
import com.betpawa.hiring.exceptions.InvalidTransactionException;
import com.betpawa.hiring.service.WalletService;
import com.betpawa.hiring.service.WalletServiceImpl;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.betpawa.hiring.Util.isAmountValid;

public class WalletCreditTxnGrpcImpl extends CreditTxnServiceGrpc.CreditTxnServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(WalletCreditTxnGrpcImpl.class);

    private final WalletService walletService = new WalletServiceImpl();

    @Override
    public void transact(final WalletCreditTxn.CreditTxnRequest request,
                         final StreamObserver<WalletCreditTxn.CreditTxnResponse> responseObserver) {

        logger.info("Got wallet credit request :: {}", request);
        String responseString = null;
        try {
            validate(request);

            final Optional<UserWalletInfo> walletInfo = walletService.listWallets(request.getUser()).stream().filter(w -> request.getCurrency().equals(w.getCurrency())).findAny();
            if (walletInfo.isPresent()) {
                walletService.credit(walletInfo.get().getId(), request.getAmount());
                responseString = "ok";
            } else {
                responseString = "wallet not found";
            }

        } catch (InvalidRequestException e) {
            logger.error("Invalid request error :: [{}]", request, e);
            responseString = e.getMessage();
            //responseObserver.onError(e);

        } catch (Throwable e) {
            logger.error("Error during debit txn :: [{}]", request, e);
            responseString = "failed txn";
            responseObserver.onError(e);
            return;
        }

        final WalletCreditTxn.CreditTxnResponse response = WalletCreditTxn.CreditTxnResponse.newBuilder()
                .setResult(responseString)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private boolean validate(WalletCreditTxn.CreditTxnRequest request) {

        boolean isValid = true;

        final String currency = request.getCurrency();
        if (Currency.valueByName(currency) == null) {
            throw new InvalidRequestException("Unknown Currency");
        }

        if (!isAmountValid(request.getAmount())) {
            throw new InvalidRequestException("Invalid Amount");
        }

        return isValid;
    }
}
