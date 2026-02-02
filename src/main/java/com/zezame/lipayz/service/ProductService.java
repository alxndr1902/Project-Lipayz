package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.product.CreateProductReqDTO;
import com.zezame.lipayz.dto.product.ProductResDTO;
import com.zezame.lipayz.dto.product.UpdateProductReqDTO;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    PageRes<ProductResDTO> getProducts(Pageable pageable);

    ProductResDTO getProductById(String id);

    CreateResDTO createProduct(CreateProductReqDTO request);

    UpdateResDTO updateProduct(String id, UpdateProductReqDTO request);

    CommonResDTO deleteProduct(String id);
}
