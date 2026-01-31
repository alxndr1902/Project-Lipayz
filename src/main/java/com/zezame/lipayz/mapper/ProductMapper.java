package com.zezame.lipayz.mapper;

import com.zezame.lipayz.dto.product.ProductResDTO;
import com.zezame.lipayz.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResDTO mapToDto(Product product);
}
