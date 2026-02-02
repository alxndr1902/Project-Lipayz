package com.zezame.lipayz.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionReqDTO {
    @NotBlank(message = "Product Is Required")
    @Size(min = 36, max = 36)
    private String productId;

    @NotNull(message = "Transaction Nominal Is Required")
    @Min(value = 10000, message = "Minimum Transaction Is Rp10.000")
    private BigDecimal nominal;

    @NotBlank(message = "Virtual Account Number Is Required")
    @Size(max = 30, message = "Virtual Account Number Maximum Length Is 30 Characters")
    private String virtualAccountNumber;

    @NotBlank(message = "Payment Gateway Is Required")
    @Size(min = 36, max = 36)
    private String paymentGatewayId;
}
