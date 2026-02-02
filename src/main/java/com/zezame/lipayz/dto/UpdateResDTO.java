package com.zezame.lipayz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateResDTO {
    private final Integer version;
    private final String message;
}
