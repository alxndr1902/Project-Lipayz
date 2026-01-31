package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.mapper.HistoryMapper;
import com.zezame.lipayz.model.History;
import com.zezame.lipayz.repo.HistoryRepo;
import com.zezame.lipayz.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HistoryServiceImpl implements HistoryService {
    private final HistoryRepo historyRepo;
    private final HistoryMapper historyMapper;

    @Override
    public List<HistoryResDTO> getHistories() {
        List<History> histories = historyRepo.findAll();
        List<HistoryResDTO> DTOs = new ArrayList<>();
        for (var history : histories) {
            DTOs.add(historyMapper.mapToDto(history));
        }
        return DTOs;
    }
}
