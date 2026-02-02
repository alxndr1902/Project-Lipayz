package com.zezame.lipayz.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActivateCustomerEmailPojo {
    private String customerEmail;
    private String link;
}
