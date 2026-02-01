package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.history.HistoryResDTO;
import com.zezame.lipayz.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("histories")
@PreAuthorize("hasAnyRole('SA', 'PGA')")
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<List<HistoryResDTO>> getHistories() {
        var response = historyService.getHistories();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
