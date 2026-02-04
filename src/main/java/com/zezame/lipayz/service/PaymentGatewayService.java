package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.paymentgateway.CreatePGAdminReqDTO;
import com.zezame.lipayz.dto.paymentgateway.CreatePGReqDTO;
import com.zezame.lipayz.dto.paymentgateway.PaymentGatewayResDTO;
import com.zezame.lipayz.dto.paymentgateway.UpdatePGReqDTO;

public interface PaymentGatewayService {
    PageRes<PaymentGatewayResDTO> getPaymentGateways(Integer page, Integer size);

    PaymentGatewayResDTO getPaymentGatewayById(String id);

    CreateResDTO registerPaymentGateway(CreatePGReqDTO request);

    UpdateResDTO updatePaymentGateway(String id, UpdatePGReqDTO request);

    CommonResDTO deletePaymentGateway(String id);

    CreateResDTO registerPaymentGatewayAdmin(String paymentGatewayId, CreatePGAdminReqDTO request);
}
