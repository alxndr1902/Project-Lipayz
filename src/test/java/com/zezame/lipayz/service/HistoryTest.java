package com.zezame.lipayz.service;

import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.History;
import com.zezame.lipayz.model.Transaction;
import com.zezame.lipayz.model.TransactionStatus;
import com.zezame.lipayz.repo.HistoryRepo;
import com.zezame.lipayz.service.impl.HistoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class HistoryTest {
    @InjectMocks
    private HistoryServiceImpl historyService;

    @Mock
    private HistoryRepo historyRepo;

    private PageMapper pageMapper = new PageMapper();

    @BeforeEach
    void setup() {
        historyService = new HistoryServiceImpl(historyRepo, pageMapper);
    }

    @Test
    public void shouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();

        var transaction = new Transaction();

        var transactionStatus = new TransactionStatus();

        var savedHistory = new History();
        savedHistory.setTransaction(transaction);
        savedHistory.setTransactionStatus(transactionStatus);
        savedHistory.setId(id);

        List<History> histories = List.of(savedHistory);

        Page<History> page = new PageImpl<>(histories, pageable, histories.size());

        Mockito.when(historyRepo.findAll(pageable))
                .thenReturn(page);

        var result = historyService.getHistories(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(histories.size(), result.getData().size());
        Assertions.assertEquals(id, result.getData().getFirst().getId());

        Mockito.verify(historyRepo, Mockito.times(1)).findAll(pageable);
    }
}
