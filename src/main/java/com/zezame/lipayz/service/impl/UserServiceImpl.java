package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.config.RabbitMQConfig;
import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.constant.RoleCode;
import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.user.CreateUserReqDTO;
import com.zezame.lipayz.dto.user.UserResDTO;
import com.zezame.lipayz.exceptiohandler.exception.ConflictException;
import com.zezame.lipayz.exceptiohandler.exception.DuplicateException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.BaseModel;
import com.zezame.lipayz.model.Role;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.pojo.ActivateCustomerEmailPojo;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.RoleRepo;
import com.zezame.lipayz.repo.TransactionRepo;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.UserService;
import com.zezame.lipayz.util.EmailUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl extends BaseService implements UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PageMapper pageMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final EmailUtil emailUtil;
    private final TransactionRepo transactionRepo;
    private final PaymentGatewayAdminRepo paymentGatewayAdminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new org.springframework.security.core.userdetails.User(
                email, user.getPassword(), new ArrayList<>());
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Is Not Found"));
    }

    @Override
    public PageRes<UserResDTO> getUsers(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        return pageMapper.toPageResponse(users, this::mapToDto);
    }

    @Override
    public UserResDTO getUserById(String id) {
        var userId = parseUUID(id);
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Is Not Found"));
        return mapToDto(user);
    }

    private UserResDTO mapToDto(User user) {
        var dto = new UserResDTO(
                user.getId(), user.getFullName(), user.getRole().getName(), user.getVersion());

        return dto;
    }

    @Override
    public CreateResDTO registerUser(CreateUserReqDTO request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateException("Email Is Not Available");
        }

        var role = roleRepo.findByCode(RoleCode.CUST.name())
                .orElseThrow(() -> new NotFoundException("Role Is Not Found"));

        var customer = createCustomer(request, role);

        var savedCustomer = userRepo.save(prepareRegister(customer, userRepo));
        sendEmail(savedCustomer, savedCustomer.getActivationCode());
        return new CreateResDTO(savedCustomer.getId(), Message.CREATED.getDescription());
    }

    private User createCustomer(CreateUserReqDTO request, Role role) {
        var activationCode = generateRandomAlphaNumeric(6);

        var customer = new User();
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setFullName(request.getName());
        customer.setRole(role);
        customer.setIsActivated(false);
        customer.setActivationCode(activationCode);

        return customer;
    }

    private void sendEmail(User customer, String activationCode) {
        var link = "http://localhost:8080/users/activate?email=" + customer.getEmail() + "&code=" + activationCode;
        var emailPojo = new ActivateCustomerEmailPojo(customer, link);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EX_ACTIVATION,
                RabbitMQConfig.EMAIL_ROUTING_KEY_ACTIVATION,
                emailPojo);
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE_ACTIVATION)
    public void receiveEmailNotifcationActivation(ActivateCustomerEmailPojo emailPojo) throws MessagingException {
        emailUtil.sendWelcomeEmail(emailPojo.getCustomer(), emailPojo.getActivationLink());
    }

    @Override
    public CommonResDTO deleteUser(String id) {
        var customer = findCustomerById(id);

        if (transactionRepo.existsByCustomer(customer)) {
            throw new ConflictException("Customer Cannot Be Deleted, Because They Have Transaction History");
        }

        if (customer.getRole().getCode().equals(RoleCode.PGA.name())) {
            var paymentGatewayAdmin = paymentGatewayAdminRepo.findByUser(customer);
            if (transactionRepo.existsByUpdatedByEquals(paymentGatewayAdmin.getId())) {
                throw new ConflictException("This Payment Gateway Admin Cannot Be Deleted, Because They Have Transactions History");
            }

            paymentGatewayAdminRepo.delete(paymentGatewayAdmin);
        }

        userRepo.delete(customer);
        return new CommonResDTO(Message.DELETED.getDescription());
    }

    @Override
    public CommonResDTO activateCustomer(String email, String code) {
        var customer = userRepo.findCustomerToActivate(email, code)
                .orElseThrow(() -> new NotFoundException("Customer Is Not Found"));

        if (customer.getIsActivated()) {
            throw new ConflictException("This Account Has Been Activated");
        }

        customer.setIsActivated(true);
        userRepo.saveAndFlush(prepareActivate(customer, userRepo));
        return new CommonResDTO(Message.UPDATED.getDescription());
    }

    private User findCustomerById(String id) {
        var customerId = parseUUID(id);
        return userRepo.findByIdAndRoleCode(customerId, RoleCode.CUST.name())
                .orElseThrow(() -> new NotFoundException("Customer Is Not Found"));
    }

//    private User prepareRegister(User model) {
//        var system = roleRepo.findByCode(RoleCode.SYS.name())
//                .orElseThrow(() -> new NotFoundException("Role Is Not Found"));
//        model.setId(UUID.randomUUID());
//        model.setCreatedAt(LocalDateTime.now());
//        model.setCreatedBy(system.getId());
//        return model;
//    }
//
//    private User prepareActivate(User model) {
//        var system = roleRepo.findByCode(RoleCode.SYS.name())
//                .orElseThrow(() -> new NotFoundException("Role Is Not Found"));
//        model.setUpdatedAt(LocalDateTime.now());
//        model.setUpdatedBy(system.getId());
//        return model;
//    }
}
