package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.paymentgateway.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentGatewayService {
    List<PaymentGatewayResDTO> getPaymentGateways();

    PaymentGatewayResDTO getPaymentGatewayById(String id);

    CreateResDTO registerPaymentGateway(CreatePGReqDTO request);

    UpdateResDTO updatePaymentGateway(String id, UpdatePGReqDTO request);

    CommonResDTO deletePaymentGateway(String id);

    CreateResDTO registerPaymentGatewayAdmin(String paymentGatewayId, CreatePGAdminReqDTO request);

    PageRes<PaymentGatewayAdminResDTO> getPaymentGatewayAdmins(String paymentGatewayId, Pageable pageable);

    PaymentGatewayAdminResDTO getPaymentGatewayAdminById(String id);

    CommonResDTO deletePaymentGatewayAdmin(String id);
}
