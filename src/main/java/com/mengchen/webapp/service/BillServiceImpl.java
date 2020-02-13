package com.mengchen.webapp.service;

import com.mengchen.webapp.dao.BillDAO;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import com.mengchen.webapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Validated
@Service
public class BillServiceImpl implements BillService{

    private BillDAO billDAO;

    @Autowired
    public BillServiceImpl(BillDAO billDAO){
        this.billDAO = billDAO;
    }

    @Override
    @Transactional
    public Bill createBill(Bill theBill){
        return billDAO.createBill(theBill);
    }

    @Override
    @Transactional
    public List<Bill> findAllBills(User theUser){
        return billDAO.findAllBills(theUser);
    }

    @Override
    @Transactional
    public void deleteBill(String billID){
        billDAO.deleteBill(billID);
    }

    @Override
    @Transactional
    public Bill findBill(String billID){
        return billDAO.findBill(billID);
    }


    @Override
    @Transactional
    public void updateBill(Bill theBill){
        billDAO.updateBill(theBill);
    }

    @Override
    public Bill uploadAttachment(Bill theBill) {
        return billDAO.uploadAttachment(theBill);
    }
}
