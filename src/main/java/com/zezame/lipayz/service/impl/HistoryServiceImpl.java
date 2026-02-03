package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
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
        var user = getLoginUser(userRepo);

        Page<History> histories = null;

        switch (user.getRole().getCode()) {
            case "CUST" -> histories = historyRepo.findByCustomer(user, pageable);
            case "PGA" -> {
                var pga = paymentGatewayAdminRepo.findByUser(user);
                histories = historyRepo.findByPaymentGateway(pga.getPaymentGateway(), pageable);
            }
            case "SA" -> historyRepo.findAll(pageable);
        }

        return pageMapper.toPageResponse(histories, this::mapToDto);
    }

    private HistoryResDTO mapToDto(History history) {
        var dto = new HistoryResDTO(history.getId(), history.getTransaction().getCode(),
                history.getTransactionStatus().getCode(), history.getCreatedAt());
        return dto;
    }
}
