package com.zezame.lipayz.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageMeta {
    private int page;
    private int size;
    private long totalElements;
}
