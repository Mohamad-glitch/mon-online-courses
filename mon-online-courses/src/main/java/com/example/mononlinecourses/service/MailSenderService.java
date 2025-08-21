package com.example.mononlinecourses.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {


    private final String EMAIL_ADDRESS;
    private final JavaMailSender mailSender;

    @Autowired
    public MailSenderService(@Value("${email.address}") String emailAddress, JavaMailSender mailSender) {
        EMAIL_ADDRESS = emailAddress;
        this.mailSender = mailSender;
    }


    // this will execute the method intended less time needed to execute other method(caller method)
    @Async
    public void sendResetPassword(String email, String url) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EMAIL_ADDRESS);
        message.setTo(email);
        message.setSubject("reset password");
        message.setText(url);
        mailSender.send(message);
    }

}
