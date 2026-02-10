package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.auth.LoginRequestDTO;
import com.zezame.lipayz.dto.auth.LoginResponseDTO;
import com.zezame.lipayz.dto.auth.RefreshTokenRequest;
import com.zezame.lipayz.exceptiohandler.exception.ForbiddenException;
import com.zezame.lipayz.exceptiohandler.exception.InvalidRefreshToken;
import com.zezame.lipayz.model.RefreshToken;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.repo.RefreshTokenRepo;
import com.zezame.lipayz.service.JwtService;
import com.zezame.lipayz.service.UserService;
import com.zezame.lipayz.util.RateLimiterUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepo refreshTokenRepo;

    private final RateLimiterUtil rateLimiterUtil;
    private final HttpServletRequest httpServletRequest;

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        var auth = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authenticationManager.authenticate(auth);

        var user = userService.findByEmail(request.getEmail());

        if (!user.getIsActivated()) {
            throw new ForbiddenException("Please Activate Your Account First Before Logging In");
        }
        var accessToken = jwtService.generateToken(user.getId().toString(),
                user.getRole().getCode());
        var token = jwtService.generateRefreshToken(user.getId().toString(),
                user.getRole().getCode());
        saveRefreshToken(user);

//        String key = httpServletRequest.getRemoteAddr() + ":/auth/login";
//        rateLimiterUtil.reset(key);

        return new ResponseEntity<>(new LoginResponseDTO(user.getFullName(),
                user.getRole().getCode(), accessToken, token), HttpStatus.OK);
    }

    private void saveRefreshToken(User user) {
        var refreshToken = new RefreshToken();
        refreshToken.setUser(user);

        var token = jwtService.generateRefreshToken(user.getId().toString(), user.getRole().getCode());
        refreshToken.setToken(token);

        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofHours(24)));

        refreshTokenRepo.save(refreshToken);
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        var claims = jwtService.validateToken(refreshToken);
        return refreshTokenRepo.findByToken(refreshToken)
                .map(token -> {
                    if (jwtService.isTokenExpired(token)) {
                        refreshTokenRepo.delete(token);
                        throw new InvalidRefreshToken("Refresh Token Has Expired, Please Login Again");
                    }
                    var newToken = jwtService.generateToken(claims.get("id", String.class),
                            claims.get("role", String.class));
                    return ResponseEntity.ok(Map.of("accessToken", newToken));
                })
                .orElseThrow(() -> new InvalidRefreshToken("Invalid Refresh Token"));
//        if (!"REFRESH".equals(claims.get("type"))) {
//            throw new InvalidRefreshToken("Invalid Refresh Token");
//        }
//
//        String id = claims.get("id", String.class);
//        String roleCode = claims.get("role", String.class);
//        String newAccessToken = jwtService.generateToken(id, roleCode);
//
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("accessToken", newAccessToken);
//
//        String key = httpServletRequest.getRemoteAddr() + ":/auth/login";
//        rateLimiterUtil.reset(key);
//
//        return ResponseEntity.ok(tokens);
    }
}
