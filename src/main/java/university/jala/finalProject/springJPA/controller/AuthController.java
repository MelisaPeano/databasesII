package university.jala.finalProject.springJPA.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.service.UserService;
import university.jala.finalProject.springJPA.dto.*;
import university.jala.finalProject.springJPA.repository.AppUserRepository;
import university.jala.finalProject.springJPA.security.JwtUtil;
import university.jala.finalProject.springJPA.service.RefreshTokenService;
import university.jala.finalProject.springJPA.entity.RefreshToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AppUserRepository userRepo;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil,
                          UserService userService, AppUserRepository userRepo,
                          RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepo = userRepo;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            userService.register(req.getEmail(), req.getPassword());
            return ResponseEntity.ok("Usuario creado");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            String accessToken = jwtUtil.generateToken(req.getEmail());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(req.getEmail());

            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken.getToken()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(request.getRefreshToken());
        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenService.verifyExpiration(refreshTokenOpt.get());
            AppUser user = refreshToken.getUser();
            String newAccessToken = jwtUtil.generateToken(user.getUserEmail());
            TokenResponse tokenResponse = new TokenResponse(newAccessToken, request.getRefreshToken());
            return ResponseEntity.ok(tokenResponse);
        } else {
            return ResponseEntity.status(403).body("Refresh token invalid");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok("Sesión cerrada");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("No autenticado");
            }

            String email = authentication.getName();
            AppUser user = userRepo.findByUserEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", user.getUserEmail());
            userInfo.put("name", user.getUserName());
            userInfo.put("userId", user.getUserId());
            userInfo.put("createdIn", user.getCreatedIn());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener información del usuario");
        }
    }
}