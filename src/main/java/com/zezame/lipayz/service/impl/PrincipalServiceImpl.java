package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.exceptiohandler.exception.InvalidUUIDException;
import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.repo.PaymentGatewayRepo;
import com.zezame.lipayz.service.PrincipalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PrincipalServiceImpl implements PrincipalService {

    private final PaymentGatewayRepo paymentGatewayRepo;

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