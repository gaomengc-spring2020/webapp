package com.mengchen.webapp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mengchen.webapp.entity.Bill;
import com.mengchen.webapp.entity.File;
import com.mengchen.webapp.service.BillService;
import com.mengchen.webapp.service.FileService;
import com.mengchen.webapp.service.UserService;
import com.mengchen.webapp.utils.ConvertJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/v1/bill")
public class FileRestController {

    BillService billService;
    FileService fileService;
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(Logger.class);

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

        //TODO: check if bill# exist

        try{
            if(!userService.findByEmail(auth.getName()).getId()
                    .equals(billService.findBill(bill_id).getOwner_id() )) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sry This bill is not belong to you.");
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("There is no such entity.");
        }

        Bill theBill = billService.findBill(bill_id);

        if(!(theBill.getAttachment() == null)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sry, Only one file per bill.");
        }

        File theFile = new File();
        theFile.setFileName(file.getName());
        theFile.setUrl("fileUri");

        fileService.storeFile(file,theBill);

        logger.info(">>>> POST file: (theBill) " + ConvertJSON.ConvertToJSON(theBill));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ConvertJSON.ConvertToJSON(theBill));
    }

    @GetMapping("{bill_id}/file/{file_id}")
    @ResponseBody
    public ResponseEntity<String> findFile(Authentication auth,
                                           @PathVariable String bill_id,
                                           @PathVariable String file_id) throws JsonProcessingException {
        //TODO: check if the file exist
        try{
            if(!userService.findByEmail(auth.getName()).getId()
                    .equals(billService.findBill(bill_id).getOwner_id() )) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sry This bill is not belong to you.");
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sry, There is no such Entity.");
        }

        File theFile = fileService.findFile(file_id);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ConvertJSON.ConvertToJSON(theFile));
    }

    @DeleteMapping("{bill_id}/file/{file_id}")
    @ResponseBody
    public ResponseEntity<String> deleteFile(Authentication auth,
                                             @PathVariable String bill_id,
                                             @PathVariable String file_id){

        try{
            if(!userService.findByEmail(auth.getName()).getId()
                    .equals(billService.findBill(bill_id).getOwner_id() )) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sry This bill is not belong to you.");
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sry, There is no such Entity.");
        }

////
        Bill theBill = billService.findBill(bill_id);

        theBill.setAttachment(null);
        billService.updateBill(theBill);
//
        fileService.deleteFile(file_id);



        return ResponseEntity.status(HttpStatus.OK).body("");

    }
}
