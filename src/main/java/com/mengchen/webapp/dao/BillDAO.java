package com.mengchen.webapp.dao;

import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BillDAO {

    // POST /v1/bill/
    Bill createBill(Bill thebill);

    // GET /v1/bills
    List<Bill> findAllBills(User theUser);

    // DELETE /v1/bill/{id}
    void deleteBill(String billID);

    // GET /v1/bill/{id}
    Bill findBill(String billID);

    // PUT /v1/bill/{id}
    void updateBill(Bill theBill);

}
