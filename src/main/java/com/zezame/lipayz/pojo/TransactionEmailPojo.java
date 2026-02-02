package com.zezame.lipayz.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionEmailPojo {
    private String customerEmail;
    private String transactionCode;
}
