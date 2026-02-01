package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.constant.RoleCode;
import com.zezame.lipayz.constant.TransactionStatusCode;
import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionReqDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionResDTO;
import com.zezame.lipayz.dto.transaction.TransactionResDTO;
import com.zezame.lipayz.exceptiohandler.exception.ConflictException;
import com.zezame.lipayz.exceptiohandler.exception.ForbiddenException;
import com.zezame.lipayz.exceptiohandler.exception.InvalidActionException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.mapper.TransactionMapper;
import com.zezame.lipayz.model.History;
import com.zezame.lipayz.model.Transaction;
import com.zezame.lipayz.model.TransactionStatus;
import com.zezame.lipayz.repo.*;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl extends BaseService implements TransactionService {
    private final TransactionRepo transactionRepo;
    private final ProductRepo productRepo;
    private final PaymentGatewayRepo paymentGatewayRepo;
    private final UserRepo userRepo;
    private final TransactionStatusRepo transactionStatusRepo;
    private final HistoryRepo historyRepo;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionResDTO> getTransactions() {
        List<Transaction> transactions = transactionRepo.findAll();
        List<TransactionResDTO> DTOs = new ArrayList<>();
        for (var transaction : transactions) {
            DTOs.add(transactionMapper.mapToDto(transaction));
        }
        return DTOs;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CreateTransactionResDTO createTransaction(CreateTransactionReqDTO request) {
        var customerId = parseUUID(principalService.getPrincipal().getId());
        var customer = userRepo.findById(customerId)
                .orElseThrow(() -> new NotFoundException("User Is Not Found"));

        if (!customer.getRole().getCode().equals(RoleCode.CUST.name())) {
            throw new ForbiddenException("You Are Forbidden To Perform This Action");
        }

        var productId = parseUUID(request.getProductId());
        var product = productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Is Not Found"));

        var paymentGatewayId = parseUUID(request.getPaymentGatewayId());
        var paymentGateway = paymentGatewayRepo.findById(paymentGatewayId)
                .orElseThrow(() -> new NotFoundException("Payment Gateway Is Not Found"));

        var transactionStatus = transactionStatusRepo.findByCode(TransactionStatusCode.PRCS.name())
                .orElseThrow(() -> new NotFoundException("Transaction Status Is Not Found"));

        LocalDateTime now = LocalDateTime.now();

        var transaction = new Transaction();
        transaction.setCode(generateRandomAlphaNumeric(20));
        transaction.setProduct(product);
        transaction.setNominal(request.getNominal());
        transaction.setVirtualAccountNumber(request.getVirtualAccountNumber());
        transaction.setCustomer(customer);
        transaction.setPaymentGateway(paymentGateway);
        transaction.setTransactionStatus(transactionStatus);

        BigDecimal totalPrice = request.getNominal().add(paymentGateway.getRate());

        transaction.setTotalPrice(totalPrice);
        var savedTransaction = transactionRepo.save(prepareCreate(transaction, now));

        var history = createHistory(transactionStatus, transaction, now);
        historyRepo.save(history);

        return new CreateTransactionResDTO(savedTransaction.getId(), savedTransaction.getCode(),
                Message.CREATED.getDescription());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResDTO processTransaction(String id, String action) {
        var transactionId = parseUUID(id);

        var transaction = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction Is Not Found"));

        var transactionStatusCode = transaction.getTransactionStatus().getCode();

        if (transactionStatusCode.equals(TransactionStatusCode.SCS.name()) ||
                transactionStatusCode.equals(TransactionStatusCode.RJC.name())) {
            throw new ConflictException("Transaction Is Already Processed");
        }

        LocalDateTime now = LocalDateTime.now();
        TransactionStatus status = null;

        switch (action) {
            case "ACCEPT" -> {
                status = transactionStatusRepo.findByCode(TransactionStatusCode.SCS.name())
                        .orElseThrow(() -> new NotFoundException("Transaction Status Is Not Found"));
                transaction.setTransactionStatus(status);
            }
            case "REJECT" -> {
                status = transactionStatusRepo.findByCode(TransactionStatusCode.RJC.name())
                        .orElseThrow(() -> new NotFoundException("Transaction Status Is Not Found"));
                transaction.setTransactionStatus(status);
            }
            default -> throw new InvalidActionException("Wrong Parameter");
        }
        transactionRepo.saveAndFlush(prepareUpdate(transaction, now));

        var history = createHistory(status, transaction, now);
        historyRepo.save(history);
        return new CommonResDTO(Message.UPDATED.getDescription());
    }

    private History createHistory(TransactionStatus status, Transaction transaction, LocalDateTime now) {
        var history = new History();
        history.setTransaction(transaction);
        history.setTransactionStatus(status);
        return prepareCreate(history, now);
    }
}
