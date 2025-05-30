package com.selimhorri.app.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.helper.ProductMappingHelper;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.domain.Category;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .productId(1)
                .productTitle("Laptop")
                .imageUrl("http://img.com/laptop.png")
                .sku("SKU123")
                .priceUnit(1500.0)
                .quantity(10)
                .category(new Category())
                .build();

        productDto = ProductMappingHelper.map(product);
    }

    @Test
    void testFindAll() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDto> result = productService.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getProductId()).isEqualTo(1);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindById_WhenProductExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ProductDto result = productService.findById(1);

        assertThat(result.getProductId()).isEqualTo(1);
        verify(productRepository).findById(1);
    }

    @Test
    void testFindById_WhenProductDoesNotExist() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(999));
        verify(productRepository).findById(999);
    }

    @Test
    void testSave() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertThat(result.getProductTitle()).isEqualTo("Laptop");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdate_ByDto() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.update(productDto);

        assertThat(result.getProductId()).isEqualTo(1);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdate_ByIdAndDto_WhenProductExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.update(1, productDto);

        assertThat(result.getProductId()).isEqualTo(1);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdate_ByIdAndDto_WhenProductDoesNotExist() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.update(999, productDto));
        verify(productRepository).findById(999);
    }


    @Test
    void testDeleteById_WhenProductExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteById(1);

        verify(productRepository).delete(product);
    }

    @Test
    void testDeleteById_WhenProductDoesNotExist() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteById(999));
        verify(productRepository).findById(999);
    }
}
