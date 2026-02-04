package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.transaction.CreateTransactionReqDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionResDTO;
import com.zezame.lipayz.dto.transaction.TransactionResDTO;

public interface TransactionService {
    PageRes<TransactionResDTO> getTransactions(Integer page, Integer size);

    CreateTransactionResDTO createTransaction(CreateTransactionReqDTO request);

    CommonResDTO processTransaction(String id, String action);
}
