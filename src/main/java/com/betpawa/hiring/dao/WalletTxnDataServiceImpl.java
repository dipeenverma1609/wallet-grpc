package com.betpawa.hiring.dao;

import com.betpawa.hiring.bean.WalletTransactionInfo;
import com.betpawa.hiring.exceptions.TransactionFailedException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WalletTxnDataServiceImpl implements WalletTxnDataService {

    private final Logger logger = LoggerFactory.getLogger(WalletTxnDataServiceImpl.class);

    private static final WalletTxnDataServiceImpl INSTANCE = new WalletTxnDataServiceImpl();

    public static WalletTxnDataService getInstance() { return INSTANCE; }

    private WalletTxnDataServiceImpl() {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            logger.error("Failed to create sessionFactory object.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static SessionFactory factory;

    public void addTransaction(WalletTransactionInfo txnInfo) throws TransactionFailedException{
        final Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            session.save(txnInfo);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error adding a new txn", e);
            throw new TransactionFailedException(e);
        } finally {
            if (session!=null) session.close();
        }
    }

    public List<WalletTransactionInfo> getAllTransactions(String walletId) {
        final Session session = factory.openSession();
        try {
            final List<WalletTransactionInfo> records = session
                .createQuery("FROM WalletTransactionInfo as w where w.walletid = :walletid")
                .setParameter("walletid", walletId).list();

            return records.stream().sorted(Comparator.comparing(WalletTransactionInfo::getTxnDate))
                .collect(Collectors.toList());
        } finally {
            if (session!=null) session.close();
        }
    }

}
