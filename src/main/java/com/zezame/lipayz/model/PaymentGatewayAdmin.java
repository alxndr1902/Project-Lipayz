package com.zezame.lipayz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_gateway_admins")
public class PaymentGatewayAdmin extends BaseModel{
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "payment_gateway_id", nullable = false)
    private PaymentGateway paymentGateway;

    public PaymentGatewayAdmin(UUID paymentGatewayId) {
        this.paymentGateway = new PaymentGateway();
        this.paymentGateway.setId(paymentGatewayId);
    }
}
