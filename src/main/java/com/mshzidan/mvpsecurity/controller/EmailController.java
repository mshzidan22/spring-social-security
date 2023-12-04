package com.mshzidan.mvpsecurity.controller;

import com.mshzidan.mvpsecurity.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailConfirmationService;
    @GetMapping("/verify/{code}")
    public ResponseEntity<String > validateEmail(@PathVariable String code){
        emailConfirmationService.ValidateEmail(code);
      return new ResponseEntity( "Email verified ...",HttpStatus.OK);
    }
}