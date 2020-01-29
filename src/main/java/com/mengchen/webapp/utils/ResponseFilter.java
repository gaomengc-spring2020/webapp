package com.mengchen.webapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class ResponseFilter {

    public static String filterOutFieldsFromResp(Object response, String jsonFilter, String ...field) throws JsonProcessingException{
        FilterProvider filters = new SimpleFilterProvider()
                .setFailOnUnknownId(false)
                .addFilter(jsonFilter, SimpleBeanPropertyFilter.serializeAllExcept(field));
        ObjectMapper mapper = new ObjectMapper().setFilterProvider(filters);
        ObjectWriter responseWriter = mapper.writer();
        return responseWriter.writeValueAsString(response);
    }
}
