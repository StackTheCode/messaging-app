package com.rkchat.demo.dto;
import  lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private  String email;
}
