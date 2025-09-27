package university.jala.finalProject.springJPA.service;

import org.springframework.stereotype.Service;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.entity.RefreshToken;
import university.jala.finalProject.springJPA.repository.AppUserRepository;
import university.jala.finalProject.springJPA.repository.RefreshTokenRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppUserRepository appUserRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, AppUserRepository appUserRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.appUserRepository = appUserRepository;
    }

    public RefreshToken createRefreshToken(String email) {
        AppUser user = appUserRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        System.out.println("Usuario encontrado: " + user.getUserEmail() + ", ID: " + user.getUserId()); // Debug

        // Eliminar token existente si hay uno
        refreshTokenRepository.findByUser(user).ifPresent(existingToken -> {
            refreshTokenRepository.delete(existingToken);
        });

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user); // ← ¡IMPORTANTE! Establecer el usuario
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        System.out.println("Creando refresh token para user_id: " + user.getUserId()); // Debug

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expirado");
        }
        return token;
    }

}
