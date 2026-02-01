package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.mapper.HistoryMapper;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.History;
import com.zezame.lipayz.repo.HistoryRepo;
import com.zezame.lipayz.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HistoryServiceImpl implements HistoryService {
    private final HistoryRepo historyRepo;
    private final HistoryMapper historyMapper;
    private final PageMapper pageMapper;

    @Override
    public PageRes<HistoryResDTO> getHistories(Pageable pageable) {
        Page<History> histories = historyRepo.findAll(pageable);
        return pageMapper.toPageResponse(histories, historyMapper::mapToDto);
    }
}
