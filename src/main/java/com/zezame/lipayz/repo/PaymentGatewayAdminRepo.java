package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.PaymentGatewayAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentGatewayAdminRepo extends JpaRepository<PaymentGatewayAdmin, UUID> {
    boolean existsByEmail(String email);
}
