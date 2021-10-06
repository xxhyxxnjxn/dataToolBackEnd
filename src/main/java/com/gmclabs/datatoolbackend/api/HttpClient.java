package com.gmclabs.datatoolbackend.api;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class HttpClient {
    private static final String SERVER_URL = "http://221.168.32.248:5000/rate/1h";
    private final RestTemplate restTemplate;

    public HttpClient(){
        this.restTemplate = new RestTemplateBuilder().defaultHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
    }

    public String get(){
        URI uri = URI.create(SERVER_URL);
        try {
            return restTemplate.getForEntity(uri, String.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
