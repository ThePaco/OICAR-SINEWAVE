package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.model.RefreshToken;
import hr.spring.web.sinewave.repository.RefreshTokenRepository;
import hr.spring.web.sinewave.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long refreshTokenExpiration;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               @Value("${jwt.refresh.expiration:604800000}") long refreshTokenExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public RefreshToken createRefreshToken(String username) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserInfo_Username(username);
        existingToken.ifPresent(token -> refreshTokenRepository.deleteByToken(token.getToken()));

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username)))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.delete(refreshToken.get());
        } else {
            throw new RuntimeException("Refresh Token is not in DB!");
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login!");
        }
        return token;
    }

    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUserInfo_Username(username);
    }
}