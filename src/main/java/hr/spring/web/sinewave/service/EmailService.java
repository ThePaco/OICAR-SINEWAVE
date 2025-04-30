package hr.spring.web.sinewave.service;

public interface EmailService {
    void sendAccountCreatedEmail(String to, String username);
}
