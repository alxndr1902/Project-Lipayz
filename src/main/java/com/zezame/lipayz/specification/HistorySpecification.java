package com.zezame.lipayz.specification;

import com.zezame.lipayz.model.History;
import com.zezame.lipayz.model.PaymentGateway;
import com.zezame.lipayz.model.PaymentGatewayAdmin;
import com.zezame.lipayz.model.Transaction;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class HistorySpecification {
    public static Specification<History> byRole(String role, UUID id) {
        return (root, query, cb) -> {
            if (role == null) {
                return cb.disjunction();
            }

            Join<History, Transaction> transaction = root.join("transaction", JoinType.INNER);

            return switch (role) {
                case "SA" -> cb.conjunction();
                case "CUST" -> cb.equal(root.get("transaction").get("customer").get("id"), id);
                case "PGA" -> {
                    Join<Transaction, PaymentGateway> pg =
                            transaction.join("paymentGateway", JoinType.INNER);

                    Root<PaymentGatewayAdmin> pga =
                            query.from(PaymentGatewayAdmin.class);

                    Predicate joinPg =
                            cb.equal(pga.get("paymentGateway"), pg);

                    Predicate userMatch =
                            cb.equal(pga.get("user").get("id"), id);

                    yield cb.and(joinPg, userMatch);
                }
                default -> cb.disjunction();
            };
        };
    }
}
