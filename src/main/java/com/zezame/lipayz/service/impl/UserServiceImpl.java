package com.zezame.lipayz.service.impl;

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
import com.zezame.lipayz.repo.RoleRepo;
import com.zezame.lipayz.mapper.UserMapper;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserMapper userMapper;
    private final PageMapper pageMapper;
    private final PasswordEncoder passwordEncoder;

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
        return pageMapper.toPageResponse(users, userMapper::mapToDto);
    }

    @Override
    public UserResDTO getUserById(String id) {
        var userId = parseUUID(id);
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Is Not Found"));
        return userMapper.mapToDto(user);
    }

    @Override
    public CreateResDTO registerCustomer(CreateUserReqDTO request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateException("Email Is Not Available");
        }

        var role = roleRepo.findByCode(RoleCode.CUST.name())
                .orElseThrow(() -> new NotFoundException("Role Is Not Found"));

        var activationCode = generateRandomAlphaNumeric(6);

        var customer = new User();
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setFullName(request.getName());
        customer.setRole(role);
        customer.setIsActivated(false);
        customer.setActivationCode(activationCode);

        var savedCustomer = userRepo.save(prepareCreate(customer));
        return new CreateResDTO(savedCustomer.getId(), Message.CREATED.getDescription());
    }

    @Override
    public CommonResDTO deleteCustomer(String id) {
        var customer = findCustomerById(id);
        userRepo.delete(customer);
        return new CommonResDTO(Message.DELETED.getDescription());
    }

    @Override
    public CommonResDTO activateCustomer(String email, String code) {
        var customer = userRepo.findCustomerToActivate(email, code, RoleCode.CUST.name())
                .orElseThrow(() -> new NotFoundException("Customer Is Not Found"));

        if (customer.getIsActivated() == true) {
            throw new ConflictException("This Account Has Been Activated");
        }

        customer.setIsActivated(true);
        userRepo.saveAndFlush(prepareUpdate(customer));
        return new CommonResDTO(Message.UPDATED.getDescription());
    }

    private User findCustomerById(String id) {
        var customerId = parseUUID(id);
        return userRepo.findByIdAndRoleCode(customerId, RoleCode.CUST.name())
                .orElseThrow(() -> new NotFoundException("Customer Is Not Found"));
    }
}
