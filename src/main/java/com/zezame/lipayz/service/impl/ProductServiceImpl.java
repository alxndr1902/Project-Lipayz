package com.zezame.lipayz.service.impl;

import com.zezame.lipayz.constant.Message;
import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.product.CreateProductReqDTO;
import com.zezame.lipayz.dto.product.ProductResDTO;
import com.zezame.lipayz.dto.product.UpdateProductReqDTO;
import com.zezame.lipayz.exceptiohandler.exception.ConflictException;
import com.zezame.lipayz.exceptiohandler.exception.DuplicateException;
import com.zezame.lipayz.exceptiohandler.exception.NotFoundException;
import com.zezame.lipayz.exceptiohandler.exception.OptimisticLockException;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.Product;
import com.zezame.lipayz.repo.ProductRepo;
import com.zezame.lipayz.repo.TransactionRepo;
import com.zezame.lipayz.service.BaseService;
import com.zezame.lipayz.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl extends BaseService implements ProductService {
    private final ProductRepo productRepo;
    private final TransactionRepo transactionRepo;
    private final PageMapper pageMapper;

    @Override
    public PageRes<ProductResDTO> getProducts(Pageable pageable) {
        Page<Product> products = productRepo.findAll(pageable);
        return pageMapper.toPageResponse(products, this::mapToDto);
    }

    @Override
    public ProductResDTO getProductById(String id) {
        var product = findProductById(id);
        var dto = mapToDto(product);
        return dto;
    }

    private ProductResDTO mapToDto(Product product) {
        var dto = new ProductResDTO(product.getId(), product.getName(), product.getVersion());
        return dto;
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
        var product = validateAndGetProductForUpdate(id, request);

        product.setCode(request.getCode());
        product.setName(request.getName());
        var updatedProduct = productRepo.saveAndFlush(prepareUpdate(product));
        return new UpdateResDTO(updatedProduct.getVersion(), Message.UPDATED.getDescription());
    }

    private Product validateAndGetProductForUpdate(String id, UpdateProductReqDTO request) {
        var product = findProductById(id);

        if (!product.getVersion().equals(request.getVersion())) {
            throw new OptimisticLockException("Error Updating Data, Please Refresh The Page");
        }

        if (!request.getCode().equals(product.getCode())) {
            if (productRepo.existsByCode(request.getCode())) {
                throw new DuplicateException("Code Is Not Available");
            }
        }

        return product;
    }

    @Override
    public CommonResDTO deleteProduct(String id) {
        var product = findProductById(id);

        if (transactionRepo.existsByProduct(product)) {
            throw new ConflictException("This Product Cannot Be Deleted, Because It Has Transaction History");
        }

        productRepo.delete(product);
        return new CommonResDTO(Message.DELETED.getDescription());
    }

    private Product findProductById(String id) {
        var productId = parseUUID(id);
        return productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Is Not Found"));
    }
}
