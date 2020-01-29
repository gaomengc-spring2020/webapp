package com.mengchen.webapp.service;

import com.mengchen.webapp.dao.UserDAO;
import com.mengchen.webapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@Service
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    @Override
    @Transactional
    public List<User> listAllUser() {
        return userDAO.listAllUser();
    }

    @Override
    @Transactional
    public User findByEmail(String theEmail) {
        return userDAO.findByEmail(theEmail);
    }

    @Override
    @Transactional
    public void createUser(User theUser) {
//        theUser.setCreatedTime(LocalDateTime.now().toString());
        userDAO.createUser(theUser);
    }

    @Override
    @Transactional
    public void updateUser(User theUser) {
        userDAO.updateUser(theUser);
    }

    @Override
    @Transactional
    public void deleteUser(String theUsername) {
        userDAO.deleteUser(theUsername);
    }

    @Override
    public String login(String username, String password) {
        return userDAO.login(username, password);
    }
}
