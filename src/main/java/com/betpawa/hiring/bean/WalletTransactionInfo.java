package com.betpawa.hiring.bean;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wallet_txn_info", uniqueConstraints={@UniqueConstraint(columnNames={"id"})},
        indexes={@Index(columnList = "walletid", name = "wallet_txn_hidx")})
public class WalletTransactionInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length=11, nullable=false, unique=true)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walletid", nullable = false)
    private UserWalletInfo walletid;

    @Column(name = "txn_type", length=10, nullable=false)
    private String txnType;

    @Column(name = "amount", nullable=false)
    private double amount;

    @Column(name = "txn_time", nullable=false)
    private long txnDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserWalletInfo getWalletid() {
        return walletid;
    }

    public void setWalletid(UserWalletInfo walletid) {
        this.walletid = walletid;
    }

    public TransactionType getTxnType() {
        return TransactionType.valueOf(txnType);
    }

    public void setTxnType(TransactionType txnType) {
        this.txnType = txnType.name();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTxnDate() {
        return new Date(txnDate);
    }

    public void setTxnDate(long txnDate) {
        this.txnDate = txnDate;
    }

    public static class Builder {

        private final WalletTransactionInfo txn = new WalletTransactionInfo();

        public Builder() {}

        public Builder txnId(long id) {
            txn.setId(id);
            return this;
        }
        public Builder amount(double amount) {
            txn.setAmount(amount);
            return this;
        }

        public Builder time(Date txnTime) {
            txn.setTxnDate(txnTime.getTime());
            return this;
        }

        public Builder userId(UserWalletInfo userId) {
            txn.setWalletid(userId);
            return this;
        }

        public Builder txnType(TransactionType type) {
            txn.setTxnType(type);
            return this;
        }

        public WalletTransactionInfo build() {
            return txn;
        }
    }
}
