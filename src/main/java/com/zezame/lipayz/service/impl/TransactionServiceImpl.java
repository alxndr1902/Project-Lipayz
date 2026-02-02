package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.config.RabbitMQConfig;
import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.constant.TransactionStatusCode;
import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.transaction.CreateTransactionReqDTO;
import com.zezame.lipayz.dto.transaction.CreateTransactionResDTO;
import com.zezame.lipayz.dto.transaction.TransactionResDTO;
import com.zezame.lipayz.exceptiohandler.exception.ConflictException;
import com.zezame.lipayz.exceptiohandler.exception.InvalidActionException;
import com.zezame.lipayz.exceptiohandler.exception.InvalidNominalException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.*;
import com.zezame.lipayz.pojo.TransactionEmailPojo;
import com.zezame.lipayz.pojo.UpdateTransactionEmailPojo;
import com.zezame.lipayz.repo.*;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.TransactionService;
import com.zezame.lipayz.util.EmailUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl extends BaseService implements TransactionService {
    private final TransactionRepo transactionRepo;
    private final ProductRepo productRepo;
    private final PaymentGatewayRepo paymentGatewayRepo;
    private final UserRepo userRepo;
    private final TransactionStatusRepo transactionStatusRepo;
    private final HistoryRepo historyRepo;
    private final PageMapper pageMapper;
    private final RabbitTemplate rabbitTemplate;
    private final EmailUtil emailUtil;

    @Override
    public PageRes<TransactionResDTO> getTransactions(Pageable pageable) {
        var id = parseUUID(principalService.getPrincipal().getId());
        var user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User Is Not Found"));

        Page<Transaction> transactions = transactionRepo.findAll(pageable);
        return pageMapper.toPageResponse(transactions, this::mapToDto);
    }

    @Override
    public TransactionResDTO getTransactionById(String id) {
        var transactionId = parseUUID(id);
        var transaction = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction Is Not Found"));
        return mapToDto(transaction);
    }

    private TransactionResDTO mapToDto(Transaction transaction) {
        var dto = new TransactionResDTO(
                transaction.getId(), transaction.getCode(), transaction.getProduct().getName(),
                transaction.getVirtualAccountNumber(), transaction.getCustomer().getFullName(),
                transaction.getPaymentGateway().getName(), transaction.getTransactionStatus().getName(),
                transaction.getPaymentGateway().getRate(),
                transaction.getTotalPrice(), transaction.getCreatedAt()
        );

        return dto;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CreateTransactionResDTO createTransaction(CreateTransactionReqDTO request) {
        if (request.getNominal().compareTo(BigDecimal.valueOf(10000)) < 0 ) {
            throw new InvalidNominalException("Minimum Transaction Is Rp10.000");
        }

        var customer = findCustomerFromToken();

        var product = findProductFromId(request.getProductId());

        var paymentGateway = findPaymentGatewayFromId(request.getPaymentGatewayId());

        var transactionStatus = findTransactionStatusFromCode(TransactionStatusCode.PRCS.name());

        LocalDateTime now = LocalDateTime.now();

        var transaction = createTransaction(request, product, customer, paymentGateway, transactionStatus);
        var savedTransaction = transactionRepo.save(prepareCreate(transaction, now));

        var history = createHistory(transactionStatus, transaction, now);
        historyRepo.save(history);

        sendEmail(savedTransaction.getCustomer().getEmail(), savedTransaction.getCode());

        return new CreateTransactionResDTO(savedTransaction.getId(), savedTransaction.getCode(),
                Message.CREATED.getDescription());
    }

    private User findCustomerFromToken() {
        var customerId = parseUUID(principalService.getPrincipal().getId());
        var customer = userRepo.findById(customerId)
                .orElseThrow(() -> new NotFoundException("User Is Not Found"));

        return customer;
    }

    private Product findProductFromId(String id) {
        var productId = parseUUID(id);
        var product = productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Is Not Found"));

        return product;
    }

    private PaymentGateway findPaymentGatewayFromId(String id) {
        var paymentGatewayId = parseUUID(id);
        var paymentGateway = paymentGatewayRepo.findById(paymentGatewayId)
                .orElseThrow(() -> new NotFoundException("Payment Gateway Is Not Found"));

        return paymentGateway;
    }

    private TransactionStatus findTransactionStatusFromCode(String code) {
        var transactionStatus = transactionStatusRepo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Transaction Status Is Not Found"));

        return transactionStatus;
    }

    private Transaction createTransaction(CreateTransactionReqDTO request, Product product,
                                          User customer, PaymentGateway paymentGateway,
                                          TransactionStatus transactionStatus) {
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

        return transaction;
    }

    private void sendEmail(String email, String transactionCode) {
        var emailPojo = new TransactionEmailPojo(email, transactionCode);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EX_CREATE_TRANSACTION,
                RabbitMQConfig.EMAIL_ROUTING_KEY_CREATE_TRANSACTION,
                emailPojo);
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE_CREATE_TRANSACTION)
    public void receiveEmailNotificationActivation(TransactionEmailPojo emailPojo) {
        emailUtil.sendEmail(emailPojo.getCustomerEmail(),
                "Transaction Created (" + emailPojo.getTransactionCode() + ")",
                "Your Transaction Has Been Processed");
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResDTO processTransaction(String id, String action) {
        var transaction = findTransactionById(id);

        var transactionStatusCode = transaction.getTransactionStatus().getCode();

        if (!transactionStatusCode.equals(TransactionStatusCode.PRCS.name())) {
            throw new ConflictException("Transaction Is Already Processed");
        }

        LocalDateTime now = LocalDateTime.now();
        TransactionStatus status;

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

        sendEmail(transaction.getCustomer().getEmail(), transaction.getCode(), status.getName());
        return new CommonResDTO(Message.UPDATED.getDescription());
    }

    private void sendEmail(String email, String transactionCode, String statusName) {
        var emailPojo = new UpdateTransactionEmailPojo(email, transactionCode, statusName);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EX_UPDATE_TRANSACTION,
                RabbitMQConfig.EMAIL_ROUTING_KEY_UPDATE_TRANSACTION,
                emailPojo);
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE_UPDATE_TRANSACTION)
    public void receiveEmailNotificationActivation(UpdateTransactionEmailPojo emailPojo) {
        emailUtil.sendEmail(emailPojo.getCustomerEmail(),
                "Transaction Updated (" + emailPojo.getTransactionCode() + ")",
                "Your Transaction status is " + emailPojo.getStatusName());
    }

    private Transaction findTransactionById(String id) {
        var transactionId = parseUUID(id);

        return transactionRepo.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction Is Not Found"));
    }

    private History createHistory(TransactionStatus status, Transaction transaction, LocalDateTime now) {
        var history = new History();
        history.setTransaction(transaction);
        history.setTransactionStatus(status);
        return prepareCreate(history, now);
    }
}
