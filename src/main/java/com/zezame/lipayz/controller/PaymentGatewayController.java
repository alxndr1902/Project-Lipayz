package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.paymentgateway.*;
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

    @PutMapping("{id}")
    public ResponseEntity<UpdateResDTO> updatePaymentGateway(@PathVariable String id,
                                                             @RequestBody @Valid UpdatePGReqDTO request) {
        var response = paymentGatewayService.updatePaymentGateway(id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<CreateResDTO> registerPaymentGateway(@RequestBody @Valid CreatePGReqDTO request) {
        var response = paymentGatewayService.registerPaymentGateway(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<CommonResDTO> deletePaymentGateway(@PathVariable String id) {
        var response = paymentGatewayService.deletePaymentGateway(id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("{paymentGatewayId}/admins")
    public ResponseEntity<CreateResDTO> registerPGAdmin(@PathVariable String paymentGatewayId,
                                                        @RequestBody @Valid CreatePGAdminReqDTO request) {
        var response = paymentGatewayService.registerPaymentGatewayAdmin(paymentGatewayId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("{paymentGatewayId}/admins")
    public ResponseEntity<List<PaymentGatewayAdminResDTO>> getPaymentGatewayAdmins(@PathVariable String paymentGatewayId) {
        var response = paymentGatewayService.getPaymentGatewayAdmins(paymentGatewayId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{paymentGatewayId}/admins/{id}")
    public ResponseEntity<CommonResDTO> deletePaymentGatewayAdmin(@PathVariable String id) {
        var response = paymentGatewayService.deletePaymentGatewayAdmin(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
