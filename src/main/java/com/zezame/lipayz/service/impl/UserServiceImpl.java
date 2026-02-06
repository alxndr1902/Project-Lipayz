package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.config.RabbitMQConfig;
import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.constant.RoleCode;
import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.user.*;
import com.zezame.lipayz.exceptiohandler.exception.*;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.Role;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.pojo.ActivateCustomerEmailPojo;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.RoleRepo;
import com.zezame.lipayz.repo.TransactionRepo;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.UserService;
import com.zezame.lipayz.specification.UserSpecification;
import com.zezame.lipayz.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    @Cacheable(value = "user",
            key = "'page:' + #page + 'size:' + #size + 'name:' + #name")
    @Override
    public PageRes<UserResDTO> getUsers(Integer page, Integer size, String name) {
        validatePaginationParam(page, size);

        Specification<User> spec = UserSpecification.hasName(name);

        Pageable pageable = PageRequest.of((page - 1), size);
        Page<User> users = userRepo.findAll(spec, pageable);
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
        return new UserResDTO(
                user.getId(), user.getFullName(), user.getRole().getName(), user.getVersion());
    }

    @CacheEvict(value = "user", allEntries = true)
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
    public void receiveEmailNotifcationActivation(ActivateCustomerEmailPojo emailPojo){
        emailUtil.sendWelcomeEmail(emailPojo.getCustomer(), emailPojo.getActivationLink());
    }

    @CacheEvict(value = "user", allEntries = true)
    @Override
    public CommonResDTO deleteUser(String id) {
        var customer = validateAndGetCustomerForDelete(id);

        userRepo.delete(customer);
        return new CommonResDTO(Message.DELETED.getDescription());
    }

    private User validateAndGetCustomerForDelete(String id) {
        var customer = findUserById(id);

        if (transactionRepo.existsByCustomer(customer)) {
            throw new ConflictException("Customer Cannot Be Deleted, Because They Have Transaction History");
        }

        if (customer.getRole().getCode().equals(RoleCode.PGA.name())) {
            var paymentGatewayAdmin = paymentGatewayAdminRepo.findByUser(customer);
            if (transactionRepo.countPGAById(paymentGatewayAdmin.getId()) > 0) {
                throw new ConflictException(
                        "This Payment Gateway Admin Cannot Be Deleted, Because They Have Transactions History");
            }

            paymentGatewayAdminRepo.delete(paymentGatewayAdmin);
        }

        return customer;
    }

    @Override
    public void activateCustomer(String email, String code) throws ActivationFailedException {
        var customer = userRepo.findCustomerToActivate(email, code)
                .orElseThrow(() -> new ActivationFailedException("Customer Is Not Found"));

        if (customer.getIsActivated()) {
            throw new ActivationFailedException("This Account Has Been Activated");
        }

        customer.setIsActivated(true);
        userRepo.saveAndFlush(prepareActivate(customer, userRepo));
    }

    @Override
    public CommonResDTO changePassword(ChangePasswordDto request) {
        var user = findUserById(principalService.getPrincipal().getId());

        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new ConflictException("Old Password Do Not Match");
        }

        user.setPassword(request.getNewPassword());
        userRepo.saveAndFlush(user);
        return new CommonResDTO(Message.UPDATED.getDescription());
    }

    @Override
    public CommonResDTO adminChangePassword(AdminChangePasswordDto request, String id) {
        if (userLoginIsNotSA()) {
            throw new ForbiddenException("You are Not Allowed to Perform This Operation");
        }

        var user = findUserById(id);
        user.setPassword(request.getNewPassword());

        return new CommonResDTO(Message.UPDATED.getDescription());
    }

    @Override
    public CommonResDTO updateUser(UpdateUserReqDTO request, String id) {
        User user;

        if (id != null && !id.isBlank()) {
            if (userLoginIsNotSA()) {
                throw new ForbiddenException("You are Not Allowed to Perform This Operation");
            }
            user = findUserById(id);
        } else {
            user = findUserById(principalService.getPrincipal().getId());
        }

        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepo.existsByEmail(request.getEmail())) {
                throw new DuplicateException("Email Is Not Available");
            }
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        userRepo.saveAndFlush(user);

        return new CommonResDTO(Message.UPDATED.getDescription());
    }

    private boolean userLoginIsNotSA() {
        var role = principalService.getPrincipal().getRoleCode();

        return !role.equals(RoleCode.SA.name());
    }

    private User findUserById(String id) {
        var customerId = parseUUID(id);
        return userRepo.findByIdAndRoleCode(customerId, RoleCode.CUST.name())
                .orElseThrow(() -> new NotFoundException("Customer Is Not Found"));
    }
}
