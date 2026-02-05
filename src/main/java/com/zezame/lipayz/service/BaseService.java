package com.zezame.lipayz.service;

import com.zezame.lipayz.exceptiohandler.exception.InvalidParameterException;
import com.zezame.lipayz.exceptiohandler.exception.InvalidUUIDException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.exceptiohandler.exception.UnauthorizedException;
import com.zezame.lipayz.model.BaseModel;
import com.zezame.lipayz.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class BaseService {
    protected PrincipalService principalService;

    private <T extends BaseModel> T initialCreate(T model, LocalDateTime now) {
        model.setId(UUID.randomUUID());
        model.setCreatedBy(UUID.fromString(principalService.getPrincipal().getId()));
        model.setCreatedAt(now);
        return model;
    }

    protected <T extends BaseModel> T prepareCreate(T model) {
        return initialCreate(model, LocalDateTime.now());
    }

    protected <T extends BaseModel> T prepareCreate(T model, LocalDateTime now) {
        return initialCreate(model, now);
    }

    protected <T extends BaseModel> T prepareRegister(T model, UserRepo userRepo) {
        var system = userRepo.findSystem()
                        .orElseThrow(() -> new NotFoundException("System Is Not Found"));
        model.setId(UUID.randomUUID());
        model.setCreatedAt(LocalDateTime.now());
        model.setCreatedBy(system.getId());
        return model;
    }

    protected <T extends BaseModel> T prepareActivate(T model, UserRepo userRepo) {
        var system = userRepo.findSystem()
                .orElseThrow(() -> new NotFoundException("System Is Not Found"));
        model.setUpdatedAt(LocalDateTime.now());
        model.setUpdatedBy(system.getId());
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

    protected void validatePaginationParam(Integer page, Integer size) {
        if (page == null || page < 1) {
            throw new InvalidParameterException("Invalid Page Number");
        }

        if (size == null || size < 1) {
            throw new InvalidParameterException("Invalid Page Size");
        }
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

    protected <T> Page<T> printPaginationByRole(
            String role,
            Supplier<Page<T>> saSupplier,
            Supplier<Page<T>> custSupplier,
            Supplier<Page<T>> pgaSupplier
    ) {
        return switch (role) {
            case "SA"   -> saSupplier.get();
            case "CUST" -> custSupplier.get();
            case "PGA"  -> pgaSupplier.get();
            default -> throw new UnauthorizedException("Invalid Role");
        };
    }

    @Autowired
    public void setPrincipal(PrincipalService principalService) {
        this.principalService = principalService;
    }
}
