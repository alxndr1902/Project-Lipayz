package com.zezame.lipayz.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorizationPojo {
    private String id;
    private String roleCode;

    public AuthorizationPojo(String id) {
        this.id = id;
    }
}
