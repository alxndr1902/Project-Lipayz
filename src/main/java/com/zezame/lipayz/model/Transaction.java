package com.zezame.lipayz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction extends BaseModel {
    @Column(nullable = false, unique = false, length = 20)
    private String code;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private BigDecimal nominal;

    @Column(nullable = false, length = 30)
    private String virtualAccountNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "payment_gateway_id", nullable = false)
    private PaymentGateway paymentGateway;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TransactionStatus transactionStatus;

    @Column(nullable = false)
    private BigDecimal totalPrice;
}