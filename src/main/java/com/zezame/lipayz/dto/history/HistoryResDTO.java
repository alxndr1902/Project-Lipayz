package com.zezame.lipayz.dto.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResDTO {
    private UUID id;
    private String transactionCode;
    private String transactionStatusCode;
    private String createdAt;
}
