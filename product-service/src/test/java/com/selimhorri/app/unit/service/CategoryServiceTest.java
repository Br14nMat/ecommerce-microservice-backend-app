package com.selimhorri.app.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.service.impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private AutoCloseable closeable;

    private Category sampleCategory;
    private CategoryDto sampleDto;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        sampleCategory = Category.builder()
                .categoryId(1)
                .categoryTitle("Technology")
                .imageUrl("http://example.com/image.png")
                .parentCategory(null)
                .subCategories(Set.of())
                .products(Set.of())
                .build();

        sampleDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Technology")
                .imageUrl("http://example.com/image.png")
                .build();
    }

    @Test
    void testFindAll_ReturnsListOfCategoryDtos() {
        when(categoryRepository.findAll()).thenReturn(List.of(sampleCategory));

        List<CategoryDto> result = categoryService.findAll();

        assertEquals(1, result.size());
        assertEquals("Technology", result.get(0).getCategoryTitle());
        verify(categoryRepository).findAll();
    }

    @Test
    void testFindById_ValidId_ReturnsCategoryDto() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(sampleCategory));

        CategoryDto result = categoryService.findById(1);

        assertEquals("Technology", result.getCategoryTitle());
        assertEquals("http://example.com/image.png", result.getImageUrl());
        verify(categoryRepository).findById(1);
    }

    @Test
    void testFindById_InvalidId_ThrowsException() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(99));
        verify(categoryRepository).findById(99);
    }

    @Test
    void testSave_ReturnsSavedCategoryDto() {
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        CategoryDto result = categoryService.save(sampleDto);

        assertEquals("Technology", result.getCategoryTitle());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testUpdate_ReturnsUpdatedCategoryDto() {
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        CategoryDto result = categoryService.update(sampleDto);

        assertEquals("Technology", result.getCategoryTitle());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testUpdateWithId_ReturnsUpdatedCategoryDto() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        CategoryDto result = categoryService.update(1, sampleDto);

        assertEquals("Technology", result.getCategoryTitle());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testDeleteById_CallsRepositoryDeleteById() {
        categoryService.deleteById(1);

        verify(categoryRepository).deleteById(1);
    }
}
