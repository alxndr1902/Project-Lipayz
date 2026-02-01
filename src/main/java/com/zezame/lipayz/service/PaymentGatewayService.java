package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.paymentgateway.*;

import java.util.List;

public interface PaymentGatewayService {
    List<PaymentGatewayResDTO> getPaymentGateways();

    PaymentGatewayResDTO getPaymentGatewayById(String id);

    CreateResDTO registerPaymentGateway(CreatePGReqDTO request);

    UpdateResDTO updatePaymentGateway(String id, UpdatePGReqDTO request);

    CommonResDTO deletePaymentGateway(String id);

    CreateResDTO registerPaymentGatewayAdmin(String paymentGatewayId, CreatePGAdminReqDTO request);

    List<PaymentGatewayAdminResDTO> getPaymentGatewayAdmins(String paymentGatewayId);

    PaymentGatewayAdminResDTO getPaymentGatewayAdminById(String id);

    CommonResDTO deletePaymentGatewayAdmin(String id);
}
