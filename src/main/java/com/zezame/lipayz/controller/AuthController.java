package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.auth.LoginRequestDTO;
import com.zezame.lipayz.dto.auth.LoginResponseDTO;
import com.zezame.lipayz.service.JwtService;
import com.zezame.lipayz.service.UserService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        var auth = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authenticationManager.authenticate(auth);

        var user = userService.findByEmail(request.getEmail());

        var token = jwtService.generateToken(user.getId().toString(), user.getRole().getCode());
        return new ResponseEntity<>(new LoginResponseDTO(user.getFullName(),
                user.getRole().getCode(), token), HttpStatus.OK);
    }


}
