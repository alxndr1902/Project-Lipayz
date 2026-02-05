package com.zezame.lipayz.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Email(message = "Invalid Email Format")
    @NotBlank(message = "Email Is Required")
    @Size(max = 100, message = "Email Maximum Length Is 100 Characters")
    private String email;

    @NotBlank(message = "Password Is Required")
    @Size(max = 200, message = "Password Maximum Length Is 200 Characters")
    private String password;
}
