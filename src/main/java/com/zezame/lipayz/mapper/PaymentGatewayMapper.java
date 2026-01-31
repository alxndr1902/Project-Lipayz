package com.zezame.lipayz.mapper;

import com.zezame.lipayz.dto.paymentgateway.PaymentGatewayResDTO;
import com.zezame.lipayz.model.PaymentGateway;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentGatewayMapper {
    PaymentGatewayResDTO mapToDto(PaymentGateway paymentGateway);
}
