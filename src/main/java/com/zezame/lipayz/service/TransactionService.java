package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionReqDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionResDTO;
import com.zezame.lipayz.dto.transaction.TransactionResDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionResDTO> getTransactions();

    CreateTransactionResDTO createTransaction(CreateTransactionReqDTO request);

    CommonResDTO processTransaction(String id, String action);
}
