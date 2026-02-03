package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.pagination.PageMeta;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.paymentgateway.CreatePGAdminReqDTO;
import com.zezame.lipayz.dto.paymentgateway.CreatePGReqDTO;
import com.zezame.lipayz.dto.paymentgateway.PaymentGatewayResDTO;
import com.zezame.lipayz.dto.paymentgateway.UpdatePGReqDTO;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.PaymentGateway;
import com.zezame.lipayz.model.PaymentGatewayAdmin;
import com.zezame.lipayz.model.Role;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.PaymentGatewayRepo;
import com.zezame.lipayz.repo.RoleRepo;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.impl.PaymentGatewayServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PaymentGatewayTest {
    @InjectMocks
    private PaymentGatewayServiceImpl paymentGatewayService;

    @Mock
    private PaymentGatewayRepo paymentGatewayRepo;

    @Mock
    private PaymentGatewayAdminRepo paymentGatewayAdminRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PrincipalService principalService;

    @Test
    public void shouldCreatePaymentGateway_WhenDataValid() {
        paymentGatewayService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(UUID.randomUUID().toString());
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        var id = UUID.randomUUID();

        var request = new CreatePGReqDTO();

        var savedPaymentGateway = new PaymentGateway();
        savedPaymentGateway.setId(id);

        Mockito.when(paymentGatewayRepo.save(Mockito.any())).thenReturn(savedPaymentGateway);
        Mockito.when(paymentGatewayRepo.existsByCode(Mockito.any())).thenReturn(false);

        var result = paymentGatewayService.registerPaymentGateway(request);

        Assertions.assertEquals(id, result.getId());

        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1)).existsByCode(Mockito.any());
    }

    @Test
    public void shouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();

        var savedPaymentGateway = new PaymentGateway();
        savedPaymentGateway.setId(id);

        List<PaymentGateway> paymentGateways = List.of(savedPaymentGateway);

        Page<PaymentGateway> page = new PageImpl<>(paymentGateways, pageable, paymentGateways.size());

        Mockito.when(paymentGatewayRepo.findAll(pageable))
                .thenReturn(page);
        Mockito.when(pageMapper.toPageResponse(Mockito.any(), Mockito.any()))
                .thenReturn(new PageRes<>(
                        List.of(new PaymentGatewayResDTO(savedPaymentGateway.getId(), null, null, null)),
                        new PageMeta(pageable.getPageNumber(), pageable.getPageSize(), paymentGateways.size())
                ));

        var result = paymentGatewayService.getPaymentGateways(pageable);

        Assertions.assertEquals(paymentGateways.size(), result.getData().size());
        Assertions.assertEquals(id, result.getData().getFirst().getId());

        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1)).findAll(pageable);
        Mockito.verify(pageMapper, Mockito.atLeast(1)).toPageResponse(Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldReturnData_whenIdValid() {
        var id = UUID.randomUUID();

        var savedPG = new PaymentGateway();
        savedPG.setId(id);

        Mockito.when(paymentGatewayRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(savedPG));

        var result = paymentGatewayService.getPaymentGatewayById(id.toString());

        Assertions.assertEquals(id, result.getId());

        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1)).findById(Mockito.any());
    }

    @Test
    public void shouldUpdateData_whenDataValid() {
        paymentGatewayService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(UUID.randomUUID().toString());
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        var paymentGatewayId = UUID.randomUUID();

        var savedPG = new PaymentGateway();
        savedPG.setId(paymentGatewayId);
        savedPG.setVersion(1);

        var paymentGatewayById = new PaymentGateway();
        paymentGatewayById.setVersion(0);
        paymentGatewayById.setCode("Test");

        var request = new UpdatePGReqDTO();
        request.setVersion(0);

        Mockito.when(paymentGatewayRepo.existsByCode(Mockito.any()))
                .thenReturn(false);
        Mockito.when(paymentGatewayRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(paymentGatewayById));
        Mockito.when(paymentGatewayRepo.saveAndFlush(Mockito.any())).thenReturn(savedPG);

        var result = paymentGatewayService.updatePaymentGateway(paymentGatewayId.toString(), request);

        Assertions.assertEquals(1, result.getVersion());

        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1))
                .existsByCode(Mockito.any());
        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1))
                .findById(Mockito.any());
        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1))
                .saveAndFlush(Mockito.any());
    }

    @Test
    public void shouldCreatePGAdmin_whenDataValid() {
        paymentGatewayService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(UUID.randomUUID().toString());
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        var userId = UUID.randomUUID();
        var PGAId = UUID.randomUUID();
        var pgId = UUID.randomUUID();

        var role = new Role();

        var user = new User();
        user.setId(userId);
        user.setRole(role);

        var paymentGateway = new PaymentGateway();
        paymentGateway.setId(pgId);

        var savedPaymentGatewayAdmin = new PaymentGatewayAdmin();
        savedPaymentGatewayAdmin.setId(PGAId);
        savedPaymentGatewayAdmin.setPaymentGateway(paymentGateway);

        var request = new CreatePGAdminReqDTO();

        Mockito.when(userRepo.existsByEmail(Mockito.any())).thenReturn(false);
        Mockito.when(roleRepo.findByCode(Mockito.any())).thenReturn(Optional.of(role));
        Mockito.when(userRepo.save(Mockito.any())).thenReturn(user);
        Mockito.when(paymentGatewayRepo.findById(Mockito.any())).thenReturn(Optional.of(paymentGateway));
        Mockito.when(paymentGatewayAdminRepo.save(Mockito.any())).thenReturn(savedPaymentGatewayAdmin);

        var result = paymentGatewayService.registerPaymentGatewayAdmin(pgId.toString(), request);

        Assertions.assertEquals(PGAId, result.getId());

        Mockito.verify(userRepo, Mockito.atLeast(1)).existsByEmail(Mockito.any());
        Mockito.verify(roleRepo, Mockito.atLeast(1)).findByCode(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(paymentGatewayRepo, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(paymentGatewayAdminRepo, Mockito.atLeast(1)).save(Mockito.any());
    }
}
