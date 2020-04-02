package com.mengchen.webapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertJSON {

    public static String ConvertToJSON(Object response) throws JsonProcessingException{
        ObjectMapper obj = new ObjectMapper();
        return obj.writeValueAsString(response);
    }
}
