package com.zezame.lipayz.service;

import com.zezame.lipayz.pojo.AuthorizationPojo;

import java.util.UUID;

public interface PrincipalService {
    AuthorizationPojo getPrincipal();

    UUID getUserId();
}
