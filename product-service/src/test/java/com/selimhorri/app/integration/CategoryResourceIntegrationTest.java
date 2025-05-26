package com.selimhorri.app.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.selimhorri.app.dto.CategoryDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CategoryResourceIntegrationTest {

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
        String url = "http://localhost:" + port + "/product-service/api/categories";
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryTitle("New Category");
        categoryDto.setImageUrl("image.jpg");
        categoryDto.setParentCategoryDto(CategoryDto.builder().categoryId(1).build());

        CategoryDto response = restTemplate.postForObject(url, categoryDto, CategoryDto.class);

        assertNotNull(response);
        assertNotNull(response.getCategoryId());
        assertEquals(categoryDto.getCategoryTitle(), response.getCategoryTitle());
        assertEquals(categoryDto.getImageUrl(), response.getImageUrl());
        assertEquals(categoryDto.getParentCategoryDto(), response.getParentCategoryDto());


    }

    @Test
    void testFindAll() {
        String url = "http://localhost:" + port + "/product-service/api/categories";
        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject(url, DtoCollectionResponse.class);

        assertNotNull(response);
        assertFalse(response.getCollection().isEmpty());
        assertEquals(3, response.getCollection().size());
    }

    @Test
    void testFindById() {
        String url = "http://localhost:" + port + "/product-service/api/categories/1";
        CategoryDto response = restTemplate.getForObject(url, CategoryDto.class);

        assertEquals(1, response.getCategoryId());
        assertEquals("Deportes", response.getCategoryTitle());
        assertEquals("deportes.jpg", response.getImageUrl());

    }



    @Test
    void testUpdate() {
        String url = "http://localhost:" + port + "/product-service/api/categories";

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(1);
        categoryDto.setCategoryTitle("update");
        categoryDto.setImageUrl("image.jpg");
        categoryDto.setParentCategoryDto(CategoryDto.builder().categoryId(1).build());

        CategoryDto response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(categoryDto), CategoryDto.class).getBody();

        assertNotNull(response);
        assertEquals(categoryDto.getCategoryTitle(), response.getCategoryTitle());
        assertEquals(categoryDto.getImageUrl(), response.getImageUrl());
    }

    @Test
    void testDelete() {
        String url = "http://localhost:" + port + "/product-service/api/categories/3";
        restTemplate.delete(url);

        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject("http://localhost:" + port + "/product-service/api/categories", DtoCollectionResponse.class);

        assertNotNull(response);
    }
}