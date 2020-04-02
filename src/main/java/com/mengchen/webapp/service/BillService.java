package com.mengchen.webapp.service;


import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.User;

import java.util.List;

public interface BillService {
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

    Bill uploadAttachment(Bill theBill);

    List<Bill> findAllDueBills(User theUser, int due_in);
}
