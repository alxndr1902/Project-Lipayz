package com.zezame.lipayz.pojo;

import com.zezame.lipayz.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActivateCustomerEmailPojo {
    private User customer;
    private String activationLink;
}
