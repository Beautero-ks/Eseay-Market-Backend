package com.ninehub.dreamshops.service.category;

import java.util.List;

import com.ninehub.dreamshops.model.Category;

public interface ICategoryService {
    Category findCategoryById(Long Id);
    Category findCategoryByName(String name);
    Category addCategory(Category category);
    void deleteCategoryById(Long id);
    Category updateCategory(Category category, Long id);
    
    List<Category> getAllCategories();

    // Long countCategory();
}
