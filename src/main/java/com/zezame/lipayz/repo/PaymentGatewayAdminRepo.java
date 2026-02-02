package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.PaymentGatewayAdmin;
import com.zezame.lipayz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentGatewayAdminRepo extends JpaRepository<PaymentGatewayAdmin, UUID> {
    PaymentGatewayAdmin findByUser(User user);
}
