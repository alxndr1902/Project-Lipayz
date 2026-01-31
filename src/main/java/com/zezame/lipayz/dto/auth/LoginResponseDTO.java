package com.zezame.lipayz.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String fullName;
    private String roleCode;
    private String token;
}
