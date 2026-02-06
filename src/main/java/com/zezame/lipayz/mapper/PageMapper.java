package com.zezame.lipayz.mapper;

import com.zezame.lipayz.dto.pagination.PageMeta;
import com.zezame.lipayz.dto.pagination.PageRes;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class PageMapper {
    public <T, R> PageRes<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        List<R> data = new ArrayList<>();
        for (T item : page.getContent()) {
            data.add(mapper.apply(item));
        }

        PageMeta meta = new PageMeta(
                (page.getNumber() + 1),
                page.getSize(),
                page.getTotalElements()
        );

        return new PageRes<>(data, meta);
    }
}
