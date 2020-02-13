package com.mengchen.webapp.entity;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.mengchen.webapp.security.SecurityUtils;
import com.mengchen.webapp.security.ValidPassword;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;

@Validated
@Entity
@DynamicUpdate
@SelectBeforeUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name= "user")
@JsonFilter("UserEntity")
public class User {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    @Email(message = "Sry, You must enter right email address to be your username!")
    @Valid
    private String email;
//
//    @Column(name = "username")
//    private String username;

    @ValidPassword
    @Column(name = "password")
    private String password;

    @Column(name = "account_created", updatable = false)
    private String createdTime;

    @Column(name = "account_updated")
    private String updateTime;

    public User(){

    }

    public User( String firstName, String lastName,
                String email, String password)  {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }

    @PrePersist
    public void prePersist() {
        password = SecurityUtils.encode(password);
        createdTime = LocalDateTime.now().toString();
        updateTime = LocalDateTime.now().toString();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now().toString();
    }
}
