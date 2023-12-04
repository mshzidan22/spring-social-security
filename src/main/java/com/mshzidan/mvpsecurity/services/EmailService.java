package com.mshzidan.mvpsecurity.services;

import com.mshzidan.mvpsecurity.model.EmailCode;
import com.mshzidan.mvpsecurity.model.User;
import com.mshzidan.mvpsecurity.repository.EmailRepository;
import com.mshzidan.mvpsecurity.repository.UserRepository;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.UUID;

import static io.jsonwebtoken.lang.Strings.UTF_8;


@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("classpath:message.txt")
    private Resource resource;

    @Value("${email.message.sender}")
    private String messageSender;

    @Value("${email.subject}")
    private String subject;

    public void ValidateEmail(String code){
        EmailCode emailWithCode = emailRepository.findByCode(code)
                .orElseThrow( () -> new RuntimeException("Wrong Code or user"));
        emailWithCode.getUser().setEnabled(true);
     emailRepository.save(emailWithCode);

    }


    public EmailCode saveEmailVerifyWithUser(User user){
        EmailCode e = new EmailCode();
        e.setUser(user);
        e.setCode(UUID.randomUUID().toString());
        emailRepository.save(e);
        return e;
    }

    public void sendEmail(EmailCode emailCode){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailCode.getUser().getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(getMessageToSend(emailCode.getUser().getUsername() , emailCode.getCode()));
        mailMessage.setFrom(messageSender);
        javaMailSender.send(mailMessage);
    }

    public String getMessageToSend(String username, String verificationCode){
        String message;
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
             message = FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        message = message.replace("#username" , username);
        message =message.replace("#verification" , verificationCode);
        return message;

    }


}
