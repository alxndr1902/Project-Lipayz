package com.zezame.lipayz.mapper;

import com.zezame.lipayz.dto.paymentgateway.PaymentGatewayAdminResDTO;
import com.zezame.lipayz.dto.paymentgateway.PaymentGatewayResDTO;
import com.zezame.lipayz.model.PaymentGateway;
import com.zezame.lipayz.model.PaymentGatewayAdmin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentGatewayMapper {
    PaymentGatewayResDTO mapToDto(PaymentGateway paymentGateway);

    @Mapping(target = "roleName", source = "user.role.name")
    @Mapping(target = "paymentGatewayName", source = "paymentGateway.name")
    PaymentGatewayAdminResDTO mapToDto(PaymentGatewayAdmin  paymentGatewayAdmin);
}
