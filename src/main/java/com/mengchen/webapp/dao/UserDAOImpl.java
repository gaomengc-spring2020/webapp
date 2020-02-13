package com.mengchen.webapp.dao;

import com.mengchen.webapp.entity.Authorities;
import com.mengchen.webapp.entity.User;
import com.mengchen.webapp.entity.Users;
import com.mengchen.webapp.security.SecurityUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {


    private EntityManager entityManager;

    @Autowired
    public UserDAOImpl(EntityManager theEntityManager){
        entityManager = theEntityManager;
    }


    @Override
    public List<User> listAllUser() {

        // get the current hibernate session
        Session currentSession = entityManager.unwrap(Session.class);

        Query<User> theQuery =
                currentSession.createQuery("from User", User.class);

        List<User> users = theQuery.getResultList();

        return users;
    }

    @Override
    public User findByEmail(String theEmail) {

        Session currentSession = entityManager.unwrap(Session.class);

        Query<User> theQuery =
                currentSession.createQuery("from User where email=:theEmail", User.class);

        theQuery.setParameter("theEmail", theEmail);

        return theQuery.uniqueResultOptional().orElse(null);
    }

    @Override
    public void createUser(User theUser) {
        Session currentSession = entityManager.unwrap(Session.class);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Users users = new Users();
        users.setUserName(theUser.getEmail());
        users.setPassword("{bcrypt}" + encoder.encode(theUser.getPassword()));
        users.setEnabled(true);

        Authorities authorities = new Authorities();
        authorities.setUserName(theUser.getEmail());
        authorities.setAuthority("ROLE_USER");

        currentSession.save(theUser);
        currentSession.save(users);
        currentSession.save(authorities);
    }

    @Override
    public void updateUser(User theUser) {

        Session currentSession = entityManager.unwrap(Session.class);

        theUser.setPassword(SecurityUtils.encode(theUser.getPassword()));

        currentSession.update(theUser);
    }

    @Override
    public void deleteUser(String theEmail) {

        Session currentSession = entityManager.unwrap(Session.class);

        Query theQuery =
                currentSession.createQuery("delete from User where email=:theEmail");
        theQuery.setParameter("theEmail", theEmail);
        theQuery.executeUpdate();
    }

    @Override
    public String login(String username, String password) {
        User customer = findByEmail(username);
        if(customer != null && SecurityUtils.match(password, customer.getPassword())){
            return SecurityUtils.getAuthToken(customer);
        }
        return null;
    }

}
