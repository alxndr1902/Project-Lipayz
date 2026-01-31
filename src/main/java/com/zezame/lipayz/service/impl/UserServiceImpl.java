package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.constant.RoleCode;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.paymentgateway.CreatePGReqDTO;
import com.zezame.lipayz.dto.user.CreateUserReqDTO;
import com.zezame.lipayz.dto.user.UserResDTO;
import com.zezame.lipayz.exceptiohandler.exception.DuplicateException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.mapper.RoleRepo;
import com.zezame.lipayz.mapper.UserMapper;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl extends BaseService implements UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final UserMapper userMapper;
//    private final PasswordEncoder passwordEncoder;

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
    public List<UserResDTO> getUsers(String roleCode) {
        List<User> users = userRepo.findAll();
        List<UserResDTO> DTOs = new ArrayList<>();
        for (var user : users) {
            DTOs.add(userMapper.mapToDto(user));
        }
        return DTOs;
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

        var user = new User();
        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPassword(request.getPassword());
        user.setFullName(request.getName());
        user.setRole(role);
        user.setIsActivated(false);
        return null;
    }

    @Override
    public CreateResDTO registerPaymentGateway(CreatePGReqDTO request) {
//        var paymentGateway = new PaymentGateway();
//        paymentGateway.
        return null;
    }
}
