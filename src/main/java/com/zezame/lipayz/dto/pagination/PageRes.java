package com.zezame.lipayz.dto.pagination;

import lombok.Getter;

import java.util.List;

@Getter
public class PageRes<T> {
    private final List<T> data;
    private final PageMeta meta;

    public PageRes(List<T> data, PageMeta meta) {
        this.data = data;
        this.meta = meta;
    }
}
