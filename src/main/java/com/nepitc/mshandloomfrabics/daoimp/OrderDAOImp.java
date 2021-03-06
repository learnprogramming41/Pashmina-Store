/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nepitc.mshandloomfrabics.daoimp;

import com.nepitc.mshandloomfrabics.dao.OrderDAO;
import com.nepitc.mshandloomfrabics.entity.OrderModel;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nishan Dhungana
 */
@Repository("orderDaoImp")
public class OrderDAOImp extends GenericDAOImp<OrderModel> implements OrderDAO{

    @Override
    public List<OrderModel> getOrderByUserId(int userId) {
        Session ses = sessionFactory.openSession();
        
        try {
            final String hql = "FROM OrderModel WHERE userId.userId =:userId";
            Query query = ses.createQuery(hql);
            query.setParameter("userId", userId);
            
            return query.list();
        } catch (HibernateException e) {
            throw new HibernateException(e.getMessage());
        } finally {
            if(ses.isOpen()){
                ses.close();
            }
        }
    }

    @Override
    public Long getPashminaCount() {
        Session ses = sessionFactory.openSession();
        
        try {
            final String hql = "SELECT COUNT(*) FROM OrderModel WHERE status=0";
            
            Query query = ses.createQuery(hql);
            return (Long) query.uniqueResult();
        } catch (HibernateException e) {
            throw new HibernateException(e.getMessage());
        } finally {
            if(ses.isOpen()){
                ses.close();
            }
        }
    }

    @Override
    public List<OrderModel> getAllOrders() {
        Session ses = sessionFactory.openSession();
        
        try {
            final String hql = "FROM OrderModel o WHERE status=:status ORDER BY o.orderDate DESC";
            Query query = ses.createQuery(hql);
            query.setParameter("status", "0");
            
            return query.list();
        } catch (HibernateException e) {
            throw new HibernateException(e.getMessage());
        } finally {
            if(ses.isOpen()){
                ses.close();
            }
        }
    }

    @Override
    public void updateOrderStatus(int orderId) {
        Session ses = sessionFactory.openSession();
        trans = ses.beginTransaction();
        
        try {
            final String hql = "UPDATE OrderModel SET status=:status WHERE orderId=:orderId";
            Query query = ses.createQuery(hql);
            query.setParameter("status", "1");
            query.setParameter("orderId", orderId);
            query.executeUpdate();
            
            trans.commit();
        } catch (HibernateException e) {
            trans.rollback();
            throw new HibernateException(e.getMessage());
        } finally {
            if(ses.isOpen()){
                ses.close();
            }
        }
    }

    @Override
    public List<OrderModel> confirmedOrders() {
        Session ses = sessionFactory.openSession();
        
        try {
            final String hql = "FROM OrderModel WHERE status =:status AND soldOutStatus=:soldStatus";
            Query query = ses.createQuery(hql);
            query.setParameter("status", "1");
            query.setParameter("soldStatus", "0");
            
            return query.list();
        } catch (HibernateException e) {
            throw new HibernateException(e.getMessage());
        } finally {
            if(ses.isOpen()){
                ses.close();
            }
        }
    }

    @Override
    public List<OrderModel> history() {
        Session ses = sessionFactory.openSession();
        
        try {
            final String hql = "FROM OrderModel WHERE soldOutStatus =:soldOutStatus";
            Query query = ses.createQuery(hql);
            query.setParameter("soldOutStatus", "1");
            
            return query.list();
        } catch (HibernateException e) {
            throw new HibernateException(e.getMessage());
        } finally {
            if(ses.isOpen()){
                ses.close();
            }
        }
    }

    @Override
    public void shippedItem(int orderId) {
        Session ses = sessionFactory.openSession();
        trans = ses.beginTransaction();
        
        try {
            final String hql = "UPDATE OrderModel SET soldOutStatus=:status, shippedDate=:shippedDate WHERE orderId=:orderId";
            Query query = ses.createQuery(hql);
            query.setParameter("status", "1");
            query.setParameter("shippedDate", new Date());
            query.setParameter("orderId", orderId);
            query.executeUpdate();
            
            trans.commit();
        } catch (HibernateException e) {
            trans.rollback();
            throw new HibernateException(e.getMessage());
        } finally {
            if(ses.isOpen()){
                ses.close();
            }
        }
    }
    
}
