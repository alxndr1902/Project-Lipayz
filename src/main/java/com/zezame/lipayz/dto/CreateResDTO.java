package com.zezame.lipayz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateResDTO {
    private final UUID id;
    private final String message;
}
