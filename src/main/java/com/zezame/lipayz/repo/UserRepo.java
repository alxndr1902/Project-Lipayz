package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndRoleCode(UUID id, String code);

    @Query("""
        SELECT u
        FROM User u
        WHERE
                u.email = :email AND
                u.activationCode = :activationCode AND
                u.role.code = :roleCode
        """)
    Optional<User> findCustomerToActivate(@Param(value = "email") String email,
                                          @Param(value = "activationCode") String activationCode,
                                          @Param(value = "roleCode") String roleCode);
}
