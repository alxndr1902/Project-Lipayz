package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.auth.LoginRequestDTO;
import com.zezame.lipayz.dto.auth.LoginResponseDTO;
import com.zezame.lipayz.dto.auth.RefreshTokenRequest;
import com.zezame.lipayz.exceptiohandler.exception.ForbiddenException;
import com.zezame.lipayz.exceptiohandler.exception.InvalidRefreshToken;
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

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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
        var refreshToken = jwtService.generateRefreshToken(user.getId().toString(),
                user.getRole().getCode());

//        String key = httpServletRequest.getRemoteAddr() + ":/auth/login";
//        rateLimiterUtil.reset(key);

        return new ResponseEntity<>(new LoginResponseDTO(user.getFullName(),
                user.getRole().getCode(), accessToken, refreshToken), HttpStatus.OK);
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        var claims = jwtService.validateToken(refreshToken);

        if (!"REFRESH".equals(claims.get("type"))) {
            throw new InvalidRefreshToken("Invalid Refresh Token");
        }

        String id = claims.get("id", String.class);
        String roleCode = claims.get("role", String.class);
        String newAccessToken = jwtService.generateToken(id, roleCode);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);

        String key = httpServletRequest.getRemoteAddr() + ":/auth/login";
        rateLimiterUtil.reset(key);

        return ResponseEntity.ok(tokens);
    }
}
