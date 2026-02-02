package com.zezame.lipayz.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProductReqDTO {
    @NotBlank(message = "Code Is Required")
    @Size(max = 20, message = "Code Maximum Length Is 20 Characters")
    private String code;

    @NotBlank(message = "Name Is Required")
    @Size(max = 100, message = "Name Maximum Length Is 100 Characters")
    private String name;

    @NotNull
    private Integer version;
}
