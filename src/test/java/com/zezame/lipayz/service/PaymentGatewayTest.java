package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.paymentgateway.CreatePGReqDTO;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.PaymentGateway;
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

import java.security.Principal;
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
}
