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
        WHERE t.customer.id = :id
        """)
    Page<Transaction> findByCustomer (Pageable pageable,
                                      @Param("id") String id);

    @Query("""
           SELECT t
           FROM Transaction t
           INNER JOIN PaymentGatewayAdmin pga ON pga.paymentGateway = t.paymentGateway
           WHERE pga.user.id = :id
           """)
    Page<Transaction> findByPaymentGateway (Pageable pageable,
                                            @Param("id") String id);

    boolean existsByCustomer(User customer);

    boolean existsByUpdatedByEquals(UUID updatedBy);

    boolean existsByProduct(Product product);
}
