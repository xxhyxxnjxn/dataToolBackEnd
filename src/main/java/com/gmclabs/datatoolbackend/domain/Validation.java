package com.gmclabs.datatoolbackend.domain;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class Validation {

    public String invalidateBlank(String tdName){
        if(tdName.equals("")||tdName.length() == 0){
            Date today = new Date();
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return date.format(today);
        }
        return tdName;
    }
}
