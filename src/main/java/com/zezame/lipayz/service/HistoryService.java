package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import org.springframework.data.domain.Pageable;

public interface HistoryService {
    PageRes<HistoryResDTO> getHistories(Pageable pageable);
}
