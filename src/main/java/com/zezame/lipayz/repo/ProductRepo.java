package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
    boolean existsByCode(String code);
}
