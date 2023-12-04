package com.mshzidan.mvpsecurity.services;

import com.mshzidan.mvpsecurity.model.Provider;
import com.mshzidan.mvpsecurity.model.Role;
import com.mshzidan.mvpsecurity.model.RoleName;
import com.mshzidan.mvpsecurity.model.User;
import com.mshzidan.mvpsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Outh2UserService {
    @Autowired
    private UserRepository userRepository;


    public User saveOuth2User(Authentication principal) {
        User user = new User();
        user.setEmail(getEmail(principal));
        String provider = getAuthorizedClientRegistrationId(principal);
        user.setProvider(Provider.valueOf(provider));
        user.setEnabled(true);
        Role role = new Role();
        role.setName(RoleName.USER);
        user.getRoles().add(role);
        return userRepository.save(user);
    }


    public Optional<User> getUser(String email){
        return userRepository.findByEmail(email);
    }


    public Map<String, Object> extractOuth2Claims(Authentication authentication) {
        Map<String, Object> claims;
        if (authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            OidcIdToken idToken = oidcUser.getIdToken();
            claims = idToken.getClaims();
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            claims = oauth2User.getAttributes();
        } else {
            claims = Collections.emptyMap();
        }
        return new HashMap<>(claims);
    }

    public String getEmail(Authentication principal) {
        Map<String, Object> claims = extractOuth2Claims(principal);
        String email = (String) claims.get("email");
        String provider = getAuthorizedClientRegistrationId(principal);
        if (email == null && provider.equals("github")) {
            return claims.get("login").toString() + "@github.com";
        }

        return email;
    }

    public String getAuthorizedClientRegistrationId(Authentication principal) {
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) principal;
            String registrationId = oauth2Authentication.getAuthorizedClientRegistrationId();
            return registrationId;
        }
        return null;
    }


}
