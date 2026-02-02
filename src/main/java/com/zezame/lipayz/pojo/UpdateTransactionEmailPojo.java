package com.zezame.lipayz.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateTransactionEmailPojo {
    private String customerEmail;
    private String transactionCode;
    private String statusName;
}
