package com.zezame.lipayz.dto.paymentgateway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePGAdminReqDTO {
    @NotBlank(message = "Email Is Required")
    @Size(max = 100, message = "Maximum Email Length Is 100 Characters")
    private String email;

    @NotBlank(message = "Password Is Required")
    @Size(max = 200, message = "Maximum Password Length Is 200 Characters")
    private String password;

    @NotBlank(message = "Name Is Required")
    @Size(max = 100, message = "Maximum Name Length Is 100 Characters")
    private String name;
}
