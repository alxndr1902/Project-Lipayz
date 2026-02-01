package com.zezame.lipayz.dto.paymentgateway;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePGReqDTO {
    @NotBlank(message = "Code Is Required")
    @Size(max = 20, message = "Maximum Code Length Is 20 Characters")
    private String code;

    @NotBlank(message = "Name Is Required")
    @Size(max = 100, message = "Maximum Name Length Is 100 Characters")
    private String name;

    @NotNull(message = "Rate Is Required")
    @Min(value = 0, message = "Rate Minimum Is 0")
    private BigDecimal rate;

    @NotNull
    private Integer version;
}
