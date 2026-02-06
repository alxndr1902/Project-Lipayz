package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("histories")
public class HistoryController extends BaseService {
    private final HistoryService historyService;
    private final PaymentGatewayAdminRepo paymentGatewayAdminRepo;

    @GetMapping
    public ResponseEntity<PageRes<HistoryResDTO>> getHistories(@RequestParam(defaultValue = "1") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer size) {
        var role = principalService.getPrincipal().getRoleCode();
        var userId = principalService.getUserId();

        userId = paymentGatewayAdminRepo.findByUser_Id(userId)
                .map(pga -> pga.getPaymentGateway().getId())
                .orElse(userId);

        var response = historyService.getHistories(page, size, role, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
