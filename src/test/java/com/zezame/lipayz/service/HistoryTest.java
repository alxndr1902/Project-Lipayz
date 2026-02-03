package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageMeta;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.*;
import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.repo.HistoryRepo;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.UserRepo;
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
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class HistoryTest {
    @InjectMocks
    private HistoryServiceImpl historyService;

    @Mock
    private HistoryRepo historyRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private PaymentGatewayAdminRepo paymentGatewayAdminRepo;

    @Mock
    private PrincipalService principalService;

    @Mock
    private PageMapper pageMapper;

//    private final PageMapper pageMapper = new PageMapper();

//    @BeforeEach
//    void setup() {
//        historyService = new HistoryServiceImpl(historyRepo, userRepo, paymentGatewayAdminRepo, pageMapper);
//    }

    @Test
    public void shouldReturnAll() {
        var userId = UUID.randomUUID();
        historyService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(userId.toString(), "CUST");
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);


        var role = new Role();
        role.setCode("CUST");

        var user = new User();
        user.setId(userId);
        user.setRole(role);


        Pageable pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();

        var transaction = new Transaction();
        transaction.setCustomer(user);

        var transactionStatus = new TransactionStatus();

        var savedHistory = new History();
        savedHistory.setTransaction(transaction);
        savedHistory.setTransactionStatus(transactionStatus);
        savedHistory.setId(id);

        List<History> histories = List.of(savedHistory);

        Page<History> page = new PageImpl<>(histories, pageable, histories.size());

        Mockito.when(historyRepo.findByCustomer(userId.toString(), pageable))
                .thenReturn(page);

        Mockito.when(pageMapper.toPageResponse(Mockito.any(), Mockito.any()))
                .thenReturn(new PageRes<>(
                        List.of(new HistoryResDTO(id, null, null, null)),
                        new PageMeta(0, 10, histories.size())
                ));

        var result = historyService.getHistories(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(histories.size(), result.getData().size());
        Assertions.assertEquals(id, result.getData().getFirst().getId());

        Mockito.verify(historyRepo, Mockito.times(1)).findByCustomer(user.getId().toString(), pageable);
    }
}
