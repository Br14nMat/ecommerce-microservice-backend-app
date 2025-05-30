package com.selimhorri.app.unit.resource;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.resource.ProductResource;
import com.selimhorri.app.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductResource productResource;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productResource).build();
        objectMapper = new ObjectMapper();
    }

    private ProductDto createSampleProductDto(Integer id) {
        ProductDto dto = new ProductDto();
        dto.setProductId(id);
        dto.setProductTitle("Product " + id);
        dto.setImageUrl("http://image.url/" + id);
        dto.setSku("SKU" + id);
        dto.setPriceUnit(100.0 + id);
        dto.setQuantity(10 + id);
        return dto;
    }

    @Test
    public void testFindAll() throws Exception {
        ProductDto p = createSampleProductDto(1);

        when(productService.findAll()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection").isArray())
                .andExpect(jsonPath("$.collection[0].productId").value(1))
                .andExpect(jsonPath("$.collection[0].productTitle").value("Product 1"))
                .andExpect(jsonPath("$.collection[0].imageUrl").value("http://image.url/1"))
                .andExpect(jsonPath("$.collection[0].sku").value("SKU1"))
                .andExpect(jsonPath("$.collection[0].priceUnit").value(101.0))
                .andExpect(jsonPath("$.collection[0].quantity").value(11));
    }

    @Test
    public void testFindById() throws Exception {
        ProductDto p = createSampleProductDto(2);

        when(productService.findById(2)).thenReturn(p);

        mockMvc.perform(get("/api/products/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(2))
                .andExpect(jsonPath("$.productTitle").value("Product 2"))
                .andExpect(jsonPath("$.imageUrl").value("http://image.url/2"))
                .andExpect(jsonPath("$.sku").value("SKU2"))
                .andExpect(jsonPath("$.priceUnit").value(102.0))
                .andExpect(jsonPath("$.quantity").value(12));
    }

    @Test
    public void testSave() throws Exception {
        ProductDto p = createSampleProductDto(0);
        p.setProductId(null);

        ProductDto saved = createSampleProductDto(3);

        when(productService.save(any(ProductDto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(3))
                .andExpect(jsonPath("$.productTitle").value("Product 3"))
                .andExpect(jsonPath("$.imageUrl").value("http://image.url/3"))
                .andExpect(jsonPath("$.sku").value("SKU3"))
                .andExpect(jsonPath("$.priceUnit").value(103.0))
                .andExpect(jsonPath("$.quantity").value(13));
    }

    @Test
    public void testUpdateWithoutIdInPath() throws Exception {
        ProductDto p = createSampleProductDto(4);

        when(productService.update(any(ProductDto.class))).thenReturn(p);

        mockMvc.perform(put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(4))
                .andExpect(jsonPath("$.productTitle").value("Product 4"))
                .andExpect(jsonPath("$.imageUrl").value("http://image.url/4"))
                .andExpect(jsonPath("$.sku").value("SKU4"))
                .andExpect(jsonPath("$.priceUnit").value(104.0))
                .andExpect(jsonPath("$.quantity").value(14));
    }

    @Test
    public void testUpdateWithIdInPath() throws Exception {
        ProductDto p = createSampleProductDto(0);
        p.setProductId(null);

        ProductDto updated = createSampleProductDto(5);

        when(productService.update(eq(5), any(ProductDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(5))
                .andExpect(jsonPath("$.productTitle").value("Product 5"))
                .andExpect(jsonPath("$.imageUrl").value("http://image.url/5"))
                .andExpect(jsonPath("$.sku").value("SKU5"))
                .andExpect(jsonPath("$.priceUnit").value(105.0))
                .andExpect(jsonPath("$.quantity").value(15));
    }

    @Test
    public void testDeleteById() throws Exception {
        mockMvc.perform(delete("/api/products/6"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
