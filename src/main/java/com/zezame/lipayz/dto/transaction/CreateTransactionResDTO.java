package com.zezame.lipayz.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateTransactionResDTO {
    private UUID id;
    private String code;
    private String message;
}
