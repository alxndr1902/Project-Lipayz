package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.constant.RoleCode;
import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.paymentgateway.*;
import com.zezame.lipayz.exceptiohandler.exception.DuplicateException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.exceptiohandler.exception.OptimisticLockException;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.PaymentGateway;
import com.zezame.lipayz.model.PaymentGatewayAdmin;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.PaymentGatewayRepo;
import com.zezame.lipayz.repo.RoleRepo;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.PaymentGatewayService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PaymentGatewayServiceImpl extends BaseService implements PaymentGatewayService {
    private final PaymentGatewayRepo paymentGatewayRepo;
    private final PaymentGatewayAdminRepo paymentGatewayAdminRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PageMapper pageMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageRes<PaymentGatewayResDTO> getPaymentGateways(Pageable pageable) {
        Page<PaymentGateway> paymentGateways = paymentGatewayRepo.findAll(pageable);
        return pageMapper.toPageResponse(paymentGateways, this::mapToDto);
    }

    @Override
    public PaymentGatewayResDTO getPaymentGatewayById(String id) {
        var paymentGateway = findPaymentGatewayById(id);
        return mapToDto(paymentGateway);
    }

    private PaymentGatewayResDTO mapToDto(PaymentGateway paymentGateway) {
        return new PaymentGatewayResDTO(
                paymentGateway.getId(), paymentGateway.getCode(),
                paymentGateway.getName(), paymentGateway.getVersion());
    }

    @Override
    public CreateResDTO registerPaymentGateway(CreatePGReqDTO request) {
        if (paymentGatewayRepo.existsByCode(request.getCode())) {
            throw new DuplicateException("Code Is Not Available");
        }

        var paymentGateway = new PaymentGateway();
        paymentGateway.setCode(request.getCode());
        paymentGateway.setName(request.getName());
        paymentGateway.setRate(request.getRate());
        var savedPaymentGateway = paymentGatewayRepo.save(prepareCreate(paymentGateway));
        return new CreateResDTO(savedPaymentGateway.getId(), Message.CREATED.getDescription());
    }

    @Override
    public UpdateResDTO updatePaymentGateway(String id, UpdatePGReqDTO request) {
        var paymentGateway = validAndGetPaymentGatewayForUpdate(id, request);

        paymentGateway.setCode(request.getCode());
        paymentGateway.setName(request.getName());
        paymentGateway.setRate(request.getRate());
        var updatedPaymentGateway = paymentGatewayRepo.saveAndFlush(prepareUpdate(paymentGateway));
        return new UpdateResDTO(updatedPaymentGateway.getVersion(), Message.UPDATED.getDescription());
    }

    private PaymentGateway validAndGetPaymentGatewayForUpdate(String id, UpdatePGReqDTO request) {
        var paymentGateway = findPaymentGatewayById(id);

        if (!paymentGateway.getVersion().equals(request.getVersion())) {
            throw new OptimisticLockException("Error Updating Data, Please Refresh The Page");
        }

        if (!paymentGateway.getCode().equals(request.getCode())) {
            if (paymentGatewayRepo.existsByCode(request.getCode())) {
                throw new OptimisticLockException("Error Updating Data, Please Refresh The Page");
            }
        }

        return paymentGateway;
    }

    @Override
    public CommonResDTO deletePaymentGateway(String id) {
        var paymentGateway = findPaymentGatewayById(id);
        paymentGatewayRepo.delete(paymentGateway);
        return new CommonResDTO(Message.DELETED.getDescription());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CreateResDTO registerPaymentGatewayAdmin(String paymentGatewayId, CreatePGAdminReqDTO request) {
        LocalDateTime now = LocalDateTime.now();
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateException("Email is Not Available");
        }

        var user = createUser(request);
        var savedUser = userRepo.save(prepareCreate(user, now));

        var paymentGatewayAdmin = createPaymentGatewayAdmin(savedUser, paymentGatewayId);

        var savedPGA = paymentGatewayAdminRepo.save(prepareCreate(paymentGatewayAdmin, now));

        return new CreateResDTO(savedPGA.getId(), Message.CREATED.getDescription());
    }

    private User createUser(CreatePGAdminReqDTO request) {
        var role = roleRepo.findByCode(RoleCode.PGA.name())
                .orElseThrow(() -> new NotFoundException("Role Not Found"));

        var user = new User();
        user.setRole(role);
        user.setIsActivated(true);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getName());

        return user;
    }

    private PaymentGatewayAdmin createPaymentGatewayAdmin(User user, String paymentGatewayId) {
        var paymentGateway = findPaymentGatewayById(paymentGatewayId);

        var paymentGatewayAdmin = new PaymentGatewayAdmin();
        paymentGatewayAdmin.setUser(user);
        paymentGatewayAdmin.setPaymentGateway(paymentGateway);

        return paymentGatewayAdmin;
    }

    private PaymentGateway findPaymentGatewayById(String id) {
        var paymentGatewayId = parseUUID(id);
        return paymentGatewayRepo.findById(paymentGatewayId)
                .orElseThrow(() -> new NotFoundException("Payment Gateway Is Not Found"));
    }
}
