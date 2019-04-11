package com.betpawa.hiring.server;

import com.betpawa.hiring.DebitTxnServiceGrpc;
import com.betpawa.hiring.WalletDebitTxn;
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

public class WalletDebitTxnGrpcImpl extends DebitTxnServiceGrpc.DebitTxnServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(WalletDebitTxnGrpcImpl.class);

    private final WalletService walletService = new WalletServiceImpl();

    @Override
    public void transact(final WalletDebitTxn.DebitTxnRequest request,
                         final StreamObserver<WalletDebitTxn.DebitTxnResponse> responseObserver) {

        logger.info("Got wallet debit request :: {}", request);
        String responseString = null;
        try {
            validate(request);

            final Optional<UserWalletInfo> walletInfo = walletService.listWallets(request.getUser()).stream().filter(w -> request.getCurrency().equals(w.getCurrency())).findAny();
            if (walletInfo.isPresent()) {
                walletService.debit(walletInfo.get().getId(), request.getAmount());
                responseString = "ok";
            } else {
                responseString = "wallet not found";
            }

        } catch (InvalidRequestException e) {
            logger.error("Invalid request error :: [{}]", request, e);
            responseString = e.getMessage();
            //responseObserver.onError(e);

        } catch (InvalidTransactionException e) {
            logger.error("Invalid txn error :: [{}]", request, e);
            responseString = e.getMessage();
            //responseObserver.onError(e);

        } catch (Throwable e) {
            logger.error("Error during debit txn :: [{}]", request, e);
            responseObserver.onError(e);
            return;
        }

        final WalletDebitTxn.DebitTxnResponse response = WalletDebitTxn.DebitTxnResponse.newBuilder()
                .setResult(responseString)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private boolean validate(WalletDebitTxn.DebitTxnRequest request) {

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
