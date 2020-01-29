package com.mengchen.webapp.rest;

import com.mengchen.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/token")
    public String getToken(@RequestParam("username") final String username, @RequestParam("password") final String password){
        String token = userService.login(username, password);
        if(StringUtils.isEmpty(token)){
            return "no token found";
        }
        return token;
    }
}
