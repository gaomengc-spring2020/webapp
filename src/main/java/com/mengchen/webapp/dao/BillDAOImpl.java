package com.mengchen.webapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import com.mengchen.webapp.entity.User;
import com.mengchen.webapp.properties.FileStorageProperties;
import com.mengchen.webapp.repository.BillRepository;
import com.mengchen.webapp.utils.ConvertJSON;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.mengchen.webapp.utils.StatsDCheckPoint.StatsDCheckPoint;

@Repository
public class BillDAOImpl implements BillDAO{

    private EntityManager entityManager;

    @Autowired
    FileStorageProperties fileStorageProperties;

    @Autowired
    FileDAO fileDAO;

    @Autowired
    BillRepository billRepository;

    private Logger logger = Logger.getLogger(Logger.class);

    @Autowired
    public BillDAOImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    // POST /v1/bill/
    @Override
    public Bill createBill(Bill theBill){

        long startTime = System.currentTimeMillis();
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.save(theBill);

        StatsDCheckPoint("database.query.saveBill",startTime);
        return theBill;

    }

    // GET /v1/bills
    @Override
    public List<Bill> findAllBills(User theUser){

        long startTime = System.currentTimeMillis();

        Session currentSession = entityManager.unwrap(Session.class);

        String ownerId = theUser.getId();
        Query<Bill> theQuery =
                currentSession.createQuery("from Bill where owner_id=:ownerId", Bill.class);
        theQuery.setParameter("ownerId", ownerId);
        StatsDCheckPoint("database.query.findAllBill",startTime);

        List<Bill> bills = theQuery.getResultList();

        return bills;
    }

    // DELETE /v1/bill/{id}
    @Override
    public void deleteBill(String billID){

        fileDAO.deleteFileInS3(findBill(billID).getAttachment().getId());
        long startTime = System.currentTimeMillis();

        billRepository.deleteById(billID);
        StatsDCheckPoint("database.query.deleteBill",startTime);

//        theQuery.executeUpdate();
    }

    // GET /v1/bill/{id}
    @Override
    public Bill findBill(String billID){
        long startTime = System.currentTimeMillis();

        Session currentSession = entityManager.unwrap(Session.class);

        Query<Bill> theQuery =
                currentSession.createQuery("from Bill where bill_id=:billID", Bill.class);
        theQuery.setParameter("billID", billID);

        Bill theBill = theQuery.uniqueResultOptional().orElse(null);
        StatsDCheckPoint("database.query.findBill",startTime);

        if(theBill == null ) return null;

        try {
            Logger.getLogger(logger.getClass()).info(">>>>>> BillDAO - findBill: " + ConvertJSON.ConvertToJSON(theBill));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

//        currentSession.evict(theBill);
        return theBill;
    }

    // PUT /v1/bill/{id}
    @Override
    public void updateBill(Bill theBill){
        long startTime = System.currentTimeMillis();

        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.update(theBill);
        StatsDCheckPoint("database.query.updateBill",startTime);

    }

    @Override
    public Bill uploadAttachment(Bill theBill) {
        long startTime = System.currentTimeMillis();

        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.update(theBill);
        StatsDCheckPoint("database.query.uploadAttachment",startTime);

        try {
            logger.info(">>>>>> billDAO uploadFile: " + ConvertJSON.ConvertToJSON(findBill(theBill.getBill_id())));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return findBill(theBill.getBill_id());
    }

}
