package com.zezame.lipayz.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TransactionResDTO {
    private UUID id;
    private String code;
    private String productName;
    private String virtualAccountNumber;
    private String customerName;
    private String paymentGatewayName;
    private String transactionStatusName;
    private BigDecimal adminRate;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}
