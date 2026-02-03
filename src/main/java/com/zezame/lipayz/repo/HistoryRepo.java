package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.History;
import com.zezame.lipayz.model.PaymentGateway;
import com.zezame.lipayz.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface HistoryRepo extends JpaRepository<History, UUID> {
    @Query("""
        SELECT h
        FROM History h
        WHERE h.transaction.customer = :customer
        """)
    Page<History> findByCustomer(@Param("customer") User customer, Pageable pageable);

    @Query("""
        SELECT h
        FROM History h
        WHERE h.transaction.paymentGateway = :paymentGateway
        """)
    Page<History> findByPaymentGateway(PaymentGateway paymentGateway, Pageable pageable);
}
