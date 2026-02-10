package com.zezame.lipayz.service;

import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.constant.TransactionStatusCode;
import com.zezame.lipayz.dto.pagination.PageMeta;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.transaction.CreateTransactionReqDTO;
import com.zezame.lipayz.dto.transaction.TransactionResDTO;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.*;
import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.repo.*;
import com.zezame.lipayz.service.impl.TransactionServiceImpl;
import com.zezame.lipayz.util.EmailUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionTest {
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private PaymentGatewayRepo paymentGatewayRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TransactionStatusRepo transactionStatusRepo;

    @Mock
    private HistoryRepo historyRepo;

    @Mock
    private PaymentGatewayAdminRepo paymentGatewayAdminRepo;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private PrincipalService principalService;

    @Test
    public void shouldCreateData_whenDataValid() {
        transactionService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(UUID.randomUUID().toString(), "CUST");
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        var transactionId = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        var pgId = UUID.randomUUID();
        var productId = UUID.randomUUID();
        var statusId = UUID.randomUUID();

        var customer = new User();
        customer.setId(customerId);

        var pg = new PaymentGateway();
        pg.setId(pgId);
        pg.setRate(BigDecimal.valueOf(3000));

        var status = new TransactionStatus();
        status.setId(statusId);

        var product = new Product();
        product.setId(productId);

        var request = new CreateTransactionReqDTO();
        request.setNominal(BigDecimal.valueOf(10000));

        var savedTransaction = new Transaction();
        savedTransaction.setId(transactionId);
        request.setProductId(productId.toString());
        request.setPaymentGatewayId(pgId.toString());
        request.setVirtualAccountNumber("1234567890");

        var savedHistory = new History();
        savedHistory.setTransaction(savedTransaction);

        Mockito.when(userRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(customer));
        Mockito.when(productRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(product));
        Mockito.when(paymentGatewayRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(pg));
        Mockito.when(transactionStatusRepo.findByCode(Mockito.any()))
                .thenReturn(Optional.of(status));
        Mockito.when(transactionRepo.save(Mockito.any()))
                .thenReturn(savedTransaction);
        Mockito.when(historyRepo.save(Mockito.any()))
                .thenReturn(savedHistory);

        var result = transactionService.createTransaction(request);

        Assertions.assertEquals(transactionId, result.getId());

        Mockito.verify(userRepo, Mockito.atLeast(1))
                .findById(Mockito.any());
        Mockito.verify(productRepo, Mockito.atLeast(1))
                .findById(Mockito.any());
        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1))
                .findById(Mockito.any());
        Mockito.verify(transactionStatusRepo, Mockito.atLeast(1))
                .findByCode(Mockito.any());
        Mockito.verify(historyRepo, Mockito.atLeast(1))
                .save(Mockito.any());
        Mockito.verify(transactionRepo, Mockito.atLeast(1))
                .save(Mockito.any());
    }

    @Test
    public void shouldUpdateTransactionStatus_whenDataValid() {
        transactionService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(UUID.randomUUID().toString(), "PGA");
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        UUID transactionId = UUID.randomUUID();
        UUID pgId = UUID.randomUUID();

        TransactionStatus prcsStatus = new TransactionStatus();
        prcsStatus.setCode(TransactionStatusCode.PRCS.name());

        TransactionStatus successStatus = new TransactionStatus();
        successStatus.setCode(TransactionStatusCode.SCS.name());

        PaymentGateway pg = new PaymentGateway();
        pg.setId(pgId);

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setPaymentGateway(pg);
        transaction.setTransactionStatus(prcsStatus);

        PaymentGatewayAdmin admin = new PaymentGatewayAdmin();
        admin.setPaymentGateway(pg);

        Mockito.when(principalService.getUserId())
                .thenReturn(UUID.fromString(auth.getId()));
        Mockito.when(transactionRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(transaction));
        Mockito.when(paymentGatewayAdminRepo.findByUser_Id(Mockito.any()))
                .thenReturn(Optional.of(admin));
        Mockito.when(transactionStatusRepo.findByCode(TransactionStatusCode.SCS.name()))
                .thenReturn(Optional.of(successStatus));
        Mockito.when(transactionRepo.saveAndFlush(Mockito.any()))
                .thenReturn(transaction);
        Mockito.when(historyRepo.save(Mockito.any()))
                .thenReturn(new History());

        var result = transactionService.processTransaction(
                transactionId.toString(),
                "ACCEPT"
        );
        Assertions.assertEquals(
                Message.UPDATED.getDescription(),
                result.getMessage()
        );
        Mockito.verify(transactionRepo).findById(Mockito.any());
        Mockito.verify(paymentGatewayAdminRepo).findByUser_Id(Mockito.any());
        Mockito.verify(transactionStatusRepo).findByCode(TransactionStatusCode.SCS.name());
        Mockito.verify(transactionRepo).saveAndFlush(Mockito.any());
        Mockito.verify(historyRepo).save(Mockito.any());
    }

    @Test
    public void shouldReturnAll_whenRoleIsSa() {
        transactionService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(UUID.randomUUID().toString(), "SA");
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);
        var pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();

        var savedTransaction = new Transaction();
        savedTransaction.setId(id);

        List<Transaction> transactions = List.of(savedTransaction);

        Page<Transaction> page = new PageImpl<>(transactions, pageable, transactions.size());

        Mockito.when(transactionRepo.findAll(pageable))
                .thenReturn(page);
        Mockito.when(pageMapper.toPageResponse(Mockito.any(), Mockito.any()))
                .thenReturn(new PageRes<>(
                        List.of(new TransactionResDTO(savedTransaction.getId(),
                                null, null, null, null,
                                null, null, null,
                                null, null)),
                        new PageMeta(page.getNumber(), pageable.getPageSize(), page.getTotalElements())
                ));

        var result = transactionService.getTransactions(1, 10);

        Assertions.assertEquals(transactions.size(), result.getData().size());
        Assertions.assertEquals(id, result.getData().getFirst().getId());

        Mockito.verify(transactionRepo, Mockito.atLeast(1)).findAll(pageable);
        Mockito.verify(pageMapper, Mockito.atLeast(1)).toPageResponse(Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldReturnAll_whenRoleIsCustomer() {
        var customerId = UUID.randomUUID();
        transactionService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(customerId.toString(), "CUST");
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        var pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();

        var customer = new User();
        customer.setId(customerId);

        var savedTransaction = new Transaction();
        savedTransaction.setId(id);
        savedTransaction.setCustomer(customer);

        List<Transaction> transactions = List.of(savedTransaction);

        Page<Transaction> page = new PageImpl<>(transactions, pageable, transactions.size());

        Mockito.when(transactionRepo.findByCustomer(pageable, customerId))
                .thenReturn(page);
        Mockito.when(pageMapper.toPageResponse(Mockito.any(), Mockito.any()))
                .thenReturn(new PageRes<>(
                        List.of(new TransactionResDTO(savedTransaction.getId(),
                                null, null, null, null,
                                null, null, null,
                                null, null)),
                        new PageMeta(page.getNumber(), pageable.getPageSize(), page.getTotalElements())
                ));

        var result = transactionService.getTransactions(1, 10);

        Assertions.assertEquals(transactions.size(), result.getData().size());
        Assertions.assertEquals(id, result.getData().getFirst().getId());

        Mockito.verify(transactionRepo, Mockito.atLeast(1)).findByCustomer(pageable, customerId);
        Mockito.verify(pageMapper, Mockito.atLeast(1)).toPageResponse(Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldReturnAll_whenRoleIsPGA() {
        var pgaId = UUID.randomUUID();
        transactionService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(pgaId.toString(), "PGA");
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        var pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();

        var pg = new PaymentGateway();

        var pgaUser = new User();
        pgaUser.setId(pgaId);

        var pga = new PaymentGatewayAdmin();
        pga.setPaymentGateway(pg);

        var savedTransaction = new Transaction();
        savedTransaction.setId(id);
        savedTransaction.setPaymentGateway(pg);

        List<Transaction> transactions = List.of(savedTransaction);

        Page<Transaction> page = new PageImpl<>(transactions, pageable, transactions.size());

        Mockito.when(transactionRepo.findByPaymentGateway(pageable, pgaId))
                .thenReturn(page);
        Mockito.when(pageMapper.toPageResponse(Mockito.any(), Mockito.any()))
                .thenReturn(new PageRes<>(
                        List.of(new TransactionResDTO(savedTransaction.getId(),
                                null, null, null, null,
                                null, null, null,
                                null, null)),
                        new PageMeta(page.getNumber(), pageable.getPageSize(), page.getTotalElements())
                ));

        var result = transactionService.getTransactions(1, 10);

        Assertions.assertEquals(transactions.size(), result.getData().size());
        Assertions.assertEquals(id, result.getData().getFirst().getId());

        Mockito.verify(transactionRepo, Mockito.atLeast(1)).findByPaymentGateway(pageable, pgaId);
        Mockito.verify(pageMapper, Mockito.atLeast(1)).toPageResponse(Mockito.any(), Mockito.any());
    }
}
