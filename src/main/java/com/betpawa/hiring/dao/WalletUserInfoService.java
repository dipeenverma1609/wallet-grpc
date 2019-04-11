package com.betpawa.hiring.dao;

import com.betpawa.hiring.bean.UserWalletInfo;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WalletUserInfoService {

    private final Logger logger = LoggerFactory.getLogger(WalletUserInfoService.class);

    private static final WalletUserInfoService INSTANCE = new WalletUserInfoService();

    private SessionFactory factory;

    private WalletUserInfoService() {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            logger.error("Failed to create sessionFactory object.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static WalletUserInfoService getInstance() { return INSTANCE; }

    public void addUser(UserWalletInfo user) {
        final Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            session.save(user);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error adding a new user", e);
        } finally {
            if (session!=null) session.close();
        }
    }

    public List<UserWalletInfo> getAllUsers() {
        final Session session = factory.openSession();
        try {
            return session.createQuery("FROM UserWalletInfo").list();
        } finally {
            if (session!=null) session.close();
        }
    }

    public UserWalletInfo getWallet(long id) {
        final Session session = factory.openSession();
        try {
            final List<UserWalletInfo> walletInfo = session.createQuery("FROM UserWalletInfo as w where w.id = :id")
                    .setParameter("id", id).list();
            return walletInfo.isEmpty() ? null : walletInfo.get(0);
        } finally {
            if (session!=null) session.close();
        }
    }

    public List<UserWalletInfo> getWallets(String userid) {
        final Session session = factory.openSession();
        try {
            final List<UserWalletInfo> walletInfo = session.createQuery("FROM UserWalletInfo as w where w.userId = :userid")
                    .setParameter("userid", userid).list();
            return walletInfo;
        } finally {
            if (session!=null) session.close();
        }
    }

    public void updateBalance(UserWalletInfo walletInfo) {
        final Session session = factory.openSession();
        Transaction tx = null;
        try {
            final UserWalletInfo user = getWallet(walletInfo.getId());
            tx = session.beginTransaction();
            user.setBalance(walletInfo.getBalance());
            session.update(user);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error updating balance user", e);
        } finally {
            if (session!=null) session.close();
        }
    }

}
