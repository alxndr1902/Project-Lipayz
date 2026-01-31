package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.constant.RoleCode;
import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.paymentgateway.*;
import com.zezame.lipayz.exceptiohandler.exception.DuplicateException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.mapper.PaymentGatewayMapper;
import com.zezame.lipayz.mapper.RoleRepo;
import com.zezame.lipayz.model.PaymentGateway;
import com.zezame.lipayz.model.PaymentGatewayAdmin;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.PaymentGatewayRepo;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.PaymentGatewayService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentGatewayServiceImpl extends BaseService implements PaymentGatewayService {
    private final PaymentGatewayRepo paymentGatewayRepo;
    private final PaymentGatewayAdminRepo paymentGatewayAdminRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PaymentGatewayMapper paymentGatewayMapper;

    @Override
    public List<PaymentGatewayResDTO> getPaymentGateways() {
        List<PaymentGateway> paymentGateways = paymentGatewayRepo.findAll();
        List<PaymentGatewayResDTO> DTOs = new ArrayList<>();
        for (var paymentGateway : paymentGateways) {
            DTOs.add(paymentGatewayMapper.mapToDto(paymentGateway));
        }
        return DTOs;
    }

    @Override
    public PaymentGatewayResDTO getPaymentGatewayById(String id) {
        var paymentGateway = findPaymentGatewayById(id);
        return paymentGatewayMapper.mapToDto(paymentGateway);
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
        var paymentGateway = findPaymentGatewayById(id);
        paymentGateway.setCode(request.getCode());
        paymentGateway.setName(request.getName());
        paymentGateway.setRate(request.getRate());
        var updatedPaymentGateway = paymentGatewayRepo.saveAndFlush(prepareUpdate(paymentGateway));
        return new UpdateResDTO(updatedPaymentGateway.getVersion(), Message.UPDATED.getDescription());
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
        if (paymentGatewayAdminRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateException("Email is Not Available");
        }

        var role = roleRepo.findByCode(RoleCode.PGA.name())
                .orElseThrow(() -> new NotFoundException("Role Not Found"));

        var user = new User();
        user.setRole(role);
        user.setIsActivated(true);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullName(request.getName());
        var savedUser = userRepo.save(prepareCreate(user));

        var paymentGateway = findPaymentGatewayById(paymentGatewayId);

        var paymentGatewayAdmin = new PaymentGatewayAdmin();
        paymentGatewayAdmin.setUser(savedUser);
        paymentGatewayAdmin.setPaymentGateway(paymentGateway);

        var savedPGA = paymentGatewayAdminRepo.save(prepareCreate(paymentGatewayAdmin));

        return new CreateResDTO(savedPGA.getId(), Message.CREATED.getDescription());
    }

    @Override
    public List<PaymentGatewayAdminResDTO> getPaymentGatewayAdmins(String paymentGatewayId) {
        List<PaymentGatewayAdmin> admins = paymentGatewayAdminRepo.findAll();
        List<PaymentGatewayAdminResDTO> DTOs = new ArrayList<>();
        for (PaymentGatewayAdmin admin : admins) {
            DTOs.add(paymentGatewayMapper.mapToDto(admin));
        }
        return DTOs;
    }

    @Override
    public CommonResDTO deletePaymentGatewayAdmin(String id) {
        var PGAdmin = findPaymentGatewayAdmin(id);
        paymentGatewayAdminRepo.delete(PGAdmin);
        return new CommonResDTO(Message.DELETED.getDescription());
    }

    private PaymentGateway findPaymentGatewayById(String id) {
        var paymentGatewayId = parseUUID(id);
        return paymentGatewayRepo.findById(paymentGatewayId)
                .orElseThrow(() -> new NotFoundException("Payment Gateway Is Not Found"));
    }

    private PaymentGatewayAdmin findPaymentGatewayAdmin(String id) {
        var paymentGatewayAdminId = parseUUID(id);
        return paymentGatewayAdminRepo.findById(paymentGatewayAdminId)
                .orElseThrow(() -> new NotFoundException("Payment Gateway Admin Is Not Found"));
    }
}
