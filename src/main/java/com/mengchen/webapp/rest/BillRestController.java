package com.mengchen.webapp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.User;
import com.mengchen.webapp.security.SecurityUtils;
import com.mengchen.webapp.service.BillService;
import com.mengchen.webapp.service.UserService;
import com.mengchen.webapp.utils.ConvertJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/v1")
public class BillRestController {

    private BillService billService;
    private UserService userService;

    @Autowired
    public BillRestController(BillService billService, UserService userService){
        this.billService = billService;
        this.userService = userService;
    }

    @GetMapping("/bills")
    @ResponseBody
    public ResponseEntity<String> getBills(@RequestHeader (name="Authorization") String token){
        User theUser = userService.findByEmail(SecurityUtils.getUserEmailFromToken(token));
        List<Bill> theBills = billService.findAllBills(theUser);
        try{
            return ResponseEntity.status(HttpStatus.OK).body(ConvertJSON.ConvertToJSON(theBills));
        }catch (JsonProcessingException je){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(je.getMessage());
        }
    }

    @PostMapping("/bill")
    @ResponseBody
    public ResponseEntity<String> createBill(@RequestHeader (name="Authorization") String token,
                                             @RequestBody Bill theBill){

        if(theBill.getBill_id()!= null
            || theBill.getCreated_ts() != null
            || theBill.getUpdated_ts() != null
            || theBill.getOwner_id() != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You input some field that are not allowed to be modified.");
        }

        theBill.setOwner_id(SecurityUtils.getUserIdFromToken(token));

        billService.createBill(theBill);

        try{
            return ResponseEntity.status(HttpStatus.OK).body(ConvertJSON.ConvertToJSON(theBill));
        }catch (JsonProcessingException je){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(je.getMessage());
        }
    }

    @DeleteMapping("bill/{bill_id}")
    public ResponseEntity<String> deleteBill(@RequestHeader (name="Authorization") String token,
                                             @PathVariable String bill_id)  {


        Bill theBill = billService.findBill(bill_id);

        if(theBill == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no such bill");

        String userId = SecurityUtils.getUserIdFromToken(token);

        if(!theBill.getOwner_id().equals(userId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sorry you only delete bills belongs to you");
        }

        billService.deleteBill(bill_id);

        try{
            String response = "This Bill has been deleted : /n" + ConvertJSON.ConvertToJSON(theBill);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);

        }catch (JsonProcessingException je){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(je.getMessage());
        }

    }

    @GetMapping("/bill/{bill_id}")
    public ResponseEntity<String> getBill(@RequestHeader (name="Authorization") String token,
                                          @PathVariable String bill_id){
        Bill theBill = billService.findBill(bill_id);

        if(theBill == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no such bill");


        String userId = SecurityUtils.getUserIdFromToken(token);

        if(!theBill.getOwner_id().equals(userId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sorry you can only get bills belongs to you");
        }

        try{
            return ResponseEntity.status(HttpStatus.OK).body(ConvertJSON.ConvertToJSON(theBill));

        }catch (JsonProcessingException je){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(je.getMessage());
        }
    }

    @PutMapping("/bill/{bill_id}")
    @ResponseBody
    public ResponseEntity<String> updateBill(@RequestHeader (name="Authorization") String token,
                                           @PathVariable String bill_id,
                                             @RequestBody Bill theBill){

        Bill checkBill = billService.findBill(bill_id);
        if(checkBill == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no such Bill id");
        }
        if(!checkBill.getOwner_id().equals(SecurityUtils.getUserIdFromToken(token))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sry You cannot update other's bills' info.");
        }

//        if(!bill_id.equals(SecurityUtils.getUserIdFromToken(token))){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sorry you can only update bills belongs to you");
//        }

        if(theBill.getOwner_id() != null || theBill.getBill_id() != null
            || theBill.getCreated_ts() != null || theBill.getUpdated_ts() != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You input some field that are not allowed to be modified.");
        }

        if(theBill.getVendor() ==null || theBill.getBill_date() == null
            || theBill.getDue_date() == null || theBill.getAmount_due() < 0.01
            || theBill.getCategories() == null || theBill.getPayment_status() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Partial PUT is never RESTFUL!");
        }

        checkBill.setVendor(theBill.getVendor());
        checkBill.setBill_date(theBill.getBill_date());
        checkBill.setDue_date(theBill.getDue_date());
        checkBill.setAmount_due(theBill.getAmount_due());
        checkBill.setCategories(theBill.getCategories());
        checkBill.setPayment_status(theBill.getPayment_status());

        billService.updateBill(checkBill);
        try{
            return ResponseEntity.status(HttpStatus.OK).body(ConvertJSON.ConvertToJSON(checkBill));

        }catch (JsonProcessingException je){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(je.getMessage());
        }
    }


}
