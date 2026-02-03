package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.History;
import com.zezame.lipayz.model.PaymentGateway;
import com.zezame.lipayz.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HistoryRepo extends JpaRepository<History, UUID> {
    @Query("""
        SELECT h
        FROM History h
        WHERE h.transaction.customer.id = :id
        """)
    Page<History> findByCustomer(@Param("id") String id, Pageable pageable);

    @Query("""
        SELECT h
        FROM History h
        INNER JOIN PaymentGatewayAdmin pga ON pga.paymentGateway = h.transaction.paymentGateway
        WHERE pga.user.id = :id
        """)
    Page<History> findByPaymentGateway(@Param("id") String id, Pageable pageable);
}
