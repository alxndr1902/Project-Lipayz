package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.paymentgateway.*;
import com.zezame.lipayz.service.PaymentGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("payment-gateways")
public class PaymentGatewayController {
    private final PaymentGatewayService paymentGatewayService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SA', 'CUST')")
    public ResponseEntity<List<PaymentGatewayResDTO>> getPaymentGateways() {
        var response = paymentGatewayService.getPaymentGateways();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('SA', 'CUST')")
    public ResponseEntity<PaymentGatewayResDTO> getPaymentGatewayById(@PathVariable String id) {
        var response = paymentGatewayService.getPaymentGatewayById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<UpdateResDTO> updatePaymentGateway(@PathVariable String id,
                                                             @RequestBody @Valid UpdatePGReqDTO request) {
        var response = paymentGatewayService.updatePaymentGateway(id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<CreateResDTO> registerPaymentGateway(@RequestBody @Valid CreatePGReqDTO request) {
        var response = paymentGatewayService.registerPaymentGateway(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<CommonResDTO> deletePaymentGateway(@PathVariable String id) {
        var response = paymentGatewayService.deletePaymentGateway(id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("{paymentGatewayId}/admins")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<CreateResDTO> registerPaymentGatewayAdmin(@PathVariable String paymentGatewayId,
                                                                    @RequestBody @Valid CreatePGAdminReqDTO request) {
        var response = paymentGatewayService.registerPaymentGatewayAdmin(paymentGatewayId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("{paymentGatewayId}/admins")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<PageRes<PaymentGatewayAdminResDTO>> getPaymentGatewayAdmins(@RequestParam(defaultValue = "0") Integer page,
                                                                                      @RequestParam(defaultValue = "10") Integer size,
                                                                                      @PathVariable String paymentGatewayId) {
        Pageable pageable = PageRequest.of(page, size);
        var response = paymentGatewayService.getPaymentGatewayAdmins(paymentGatewayId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("admins/{id}")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<PaymentGatewayAdminResDTO> getPaymentGatewayAdminById(@PathVariable String id) {
        var response = paymentGatewayService.getPaymentGatewayAdminById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("admins/{id}")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<CommonResDTO> deletePaymentGatewayAdmin(@PathVariable String id) {
        var response = paymentGatewayService.deletePaymentGatewayAdmin(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
