package com.zezame.lipayz.mapper;

import com.zezame.lipayz.dto.product.ProductResDTO;
import com.zezame.lipayz.model.Product;

public interface ProductMapper {
    ProductResDTO mapToDto(Product product);
}
