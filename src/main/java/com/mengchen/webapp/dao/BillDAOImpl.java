package com.mengchen.webapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.User;
import com.mengchen.webapp.properties.FileStorageProperties;
import com.mengchen.webapp.repository.BillRepository;
import com.mengchen.webapp.utils.ConvertJSON;
import com.timgroup.statsd.StatsDClient;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BillDAOImpl implements BillDAO{

    private EntityManager entityManager;

    @Autowired
    FileStorageProperties fileStorageProperties;

    @Autowired
    FileDAO fileDAO;

    @Autowired
    BillRepository billRepository;

    @Autowired
    private StatsDClient statsDClient;

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
        statsDClient.recordExecutionTimeToNow("database.query.saveBill", startTime);

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
        statsDClient.recordExecutionTimeToNow("database.query.findAllBill", startTime);

        List<Bill> bills = theQuery.getResultList();

        return bills;
    }

    // DELETE /v1/bill/{id}
    @Override
    public void deleteBill(String billID){

        fileDAO.deleteFileInS3(findBill(billID).getAttachment().getId());
        long startTime = System.currentTimeMillis();

        billRepository.deleteById(billID);

        statsDClient.recordExecutionTimeToNow("database.query.deleteBill", startTime);


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
        statsDClient.recordExecutionTimeToNow("database.query.findBill", startTime);


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
        statsDClient.recordExecutionTimeToNow("database.query.updateBill", startTime);

    }

    @Override
    public Bill uploadAttachment(Bill theBill) {
        long startTime = System.currentTimeMillis();

        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.update(theBill);
        statsDClient.recordExecutionTimeToNow("database.query.uploadAttachment", startTime);

        try {
            logger.info(">>>>>> billDAO uploadFile: " + ConvertJSON.ConvertToJSON(findBill(theBill.getBill_id())));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return findBill(theBill.getBill_id());
    }

    @Override
    public List<Bill> findAllDueBills(User theUser, int due_in) {

        List<Bill> allBills = findAllBills(theUser);

        List<Bill> dueBills = new ArrayList<>();

        allBills.forEach(bill -> {
            LocalDate now = LocalDate.now();
            LocalDate dueDate = LocalDate.parse(bill.getDue_date());

            if(ChronoUnit.DAYS.between(dueDate,now) <= due_in){
                dueBills.add(bill);
            }
        });

        return dueBills;
    }

}
