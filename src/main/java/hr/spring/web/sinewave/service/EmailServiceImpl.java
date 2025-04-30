package hr.spring.web.sinewave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendAccountCreatedEmail(String to, String username) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Welcome to Sinewave!");
        msg.setFrom("bogdanic3@gmail.com");
        msg.setText(
                "Hi " + username + ",\n\n" +
                        "Your account has been successfully created on Sinewave!\n\n" +
                        "Enjoy your music,\nThe Sinewave Team"
        );
        mailSender.send(msg);
    }
}
