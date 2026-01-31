package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.paymentgateway.CreatePGAdminReqDTO;
import com.zezame.lipayz.dto.paymentgateway.CreatePGReqDTO;
import com.zezame.lipayz.dto.paymentgateway.PaymentGatewayResDTO;
import com.zezame.lipayz.dto.user.UserResDTO;

import java.util.List;

public interface PaymentGatewayService {
    List<PaymentGatewayResDTO> getPaymentGateways();

    PaymentGatewayResDTO getPaymentGatewayById(String id);

    CreateResDTO register(CreatePGReqDTO request);

    CreateResDTO registerPGAdmin(String paymentGatewayId, CreatePGAdminReqDTO request);

    List<UserResDTO> getPaymentGatewayAdmins(String paymentGatewayId);
}
