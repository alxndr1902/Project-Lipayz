package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.paymentgateway.CreatePGAdminReqDTO;
import com.zezame.lipayz.dto.paymentgateway.CreatePGReqDTO;
import com.zezame.lipayz.dto.paymentgateway.PaymentGatewayResDTO;
import com.zezame.lipayz.dto.user.UserResDTO;
import com.zezame.lipayz.service.PaymentGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("payment-gateways")
public class PaymentGatewayController {
    private final PaymentGatewayService paymentGatewayService;

    @GetMapping
    public ResponseEntity<List<PaymentGatewayResDTO>> getPaymentGateways() {
        var response = paymentGatewayService.getPaymentGateways();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<PaymentGatewayResDTO> getPaymentGateway(@PathVariable String id) {
        var response = paymentGatewayService.getPaymentGatewayById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CreateResDTO> registerAdmin(@RequestBody @Valid CreatePGReqDTO request) {
        var response = paymentGatewayService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("{paymentGatewayId}/admin")
    public ResponseEntity<CreateResDTO> registerPGAdmin(@PathVariable String paymentGatewayId,
                                                        @RequestBody @Valid CreatePGAdminReqDTO request) {
        var response = paymentGatewayService.registerPGAdmin(paymentGatewayId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("{paymentGatewayId}/admin")
    public ResponseEntity<List<UserResDTO>> getPaymentGatewayAdmins(@PathVariable String paymentGatewayId) {
        var response = paymentGatewayService.getPaymentGatewayAdmins(paymentGatewayId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
