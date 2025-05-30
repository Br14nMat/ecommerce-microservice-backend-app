package com.selimhorri.app.unit.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.resource.CategoryResource;
import com.selimhorri.app.service.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryResource.class)
class CategoryResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Tech")
                .imageUrl("img.png")
                .build();
    }

    @Test
    void testFindById() throws Exception {
        when(categoryService.findById(1)).thenReturn(categoryDto);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Tech"));
    }

    @Test
    void testSave() throws Exception {
        when(categoryService.save(any(CategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Tech"));
    }

    @Test
    void testUpdateWithoutId() throws Exception {
        when(categoryService.update(any(CategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(put("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Tech"));
    }

    @Test
    void testUpdateWithId() throws Exception {
        when(categoryService.update(eq(1), any(CategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Tech"));
    }

    @Test
    void testDeleteById() throws Exception {
        doNothing().when(categoryService).deleteById(1);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
