package com.mengchen.webapp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import com.mengchen.webapp.entity.User;
import com.mengchen.webapp.security.SecurityUtils;
import com.mengchen.webapp.service.BillService;
import com.mengchen.webapp.service.UserService;
import com.mengchen.webapp.utils.ConvertJSON;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Validated
@RestController
@ComponentScan(basePackages = "com.mengchen.webapp")
@RequestMapping("/v1")
public class BillRestController {

    private BillService billService;
    private UserService userService;
    private final static Logger logger = LoggerFactory.getLogger(BillRestController.class);

    @Autowired
    private StatsDClient statsDClient;


    @Autowired
    public BillRestController(BillService billService, UserService userService){
        this.billService = billService;
        this.userService = userService;
    }

    @GetMapping("/bills")
    @ResponseBody
    public ResponseEntity<String> getBills(Authentication auth){
        statsDClient.incrementCounter("endpoint.bill.http.getAll");
        User theUser = userService.findByEmail(auth.getName());
        List<Bill> theBills = billService.findAllBills(theUser);
        logger.info(">>>>>>> GET USR : " + "Get all users");
        try{

            return ResponseEntity.status(HttpStatus.OK).body(ConvertJSON.ConvertToJSON(theBills));
        }catch (JsonProcessingException je){
            logger.error("endpoint.bill.http.getAll - INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(je.getMessage());
        }
    }

    @PostMapping("/bill")
    @ResponseBody
    public ResponseEntity<String> createBill(Authentication auth,
                                             @RequestBody Bill theBill){
        statsDClient.incrementCounter("endpoint.bill.http.post");
        if(theBill.getBill_id()!= null
            || theBill.getCreated_ts() != null
            || theBill.getUpdated_ts() != null
            || theBill.getOwner_id() != null){
            logger.error("endpoint.bill.http.post - HttpStatus.BAD_REQUEST");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You input some field that are not allowed to be modified.");
        }

        theBill.setOwner_id(userService.findByEmail(auth.getName()).getId());

        billService.createBill(theBill);

        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(8125);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
        try {
            serverSocket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sentence = new String(receivePacket.getData());
        System.out.println("RECEIVED: " + sentence);

        try{
            logger.info(">>>>>>> CREATE USR : " + "bill created");
            return ResponseEntity.status(HttpStatus.OK).body(ConvertJSON.ConvertToJSON(theBill));

        }catch (JsonProcessingException je){
            logger.error("endpoint.bill.http.post - HttpStatus.INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(je.getMessage());
        }



    }

    @DeleteMapping("bill/{bill_id}")
    public ResponseEntity<String> deleteBill(Authentication auth,
                                             @PathVariable String bill_id)  {

        statsDClient.incrementCounter("endpoint.bill.http.delete");

        Bill theBill = billService.findBill(bill_id);

        if(theBill == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no such bill");

        String userId = userService.findByEmail(auth.getName()).getId();

        if(!theBill.getOwner_id().equals(userId)){
            logger.error("endpoint.bill.http.delete - HttpStatus.UNAUTHORIZED");
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
    public ResponseEntity<String> getBill(Authentication auth,
                                          @PathVariable String bill_id){
        statsDClient.incrementCounter("endpoint.bill.http.get");

        Bill theBill = billService.findBill(bill_id);

        if(theBill == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no such bill");


        String userId = userService.findByEmail(auth.getName()).getId();

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
    public ResponseEntity<String> updateBill(Authentication auth,
                                           @PathVariable String bill_id,
                                             @RequestBody Bill theBill){
        statsDClient.incrementCounter("endpoint.bill.http.update");

        Bill checkBill = billService.findBill(bill_id);
        if(checkBill == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no such Bill id");
        }
        if(!checkBill.getOwner_id().equals(userService.findByEmail(auth.getName()).getId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sry You cannot update other's bills' info.");
        }

        if(theBill.getOwner_id() != null || theBill.getBill_id() != null
            || theBill.getCreated_ts() != null || theBill.getUpdated_ts() != null
        || theBill.getAttachment() != null){
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
