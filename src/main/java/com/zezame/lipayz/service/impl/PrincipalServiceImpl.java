package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.service.PrincipalService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
}