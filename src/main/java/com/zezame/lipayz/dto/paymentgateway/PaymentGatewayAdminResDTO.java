package com.zezame.lipayz.dto.paymentgateway;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentGatewayAdminResDTO {
    private UUID id;
    private String fullName;
    private String roleName;
    private String paymentGatewayName;
    private Integer version;
}
