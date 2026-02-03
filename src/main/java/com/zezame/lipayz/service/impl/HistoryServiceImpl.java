package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.History;
import com.zezame.lipayz.repo.HistoryRepo;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HistoryServiceImpl extends BaseService implements HistoryService {
    private final HistoryRepo historyRepo;
    private final UserRepo userRepo;
    private final PaymentGatewayAdminRepo paymentGatewayAdminRepo;
    private final PageMapper pageMapper;

    @Override
    public PageRes<HistoryResDTO> getHistories(Pageable pageable) {
        String role = principalService.getPrincipal().getRoleCode();
        String id = principalService.getPrincipal().getId();
        Page<History> histories = null;

        switch (role) {
            case "CUST" -> histories = historyRepo.findByCustomer(id, pageable);
            case "PGA" -> histories = historyRepo.findByPaymentGateway(id, pageable);
            case "SA" -> histories = historyRepo.findAll(pageable);
        }

        return pageMapper.toPageResponse(histories, this::mapToDto);
    }

    private HistoryResDTO mapToDto(History history) {
        var dto = new HistoryResDTO(history.getId(), history.getTransaction().getCode(),
                history.getTransactionStatus().getCode(), history.getCreatedAt());
        return dto;
    }
}
