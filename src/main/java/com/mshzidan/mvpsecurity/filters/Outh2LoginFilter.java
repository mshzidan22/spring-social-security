package com.mshzidan.mvpsecurity.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mshzidan.mvpsecurity.dto.JwtOnlyRespose;
import com.mshzidan.mvpsecurity.model.User;
import com.mshzidan.mvpsecurity.security.JwtUtil;
import com.mshzidan.mvpsecurity.services.Outh2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class Outh2LoginFilter extends OncePerRequestFilter {
   @Autowired
    private Outh2UserService outh2UserService;

@Autowired
   private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken)){
            filterChain.doFilter(request,response);
        }
        //get email from authentication
        String email = outh2UserService.getEmail(authentication);
        User user = outh2UserService.getUser(email).orElseGet(()->outh2UserService.saveOuth2User(authentication));

        // user need to add his username
        if(user.getUsername() == null){
            String token = jwtUtil.generateTokenForAddingUsername(user);
            sendToken(response,token);
        }

          //user added his username return token
        else {
           String token =  jwtUtil.generateToken(user);
           sendToken(response,token);

        }


    }

    private void sendToken(HttpServletResponse response ,String token) throws IOException {
        response.resetBuffer();
        response.setStatus(HttpStatus.OK.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response.getOutputStream().print(new ObjectMapper().writeValueAsString(new JwtOnlyRespose(token)));
        response.flushBuffer();

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean isRequestContainingJWT = !request.getHeader("Authorization").isEmpty();
        return request.getServletPath().equals("/add-outh2-username") || isRequestContainingJWT;
    }





}
