package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;

public interface HistoryService {
    PageRes<HistoryResDTO> getHistories(Integer page, Integer size);
}
