package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.exceptiohandler.exception.InvalidNominalException;
import com.zezame.lipayz.exceptiohandler.exception.InvalidParameterException;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.History;
import com.zezame.lipayz.model.Transaction;
import com.zezame.lipayz.repo.HistoryRepo;
import com.zezame.lipayz.repo.PaymentGatewayAdminRepo;
import com.zezame.lipayz.repo.UserRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class HistoryServiceImpl extends BaseService implements HistoryService {
    private final HistoryRepo historyRepo;
    private final PageMapper pageMapper;

    @Cacheable(value = "history", key = "'page:' + #page + 'size:' + #size")
    @Override
    public PageRes<HistoryResDTO> getHistories(Integer page, Integer size) {
        validatePaginationParam(page, size);

        Pageable pageable = PageRequest.of((page - 1), size);
        var role = principalService.getPrincipal().getRoleCode();
        var id = UUID.fromString(principalService.getPrincipal().getId());

        Page<History> histories = resolveByRole(role,
                () -> historyRepo.findAll(pageable),
                () -> historyRepo.findByCustomer(id, pageable),
                () -> historyRepo.findByPaymentGateway(id, pageable));

        return pageMapper.toPageResponse(histories, this::mapToDto);
    }

    private HistoryResDTO mapToDto(History history) {
        return new HistoryResDTO(history.getId(), history.getTransaction().getCode(),
                history.getTransactionStatus().getCode(), history.getCreatedAt().toString());
    }
}
