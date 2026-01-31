package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionReqDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionResDTO;
import com.zezame.lipayz.dto.transaction.TransactionResDTO;
import com.zezame.lipayz.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResDTO>> getTransactions() {
        var response = transactionService.getTransactions();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CreateTransactionResDTO> createTransaction(@RequestBody @Valid CreateTransactionReqDTO request) {
        var response = transactionService.createTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<CommonResDTO> processTransaction(@PathVariable String id,
                                                           @RequestParam String action) {
        var response = transactionService.processTransaction(id, action);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
