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

import static com.mengchen.webapp.utils.StatsDCheckPoint.StatsDCheckPoint;

@Repository
public class UserDAOImpl implements UserDAO {


    private EntityManager entityManager;

    @Autowired
    public UserDAOImpl(EntityManager theEntityManager){
        entityManager = theEntityManager;
    }

    @Override
    public List<User> listAllUser() {
        long startTime = System.currentTimeMillis();

        // get the current hibernate session
        Session currentSession = entityManager.unwrap(Session.class);

        Query<User> theQuery =
                currentSession.createQuery("from User", User.class);

        List<User> users = theQuery.getResultList();
        StatsDCheckPoint("database.query.findAllUser",startTime);

        return users;
    }

    @Override
    public User findByEmail(String theEmail) {
        long startTime = System.currentTimeMillis();

        Session currentSession = entityManager.unwrap(Session.class);

        Query<User> theQuery =
                currentSession.createQuery("from User where email=:theEmail", User.class);

        theQuery.setParameter("theEmail", theEmail);
        StatsDCheckPoint("database.query.findByEmail",startTime);

        return theQuery.uniqueResultOptional().orElse(null);
    }

    @Override
    public void createUser(User theUser) {
        long startTime = System.currentTimeMillis();
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
        StatsDCheckPoint("database.query.createUser",startTime);

    }

    @Override
    public void updateUser(User theUser) {
        long startTime = System.currentTimeMillis();

        Session currentSession = entityManager.unwrap(Session.class);

        theUser.setPassword(SecurityUtils.encode(theUser.getPassword()));

        currentSession.update(theUser);
        StatsDCheckPoint("database.query.updateUser",startTime);

    }

    @Override
    public void deleteUser(String theEmail) {
        long startTime = System.currentTimeMillis();

        Session currentSession = entityManager.unwrap(Session.class);

        Query theQuery =
                currentSession.createQuery("delete from User where email=:theEmail");
        theQuery.setParameter("theEmail", theEmail);
        theQuery.executeUpdate();
        StatsDCheckPoint("database.query.deleteUser",startTime);

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
