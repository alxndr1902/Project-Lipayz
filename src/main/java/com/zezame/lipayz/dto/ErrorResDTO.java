package com.zezame.lipayz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResDTO<T> {
    private final T message;
}
