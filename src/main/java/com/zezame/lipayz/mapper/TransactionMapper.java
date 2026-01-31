package com.zezame.lipayz.mapper;

import com.zezame.lipayz.dto.transaction.TransactionResDTO;
import com.zezame.lipayz.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "paymentGatewayName", source = "paymentGateway.name")
    @Mapping(target = "transactionStatusName", source = "transactionStatus.name")
    @Mapping(target = "adminRate", source = "paymentGateway.rate")
    TransactionResDTO mapToDto(Transaction transaction);
}
