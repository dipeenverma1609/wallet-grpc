package com.betpawa.hiring.bean;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_wallet_info", uniqueConstraints={@UniqueConstraint(columnNames={"id"})},
        indexes = {@Index(columnList = "userid", name = "userid_hidx")})
public class UserWalletInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length=11, nullable=false, unique=true)
    private long id;

    @Column(name = "userid", length=11, nullable=false)
    private String userId;

    @Column(name = "balance", length=10, nullable=false)
    private double balance;

    @Column(name = "currency", length=5, nullable=false)
    private String currency;

    @Column(name = "creation_date", length=20, nullable=false)
    private long creationDate;

    public UserWalletInfo() {}

    public UserWalletInfo(String userId, double balance, String currency, long creationDate) {
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.creationDate = creationDate;
    }

    public UserWalletInfo(long id, String userId, double balance, String currency, long creationDate) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getCreationDate() {
        return new Date(creationDate);
    }

    public String getCurrency() {
        return currency;
    }

    public  static class Builder {

        private String userId;
        private double balance;
        private Currency currency;
        private Date creationDate;

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setBalance(double balance) {
            this.balance = balance;
            return this;
        }

        public Builder setCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder setCreationDate(Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public UserWalletInfo build() {
            return new UserWalletInfo(userId, balance, currency.name(), creationDate.getTime());
        }
    }
}
