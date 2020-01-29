package com.mengchen.webapp.dao;

import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class BillDAOImpl implements BillDAO{

    private EntityManager entityManager;

    @Autowired
    public BillDAOImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    // POST /v1/bill/
    @Override
    public Bill createBill(Bill theBill){

//        theBill.setCreated_ts(LocalDateTime.now().toString());
//        theBill.setUpdated_ts(theBill.getCreated_ts());

//        theBill.setCategories(theBill.getCategories().toString());
        Session currentSession = entityManager.unwrap(Session.class);

        currentSession.save(theBill);

        return theBill;

    }

    // GET /v1/bills
    @Override
    public List<Bill> findAllBills(User theUser){
        Session currentSession = entityManager.unwrap(Session.class);

        String ownerId = theUser.getId();
        Query<Bill> theQuery =
                currentSession.createQuery("from Bill where owner_id=:ownerId", Bill.class);
        theQuery.setParameter("ownerId", ownerId);

        List<Bill> bills = theQuery.getResultList();

        return bills;
    }

    // DELETE /v1/bill/{id}
    @Override
    public void deleteBill(String billID){
        Session currentSession = entityManager.unwrap(Session.class);

        Query theQuery =
                currentSession.createQuery("delete from Bill where bill_id=:billID");
        theQuery.setParameter("billID", billID);

        theQuery.executeUpdate();

    }

    // GET /v1/bill/{id}
    @Override
    public Bill findBill(String billID){

        Session currentSession = entityManager.unwrap(Session.class);

        Query<Bill> theQuery =
                currentSession.createQuery("from Bill where bill_id=:billID", Bill.class);
        theQuery.setParameter("billID", billID);

        Bill theBill = theQuery.uniqueResultOptional().orElse(null);
        if(theBill == null ) return null;

//        currentSession.evict(theBill);
        return theBill;
    }

    // PUT /v1/bill/{id}
    @Override
    public void updateBill(Bill theBill){

        Session currentSession = entityManager.unwrap(Session.class);

        currentSession.update(theBill);

    }


}
