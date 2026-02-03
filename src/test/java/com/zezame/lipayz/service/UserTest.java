package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.pagination.PageMeta;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.user.CreateUserReqDTO;
import com.zezame.lipayz.dto.user.UserResDTO;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.Role;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.RoleRepo;
import com.zezame.lipayz.repo.TransactionRepo;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.impl.UserServiceImpl;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private PaymentGatewayAdminRepo paymentGatewayAdminRepo;

    @Mock
    private PrincipalService principalService;

    @Test
    public void shouldCreateCustomer_WhenDataValid() {
        var id = UUID.randomUUID();
        var sysId = UUID.randomUUID();

        var role = new Role();
        role.setCode("CUST");

        var sys = new User();
        sys.setId(sysId);

        var savedUser = new User();
        savedUser.setId(id);
        savedUser.setRole(role);

        var request = new CreateUserReqDTO();

        Mockito.when(userRepo.findSystem())
                        .thenReturn(Optional.of(sys));
        Mockito.when(roleRepo.findByCode("CUST"))
                        .thenReturn(Optional.of(role));
        Mockito.when(userRepo.existsByEmail(Mockito.any()))
                .thenReturn(false);
        Mockito.when(userRepo.save(Mockito.any()))
                .thenReturn(savedUser);

        var result = userService.registerUser(request);

        Assertions.assertEquals(id, result.getId());

        Mockito.verify(userRepo, Mockito.atLeast(1)).findSystem();
        Mockito.verify(userRepo, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).existsByEmail(Mockito.any());
        Mockito.verify(roleRepo, Mockito.atLeast(1)).findByCode(Mockito.any());
    }

    @Test
    public void shouldReturnData_WhenIdValid() {
        var id = UUID.randomUUID();

        var role = new Role();
        role.setName("Customer");

        var savedUser = new User();
        savedUser.setId(id);
        savedUser.setRole(role);

        Mockito.when(userRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(savedUser));

        var result = userService.getUserById(id.toString());

        Assertions.assertEquals(id, result.getId());

        Mockito.verify(userRepo, Mockito.atLeast(1))
                .findById(Mockito.any());

    }

    @Test
    public void shoudlReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();

        var role = new Role();
        role.setName("Customer");

        var savedUser = new User();
        savedUser.setId(id);
        savedUser.setRole(role);

        List<User> users = List.of(savedUser);

        Page<User> page = new PageImpl(users, pageable, users.size());

        Mockito.when(userRepo.findAll(pageable))
                .thenReturn(page);

        Mockito.when(pageMapper.toPageResponse(Mockito.any(), Mockito.any()))
                .thenReturn(new PageRes<>(
                        List.of(new UserResDTO(id, null, null, null)),
                        new PageMeta(0, 10, users.size())
                ));

        var result = userService.getUsers(pageable);

        Assertions.assertEquals(users.size(), result.getData().size());
        Assertions.assertEquals(id, result.getData().getFirst().getId());

        Mockito.verify(userRepo, Mockito.atLeast(1)).findAll(pageable);
    }
}
