package com.zezame.lipayz.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductResDTO {
    private UUID id;
    private String name;
    private Integer version;
}
