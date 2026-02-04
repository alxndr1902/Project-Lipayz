package com.zezame.lipayz.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageRes<T> {
    private List<T> data;
    private PageMeta meta;
}
