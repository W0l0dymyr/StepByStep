package com.example.StepByStep.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpMailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    private static final Logger LOGGER = LogManager.getLogger(SmtpMailSender.class);


    public void send(String emailTo, String subject, String message){
        LOGGER.info("Method 'send' in class SmtpMailSender is started");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
        LOGGER.info("Message sent \nFrom: "+username+"\nEmailTo "+emailTo+"\nSubject "+subject+"\nMessage "+message);
    }
}


