package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.PaymentGatewayAdmin;
import com.zezame.lipayz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentGatewayAdminRepo extends JpaRepository<PaymentGatewayAdmin, UUID> {
    PaymentGatewayAdmin findByUser(User user);

    @Query("""
        SELECT new com.zezame.lipayz.model.PaymentGatewayAdmin(pg.id)
        FROM PaymentGatewayAdmin pga
        JOIN PaymentGateway pg ON pga.paymentGateway.id = pg.id
        WHERE pga.user.id = :userId
        """)
    Optional<PaymentGatewayAdmin> findByUser_Id(UUID userId);
}
