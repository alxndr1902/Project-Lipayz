package com.zezame.lipayz.dto.paymentgateway;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentGatewayResDTO {
    private UUID id;
    private String code;
    private String name;
    private Integer version;
}
