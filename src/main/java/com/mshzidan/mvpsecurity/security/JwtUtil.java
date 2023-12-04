package com.mshzidan.mvpsecurity.security;

import com.mshzidan.mvpsecurity.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtUtil {
    private String SECRET_KEY  = "Sha3boksha" ;
    public String generateToken (Authentication authentication){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims , authentication.getName());
    }

    public String generateTokenForAddingUsername (User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("provider" , user.getProvider().toString());
        claims.put("isEnabled",user.isEnabled());
        claims.put("message","Please Add your username");
        claims.put("link","/add-outh2-username");


        return createToken(claims , user.getEmail());
    }

    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims , user.getUsername());
    }


    private String createToken(Map<String ,Object> claims , String subject){

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *60 *24 * 60 ))  //60 days
                .signWith(SignatureAlgorithm.HS256 , SECRET_KEY)
                .compact();
    }

    public boolean validateToken (String token , String username){
        String tokenUsername = extractUsername(token);
        return (username.equals(tokenUsername) & !isTokenExpired(token));
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);

    }
    public Date extractExpiration(String token){
        return extractClaim(token , Claims::getExpiration);
    }

    public <T> T extractClaim(String token , Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

}
