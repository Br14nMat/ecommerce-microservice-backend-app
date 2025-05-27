package com.selimhorri.app.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;

import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.CategoryDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProductResourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                super.handleError(response);
            }
        });
    }

    @Test
    void testSave() {
        String url = "http://localhost:" + port + "/product-service/api/products";
        ProductDto productDto = new ProductDto();
        productDto.setProductTitle("New Product");
        productDto.setSku("NEW-SKU");
        productDto.setPriceUnit(150000.0);
        productDto.setQuantity(777);
        productDto.setImageUrl("image.jpg");
        productDto.setCategoryDto(CategoryDto.builder().categoryId(1).build());

        ProductDto response = restTemplate.postForObject(url, productDto, ProductDto.class);

        assertNotNull(response);
        assertNotNull(response.getProductId());
        assertEquals(productDto.getProductTitle(), response.getProductTitle());
        assertEquals(productDto.getSku(), response.getSku());

    }

    @Test
    void testFindAll() {
        String url = "http://localhost:" + port + "/product-service/api/products";
        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject(url, DtoCollectionResponse.class);

        assertNotNull(response);
        assertFalse(response.getCollection().isEmpty());
        assertEquals(3, response.getCollection().size());
    }

    @Test
    void testFindById() {
        String url = "http://localhost:" + port + "/product-service/api/products/1";
        ProductDto response = restTemplate.getForObject(url, ProductDto.class);

        assertEquals("Balon de futbol", response.getProductTitle());
        assertEquals("ball_football.jpg", response.getImageUrl());
        assertEquals("SKU-DEP-BF1", response.getSku());
        assertEquals(29.99, response.getPriceUnit());
        assertEquals(75, response.getQuantity());
        assertEquals(1, response.getProductId());
    }

    @Test
    void testUpdate() {
        String url = "http://localhost:" + port + "/product-service/api/products";

        ProductDto productDto = new ProductDto();
        productDto.setProductTitle("update");
        productDto.setSku("update");
        productDto.setPriceUnit(777.777);
        productDto.setQuantity(777);
        productDto.setImageUrl("image.jpg");
        productDto.setCategoryDto(CategoryDto.builder().categoryId(2).build());

        ProductDto response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(productDto), ProductDto.class).getBody();

        assertNotNull(response);
        assertEquals(productDto.getProductTitle(), response.getProductTitle());
        assertEquals(productDto.getSku(), response.getSku());
        assertEquals(productDto.getPriceUnit(), response.getPriceUnit());
        assertEquals(productDto.getQuantity(), response.getQuantity());
        assertEquals(productDto.getImageUrl(), response.getImageUrl());
    }

}