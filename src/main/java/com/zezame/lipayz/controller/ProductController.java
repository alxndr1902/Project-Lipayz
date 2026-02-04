package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CommonResDTO;
import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
import com.zezame.lipayz.dto.pagination.PageRes;
import com.zezame.lipayz.dto.product.CreateProductReqDTO;
import com.zezame.lipayz.dto.product.ProductResDTO;
import com.zezame.lipayz.dto.product.UpdateProductReqDTO;
import com.zezame.lipayz.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageRes<ProductResDTO>> getProducts(@RequestParam(defaultValue = "1") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        var response = productService.getProducts(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductResDTO> getProductById(@PathVariable String id) {
        var response = productService.getProductById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<CreateResDTO> createProduct(@RequestBody @Valid CreateProductReqDTO request) {
        var response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<UpdateResDTO> updateProduct(@PathVariable String id,
                                                      @RequestBody @Valid UpdateProductReqDTO request) {
        var response = productService.updateProduct(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<CommonResDTO> updateProduct(@PathVariable String id) {
        var response = productService.deleteProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
