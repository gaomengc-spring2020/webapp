package com.mengchen.webapp.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="authorities")
public class Authorities {

    @Id
    @Column(name = "username")
    @Email(message = "Sry, You must enter right email address to be your username!")
    private String userName;

    @Column(name = "authority")
    private String authority;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "Authorities{" +
                "userName='" + userName + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}
