package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.history.HistoryResDTO;

import java.util.List;

public interface HistoryService {
    List<HistoryResDTO> getHistories();
}
