package com.account_bank.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

public class RequestHandler {

    public static HttpEntity<String> createRequest(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String input = mapper.writeValueAsString(obj);

        // set headers
        return createRequest(input);
    }

    public static HttpEntity<String> createRequest(String input) {
        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(input, headers);
    }

    public static CollectionType createCollectionType(Class<?> elementType) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.getTypeFactory()
                .constructCollectionType(List.class, elementType);
    }
}
