package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.exceptiohandler.exception.InvalidUUIDException;
import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.PrincipalService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PrincipalServiceImpl implements PrincipalService {
    @Override
    public AuthorizationPojo getPrincipal() {
        var auth  = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UsernameNotFoundException("Invalid Login");
        }
        return (AuthorizationPojo) auth.getPrincipal();
    }

    @Override
    public UUID getUserId() {
        try {
            return UUID.fromString(getPrincipal().getId());
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Invalid UUID");
        }
    }
}