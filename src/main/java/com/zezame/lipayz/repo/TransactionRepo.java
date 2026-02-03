package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.PaymentGateway;
import com.zezame.lipayz.model.Product;
import com.zezame.lipayz.model.Transaction;
import com.zezame.lipayz.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TransactionRepo extends JpaRepository<Transaction, UUID> {
    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.customer = :customer
        """)
    Page<Transaction> findByCustomer (Pageable pageable,
                                      @Param("customer") User customer);

    @Query("""
           SELECT t
           FROM Transaction t
           WHERE t.paymentGateway = :paymentGateway
           """)
    Page<Transaction> findByPaymentGateway (Pageable pageable,
                                                  @Param("paymentGateway") PaymentGateway paymentGateway);

    boolean existsByCustomer(User customer);

    boolean existsByUpdatedByEquals(UUID updatedBy);

    boolean existsByProduct(Product product);
}
