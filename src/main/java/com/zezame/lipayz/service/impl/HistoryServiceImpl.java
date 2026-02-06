package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.History;
import com.zezame.lipayz.repo.HistoryRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.HistoryService;
import com.zezame.lipayz.specification.HistorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class HistoryServiceImpl extends BaseService implements HistoryService {
    private final HistoryRepo historyRepo;
    private final PageMapper pageMapper;

    @Cacheable(value = "history",
            key = "'page:' + #page + 'size:' + #size + 'role' + #role + 'id' + #id")
    @Override
    public PageRes<HistoryResDTO> getHistories(Integer page, Integer size, String role, UUID id) {
        validatePaginationParam(page, size);

        Pageable pageable = PageRequest.of((page - 1), size);

        Specification<History> spec = HistorySpecification.byRole(role, id);

        Page<History> histories = historyRepo.findAll(spec, pageable);

        log.info("HIT SERVICE getHistories page={}, size={}, role={}, id={}",
                page, size, role, id);


        return pageMapper.toPageResponse(histories, this::mapToDto);
    }

    private HistoryResDTO mapToDto(History history) {
        return new HistoryResDTO(history.getId(), history.getTransaction().getCode(),
                history.getTransactionStatus().getCode(), history.getCreatedAt().toString());
    }
}
