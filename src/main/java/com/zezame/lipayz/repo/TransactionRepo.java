package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.Product;
import com.zezame.lipayz.model.Transaction;
import com.zezame.lipayz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepo extends JpaRepository<Transaction, UUID> {
    boolean existsByCustomer(User customer);

    boolean existsByUpdatedByEquals(UUID updatedBy);

    boolean existsByProduct(Product product);
}
