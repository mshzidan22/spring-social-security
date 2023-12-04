package com.mshzidan.mvpsecurity.controller;

import com.mshzidan.mvpsecurity.dto.JwtOnlyRespose;
import com.mshzidan.mvpsecurity.dto.SocialUsernameDto;
import com.mshzidan.mvpsecurity.model.User;
import com.mshzidan.mvpsecurity.security.JwtUtil;
import com.mshzidan.mvpsecurity.dto.LoginDto;
import com.mshzidan.mvpsecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class Mycontorller {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;



    @GetMapping("/")
    public String home(Authentication authentication)
    {
        User user = (User) authentication.getPrincipal();
        return  "Welcome to the Home page   "  + user.getUsername();

    }


    @GetMapping("/private")
    public String a(){
        return "this should be private";
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody User user ){
        userService.signUp(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody LoginDto loginDto){
        System.out.println(loginDto.getUsername());

        Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername() , loginDto.getPassword()));

        User user = (User) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(authentication);
         return new ResponseEntity<>(jwt, HttpStatus.OK);


    }


    @PostMapping("/add-outh2-username")
    public ResponseEntity<JwtOnlyRespose> addOuth2username(@RequestBody SocialUsernameDto socialUsernameDto) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user =userService.addOuth2UserName(user, socialUsernameDto.getUsername());

         String jwt = jwtUtil.generateToken(user);
         return new ResponseEntity<>(new JwtOnlyRespose(jwt),HttpStatus.OK);

    }

    @GetMapping("/leet")
    public String leetcode(Authentication authentication){

        System.out.println(authentication.getName());
        return authentication.getName();
    }



}

