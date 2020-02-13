package com.mengchen.webapp.dao;

import com.mengchen.webapp.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserDAO {

    List<User> listAllUser();

    User findByEmail(String theEmail);

    void createUser(User theUser);

    void updateUser(User theUser);

    void deleteUser(String theEmail);

    String login(String username, String password);
}
