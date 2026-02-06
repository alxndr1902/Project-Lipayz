package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndRoleCode(UUID id, String code);

    @Query("""
        SELECT u
        FROM User u
        WHERE
                u.email = :email AND
                u.activationCode = :activationCode
        """)
    Optional<User> findCustomerToActivate(@Param(value = "email") String email,
                                          @Param(value = "activationCode") String activationCode);

    @Query("""
        SELECT u
        FROM User u
        WHERE u.role.code = 'SYS'
        """)
    Optional<User> findSystem();
}
