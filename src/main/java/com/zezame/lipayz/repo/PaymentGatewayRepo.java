package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentGatewayRepo extends JpaRepository<PaymentGateway, UUID> {
    boolean existsByCode(String code);
}
