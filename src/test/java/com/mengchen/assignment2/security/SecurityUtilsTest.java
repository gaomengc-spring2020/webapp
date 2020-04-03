package com.mengchen.assignment2.security;

import com.mengchen.webapp.entity.User;
import com.mengchen.webapp.security.SecurityUtils;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class SecurityUtilsTest {

//    @Test
//    public void testEncodeAndMatch() {
//        String plainText = RandomString.make(10);
//        String encoded = SecurityUtils.encode(plainText);
//        Assert.assertTrue(SecurityUtils.match(plainText, encoded));
//    }
//
//    @Test
//    public void testEncodeAndMismatch() {
//        String plainText = RandomString.make(10);
//        String encoded = SecurityUtils.encode(plainText);
//        Assert.assertFalse(SecurityUtils.match(RandomString.make(10), encoded));
//    }
//
//    @Test
//    public void testGetAuthTokenInJWTFormat() {
//        String randomFName = RandomString.make(10);
//        String randomLName = RandomString.make(10);
//        String randomEmail = RandomString.make(10);
//        String randomPassword = RandomString.make(10);
//        User testUser = new User(randomFName, randomLName, randomEmail, randomPassword);
//        Assert.assertTrue(SecurityUtils.getAuthToken(testUser).contains("Bearer "));
//    }
//
//    @Test
//    public void testGetEmailFromAuthToken() {
//        String randomFName = RandomString.make(10);
//        String randomLName = RandomString.make(10);
//        String randomEmail = RandomString.make(10);
//        String randomPassword = RandomString.make(10);
//        User testUser = new User(randomFName, randomLName, randomEmail, randomPassword);
//        String token = SecurityUtils.getAuthToken(testUser);
//        Assert.assertEquals(randomEmail, SecurityUtils.getUserEmailFromToken(token));
//    }
//
//    @Test
//    public void testGetIdFromAuthToken() {
//        String randomFName = RandomString.make(10);
//        String randomLName = RandomString.make(10);
//        String randomEmail = RandomString.make(10);
//        String randomPassword = RandomString.make(10);
//        String randomId = RandomString.make(10);
//        User testUser = new User(randomFName, randomLName, randomEmail, randomPassword);
//        testUser.setId(randomId);
//        String token = SecurityUtils.getAuthToken(testUser);
//        Assert.assertEquals(randomId, SecurityUtils.getUserIdFromToken(token));
//    }
}
