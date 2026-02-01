package com.zezame.lipayz.dto.pagination;

import lombok.Getter;

@Getter
public class PageMeta {
    private final int page;
    private final int size;
    private final long totalElements;

    public PageMeta(
            int page,
            int size,
            long totalElements
    ) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }
}
