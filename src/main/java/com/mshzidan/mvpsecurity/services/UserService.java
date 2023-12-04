package com.mshzidan.mvpsecurity.services;

import com.mshzidan.mvpsecurity.model.EmailCode;
import com.mshzidan.mvpsecurity.model.Provider;
import com.mshzidan.mvpsecurity.model.User;
import com.mshzidan.mvpsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //We can use findByUserNameOREmailOrMobile but this is more better
        User user = null;
        if (username.contains("@")) user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        else if (username.matches("-?\\d+(.\\d+)?")) user = userRepository.findByMobile(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        else user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(!user.isEnabled()){
            System.out.println("user is disabled");
            throw new DisabledException("User need to be Activated");
        }
         return user;
    }


    public void signUp (User user){
        String encryptedPass = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPass);
        user.setEnabled(false);
        user.setProvider(Provider.local);
        EmailCode emailCode = emailService.saveEmailVerifyWithUser(user);
        emailService.sendEmail(emailCode);
        userRepository.save(user);
    }


    public void save(User user){
        userRepository.save(user);
    }


    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("this Email is not found"));
    }
    public boolean isRegisterd(String email){
       return userRepository.findByEmail(email).isPresent();
    }

    public User addOuth2UserName(User user, String username){
        user.setUsername(username);
        user.setEnabled(true);
        return userRepository.save(user);
    }


}
