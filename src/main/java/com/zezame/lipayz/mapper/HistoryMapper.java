package com.zezame.lipayz.mapper;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.model.History;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoryMapper {
    @Mapping(target = "transactionCode", source = "transaction.code")
    @Mapping(target = "transactionStatusCode", source = "transactionStatus.code")
    HistoryResDTO mapToDto(History history);
}
