package com.mengchen.assignment2.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mengchen.webapp.entity.User;
import com.mengchen.webapp.utils.ResponseFilter;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


class ReponseFilterTest {

    String USER_FILTER_ID = "UserEntity";
    @Test
    void testFilterOutFieldsFromResp() throws JsonProcessingException {
        String randomFName = RandomString.make(10);
        String randomLName = RandomString.make(10);
        String randomEmail = RandomString.make(10);
        String randomPassword = RandomString.make(10);
        User testUser = new User(randomFName, randomLName, randomEmail, randomPassword);
        String filteredResult = ResponseFilter.filterOutFieldsFromResp(testUser, USER_FILTER_ID, "password");
        Assert.assertFalse(filteredResult.contains("password"));
    }
}