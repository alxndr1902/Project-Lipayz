package com.zezame.lipayz.service;

import com.zezame.lipayz.dto.pagination.PageMeta;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.product.CreateProductReqDTO;
import com.zezame.lipayz.dto.product.ProductResDTO;
import com.zezame.lipayz.dto.product.UpdateProductReqDTO;
import com.zezame.lipayz.mapper.PageMapper;
import com.zezame.lipayz.model.Product;
import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.repo.ProductRepo;
import com.zezame.lipayz.repo.TransactionRepo;
import com.zezame.lipayz.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductTest {
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private TransactionRepo transactionRepo;

    private PageMapper pageMapper = new PageMapper();

    @BeforeEach
    void setup() {
        productService = new ProductServiceImpl(productRepo, transactionRepo, pageMapper);
    }

    @Mock
    private PrincipalService principalService;

    @Test
    public void shouldCreateProduct_WhenDataValid() {
        productService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(UUID.randomUUID().toString());
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        var id = UUID.randomUUID();

        var request = new CreateProductReqDTO();

        var savedProduct = new Product();
        savedProduct.setId(id);

        Mockito.when(productRepo.existsByCode(Mockito.any())).thenReturn(false);
        Mockito.when(productRepo.save(Mockito.any())).thenReturn(savedProduct);

        var result = productService.createProduct(request);

        Assertions.assertEquals(id, result.getId());

        Mockito.verify(productRepo, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(productRepo, Mockito.atLeast(1)).existsByCode(Mockito.any());
    }

    @Test
    public void shouldReturnData_WhenIdIsValid() {
        var id = UUID.randomUUID();

        var savedProduct = new Product();
        savedProduct.setId(id);

        Mockito.when(productRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(savedProduct));

        var result = productService.getProductById(id.toString());

        Assertions.assertEquals(id, result.getId());
        Mockito.verify(productRepo, Mockito.atLeast(1)).findById(Mockito.any());
    }

    @Test
    public void shouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();

        var savedProduct = new Product();
        savedProduct.setId(id);

        List<Product> products = List.of(savedProduct);

        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        Mockito.when(productRepo.findAll(pageable))
                .thenReturn(page);

        Mockito.when(pageMapper.toPageResponse(Mockito.any(), Mockito.any()))
                .thenReturn(new PageRes<>(
                        List.of(new ProductResDTO(id, null, null)),
                        new PageMeta(0, 10, products.size())
                ));

        var result = productService.getProducts(pageable);

        Assertions.assertEquals(products.size(), result.getData().size());
        Assertions.assertEquals(id,  result.getData().getFirst().getId());

        Mockito.verify(productRepo, Mockito.atLeast(1)).findAll(pageable);
    }

    @Test
    public void shouldUpdateProduct_WhenDataValid() {
        productService.setPrincipal(principalService);
        var auth = new AuthorizationPojo(UUID.randomUUID().toString());
        Mockito.when(principalService.getPrincipal()).thenReturn(auth);

        var productId = UUID.randomUUID();

        var savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setVersion(1);

        var productById = new Product();
        productById.setVersion(0);

        var request = new UpdateProductReqDTO();
        request.setVersion(0);
        request.setCode("Prod1");

        Mockito.when(productRepo.existsByCode(Mockito.any()))
                .thenReturn(false);
        Mockito.when(productRepo.findById(Mockito.any()))
                .thenReturn(Optional.of(productById));
        Mockito.when(productRepo.saveAndFlush(Mockito.any()))
                .thenReturn(savedProduct);

        var result = productService.updateProduct(productId.toString(), request);

        Assertions.assertEquals(1, result.getVersion());

        Mockito.verify(productRepo, Mockito.atLeast(1)).existsByCode(Mockito.any());
    }
}
