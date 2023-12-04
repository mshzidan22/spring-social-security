package com.mshzidan.mvpsecurity.filters;

import com.mshzidan.mvpsecurity.model.User;
import com.mshzidan.mvpsecurity.security.JwtUtil;
import com.mshzidan.mvpsecurity.security.UserAuthenticationToken;
import com.mshzidan.mvpsecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
public class AddSocialUsernameFillter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);
            email = jwtUtil.extractClaim(jwt,claims -> (String) claims.get("sub"));
        }

        User user = userService.getUserByEmail(email);
        SecurityContextHolder.getContext().setAuthentication(new UserAuthenticationToken(user));
        filterChain.doFilter(request,response);


    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
         return !request.getServletPath().equals("/add-outh2-username");
    }
}
