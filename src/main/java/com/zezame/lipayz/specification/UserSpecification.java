package com.zezame.lipayz.specification;

import com.zezame.lipayz.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasName(String name) {
        return (root, query, cb) -> {
                if (name == null || name.isBlank()) {
                    return cb.conjunction();
                }
                return cb.like(root.get("fullName"), "%" + name + "%");
        };
    }
}
