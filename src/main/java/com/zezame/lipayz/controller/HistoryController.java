package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.service.HistoryService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("histories")
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<PageRes<HistoryResDTO>> getHistories(@RequestParam(defaultValue = "1") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer size) {
        var response = historyService.getHistories(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
