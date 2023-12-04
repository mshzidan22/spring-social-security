package com.mshzidan.mvpsecurity.dto;

import lombok.Data;

@Data
public class JwtOnlyRespose {
    private String jwt;

    public JwtOnlyRespose(String jwt){
        this.jwt = jwt;
    }
}
