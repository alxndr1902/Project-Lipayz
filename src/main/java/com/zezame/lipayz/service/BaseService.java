package com.zezame.lipayz.service;

import com.zezame.lipayz.exceptiohandler.exception.InvalidUUIDException;
import com.zezame.lipayz.model.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class BaseService {
    protected PrincipalService principalService;

    protected <T extends BaseModel> T prepareCreate(T model) {
        model.setId(UUID.randomUUID());
        model.setCreatedAt(LocalDateTime.now());
        model.setCreatedBy(UUID.fromString(principalService.getPrincipal().getId()));
        return model;
    }

    protected <T extends BaseModel> T prepareCreate(T model, LocalDateTime now) {
        model.setId(UUID.randomUUID());
        model.setCreatedAt(now);
        return model;
    }

    protected <T extends BaseModel> T prepareUpdate(T model) {
        model.setUpdatedAt(LocalDateTime.now());
        model.setUpdatedBy(UUID.fromString(principalService.getPrincipal().getId()));
        return model;
    }

    protected <T extends BaseModel> T prepareUpdate(T model, LocalDateTime now) {
        model.setUpdatedAt(now);
        model.setUpdatedBy(UUID.fromString(principalService.getPrincipal().getId()));
        return model;
    }

    protected UUID parseUUID(String request) {
        if (request == null || request.isBlank()) {
            throw new InvalidUUIDException("Id Is Required");
        }
        try {
            return UUID.fromString(request);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Invalid UUID");
        }
    }

    protected String generateRandomAlphaNumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            result.append(chars.charAt(index));
        }
        return result.toString();
    }

    @Autowired
    public void setPrincipal(PrincipalService principalService) {
        this.principalService = principalService;
    }
}
