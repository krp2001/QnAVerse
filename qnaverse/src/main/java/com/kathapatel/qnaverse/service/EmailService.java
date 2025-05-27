package com.kathapatel.qnaverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("QnAVerse Password Reset");
        
        String resetUrl = "http://localhost:9090/users/reset-password?token=" + token;
        message.setText("Hello,\n\nYou have requested to reset your password. " +
                "Please click the link below to reset your password:\n\n" + 
                resetUrl + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\nQnAVerse Team");
        
        mailSender.send(message);
    }
}