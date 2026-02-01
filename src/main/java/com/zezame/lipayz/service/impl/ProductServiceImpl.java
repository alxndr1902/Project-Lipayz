package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.product.CreateProductReqDTO;
import com.zezame.lipayz.dto.product.ProductResDTO;
import com.zezame.lipayz.dto.product.UpdateProductReqDTO;
import com.zezame.lipayz.exceptiohandler.exception.DuplicateException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.mapper.ProductMapper;
import com.zezame.lipayz.model.Product;
import com.zezame.lipayz.repo.ProductRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl extends BaseService implements ProductService {
    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResDTO> getProducts() {
        List<Product> products = productRepo.findAll();
        List<ProductResDTO> DTOs = new ArrayList<>();
        for (Product product : products) {
            DTOs.add(productMapper.mapToDto(product));
        }
        return DTOs;
    }

    @Override
    public ProductResDTO getProductById(String id) {
        var product = findProductById(id);
        return productMapper.mapToDto(product);
    }

    @Override
    public CreateResDTO createProduct(CreateProductReqDTO request) {
        if (productRepo.existsByCode(request.getCode())) {
            throw new DuplicateException("Code Is Not Available");
        }

        var product = new Product();
        product.setCode(request.getCode());
        product.setName(request.getName());
        var savedProduct = productRepo.save(prepareCreate(product));
        return new CreateResDTO(savedProduct.getId(), Message.CREATED.getDescription());
    }

    @Override
    public UpdateResDTO updateProduct(String id, UpdateProductReqDTO request) {
        var product = findProductById(id);

        if (!product.getVersion().equals(request.getVersion())) {
            throw new OptimisticLockException("Error Updating Data, Please Refresh The Page");
        }

        if (!request.getCode().equals(product.getCode())) {
            if (productRepo.existsByCode(request.getCode())) {
                throw new DuplicateException("Code Is Not Available");
            }
        }
        product.setCode(request.getCode());
        product.setName(request.getName());
        var updatedProduct = productRepo.saveAndFlush(prepareUpdate(product));
        return new UpdateResDTO(updatedProduct.getVersion(), Message.UPDATED.getDescription());
    }

    private Product findProductById(String id) {
        var productId = parseUUID(id);
        return productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Is Not Found"));
    }
}
