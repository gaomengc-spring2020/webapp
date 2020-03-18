package com.mengchen.webapp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import com.mengchen.webapp.service.BillService;
import com.mengchen.webapp.service.FileService;
import com.mengchen.webapp.service.UserService;
import com.mengchen.webapp.utils.ConvertJSON;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;


@Validated
@RestController
@RequestMapping("/v1/bill")
public class FileRestController {

    BillService billService;
    FileService fileService;
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(Logger.class);
    private static final String[] check = {"image/png","application/pdf","image/jpg", "image/jpeg"};

    @Autowired
    private StatsDClient statsDClient;

    @Autowired
    FileRestController(BillService billService, FileService fileService, UserService userService){
        this.userService = userService;
        this.billService = billService;
        this.fileService = fileService;
    }

    @PostMapping("/{bill_id}/file")
    @ResponseBody
    public ResponseEntity<String> attachFile(Authentication auth,
                                             @PathVariable String bill_id,
                                             @RequestParam("file") MultipartFile file) throws JsonProcessingException {
        long startTime = System.currentTimeMillis();

        try{
            if(!userService.findByEmail(auth.getName()).getId()
                    .equals(billService.findBill(bill_id).getOwner_id() )) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sry This bill is not belong to you.");
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("There is no such entity.");
        }

        String fileType = file.getContentType();
        if(!Arrays.asList(check).contains(fileType)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can only upload pdf, png, jpg or jpeg.");
        }
        logger.info(">>>>>> fileType: " + fileType);

        Bill theBill = billService.findBill(bill_id);

        if(!(theBill.getAttachment() == null)){
            theBill = billService.findBill(bill_id);
            File theFile = theBill.getAttachment();
            theBill.setAttachment(null);
            billService.updateBill(theBill);
            try{
                fileService.deleteFile(theFile.getId());
            }catch (IOException | NullPointerException ex){
                ex.printStackTrace();
            }
        }

        File theFile = new File();
        theFile.setFileName(file.getName());
        theFile.setUrl("fileUri");

        fileService.storeFile(file,theBill);

        logger.info(">>>> POST file: (theBill) " + ConvertJSON.ConvertToJSON(theBill));

        statsDClient.recordExecutionTimeToNow("endpoint.file.http.attachFile.Timer", startTime);
        statsDClient.incrementCounter("endpoint.file.http.attachFile");

        return ResponseEntity.status(HttpStatus.CREATED).body( ConvertJSON.ConvertToJSON(theBill));
    }

    @GetMapping("{bill_id}/file/{file_id}")
    @ResponseBody
    public ResponseEntity<String> findFile(Authentication auth,
                                           @PathVariable String bill_id,
                                           @PathVariable String file_id) throws JsonProcessingException {
        long startTime = System.currentTimeMillis();

        try{
            if(!userService.findByEmail(auth.getName()).getId()
                    .equals(billService.findBill(bill_id).getOwner_id() )) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sry This bill is not belong to you.");
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sry, There is no such Entity.");
        }

        File theFile = fileService.findFile(file_id);

        if(theFile == null ) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no such file");

        statsDClient.recordExecutionTimeToNow("endpoint.file.http.findFile.Timer", startTime);
        statsDClient.incrementCounter("endpoint.file.http.findFile");

        return ResponseEntity.status(HttpStatus.OK).body(ConvertJSON.ConvertToJSON(theFile));
    }

    @DeleteMapping("{bill_id}/file/{file_id}")
    @ResponseBody
    public ResponseEntity<String> deleteFile(Authentication auth,
                                             @PathVariable String bill_id,
                                             @PathVariable String file_id){
        long startTime = System.currentTimeMillis();
        try{
            if(!userService.findByEmail(auth.getName()).getId()
                    .equals(billService.findBill(bill_id).getOwner_id() )) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sry This bill is not belong to you.");
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sry, There is no such Entity.");
        }

        Bill theBill = billService.findBill(bill_id);

        theBill.setAttachment(null);
        billService.updateBill(theBill);
        try{
            fileService.deleteFile(file_id);
        }catch (NullPointerException | IOException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such file for this bill");
        }

        statsDClient.recordExecutionTimeToNow("endpoint.file.http.deleteFile.Timer", startTime);
        statsDClient.incrementCounter("endpoint.file.http.deleteFile");


        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");

    }

}
