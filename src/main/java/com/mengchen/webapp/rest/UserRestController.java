package com.mengchen.webapp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mengchen.webapp.entity.User;
import com.mengchen.webapp.security.SecurityUtils;
import com.mengchen.webapp.service.UserService;
import com.mengchen.webapp.utils.ResponseFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

@Validated
@RestController
@RequestMapping("/v2")
public class UserRestController {

    private UserService userService;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    public UserRestController(UserService theUserService){
        this.userService = theUserService;
    }

    @GetMapping(value = "/users", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> getUsers() {

        List<User> listUser = userService.listAllUser();

        return ResponseEntity.status(HttpStatus.OK).body(listUser.toString());
    }

    // add mapping for GET /users/{email}

    @GetMapping("/user/self")
    @ResponseBody
    public ResponseEntity<String> getUser( Authentication auth) throws JsonProcessingException{


        User user = userService.findByEmail(auth.getName());

        String filter = filterPassword(user);
        return ResponseEntity.status(HttpStatus.OK).body(filter);
    }

    @PostMapping("/user")
    @ResponseBody
    public ResponseEntity<String> addUser(@RequestBody @Valid User theUser) throws JsonProcessingException{

//        // check if the email follow the rules

        logger.info(">>>>>> Details=" + theUser.toString());

        if(theUser.getFirstName() == null || theUser.getEmail() == null ||theUser.getLastName() == null || theUser.getPassword()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Luck of Attribute!");
        }
        // check if the email address already exist
        User user = userService.findByEmail(theUser.getEmail());

        if(user != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sry, This Email Address (Username) already exist!");
        }

        userService.createUser(theUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(filterPassword(theUser));
    }

    @PutMapping("/user/self")
    @ResponseBody
    public ResponseEntity<String> updateUser(@RequestBody @Valid User theUser,Authentication auth) throws JsonProcessingException{
        if(theUser.getId() != null
                || theUser.getCreatedTime() != null
                || theUser.getUpdateTime() != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You input some field that are not allowed to be modified.");
        }

        if(!theUser.getEmail().equals(auth.getName())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sry You cannot update other's info.");
        }

        if(!SecurityUtils.passwordPatternCorrect(theUser.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your password is not follow the rule.");
        }

        logger.info(">>>>>>>name: " + auth.getName());
        logger.info(">>>>>>>>principle: " + auth.getPrincipal());
        logger.info(">>>>>>>>autho: " + auth.getAuthorities());
        logger.info(">>>>>>>>Details: " + auth.getDetails());
        logger.info(">>>>>>>Credentials: " + auth.getCredentials());


        if(theUser.getFirstName() == null || theUser.getLastName() == null || theUser.getPassword()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Partial PUT is never RESTFUL!");
        }

        User updateUser = userService.findByEmail(auth.getName());

        updateUser.setFirstName(theUser.getFirstName());
        updateUser.setLastName(theUser.getLastName());
        updateUser.setPassword(theUser.getPassword());


        userService.updateUser(updateUser);

        String filter = filterPassword(updateUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(filter);
    }



    @DeleteMapping("/user/{email}")
    public String deleteUser(@PathVariable String email){
        User theUser = userService.findByEmail(email);

        if(theUser == null){
            throw new RuntimeException("There is no user under email : " + email);
        }

        userService.deleteUser(email);

        return "User " + theUser.getLastName() + " has been deleted";

    }

    private String filterPassword(Object result) throws JsonProcessingException{
        return ResponseFilter.filterOutFieldsFromResp(result, "UserEntity", "password");
    }
}
