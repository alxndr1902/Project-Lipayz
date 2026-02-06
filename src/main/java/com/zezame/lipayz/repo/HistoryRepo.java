package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface HistoryRepo extends JpaRepository<History, UUID>, JpaSpecificationExecutor<History> {
    @Query("""
        SELECT h
        FROM History h
        WHERE h.transaction.customer.id = :id
        """)
    Page<History> findByCustomer(@Param("id") UUID id, Pageable pageable);

    @Query("""
        SELECT h
        FROM History h
        INNER JOIN PaymentGatewayAdmin pga ON pga.paymentGateway = h.transaction.paymentGateway
        WHERE pga.user.id = :id
        """)
    Page<History> findByPaymentGateway(@Param("id") UUID id, Pageable pageable);
}
