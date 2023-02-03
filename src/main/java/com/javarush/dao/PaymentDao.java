package com.javarush.dao;

import com.javarush.domain.Payment;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class PaymentDao extends GenericDao<Payment> {
    public PaymentDao(SessionFactory sessionFactory) {
        super(Payment.class, sessionFactory);
    }

    public Payment getPaymentByRentalId(Integer id) {
        Query<Payment> query = getCurrentSession().createQuery("select p from Payment p " +
                "where p.rental.id = :ID", Payment.class);
        query.setParameter("ID", id);
        return query.getSingleResult();
    }
}
