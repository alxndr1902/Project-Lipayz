package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.transaction.CreateTransactionReqDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionResDTO;
import com.zezame.lipayz.dto.transaction.TransactionResDTO;
import com.zezame.lipayz.service.TransactionService;
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
@RequestMapping("transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<PageRes<TransactionResDTO>> getTransactions(@RequestParam(defaultValue = "0") Integer page,
                                                                      @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        var response = transactionService.getTransactions(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUST')")
    public ResponseEntity<CreateTransactionResDTO> createTransaction(@RequestBody @Valid CreateTransactionReqDTO request) {
        var response = transactionService.createTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasRole('PGA')")
    public ResponseEntity<CommonResDTO> processTransaction(@PathVariable String id,
                                                           @RequestParam String action) {
        var response = transactionService.processTransaction(id, action);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
