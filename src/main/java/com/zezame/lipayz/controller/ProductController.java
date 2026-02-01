package com.zezame.lipayz.controller;

import com.zezame.lipayz.dto.CreateResDTO;
import com.zezame.lipayz.dto.UpdateResDTO;
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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResDTO>> getProducts() {
        var response = productService.getProducts();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductResDTO> getProductById(@PathVariable String id) {
        var response = productService.getProductById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<CreateResDTO> createProduct(@RequestBody @Valid CreateProductReqDTO request) {
        var response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<UpdateResDTO> updateProduct(@PathVariable String id,
                                                      @RequestBody @Valid UpdateProductReqDTO request) {
        var response = productService.updateProduct(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
