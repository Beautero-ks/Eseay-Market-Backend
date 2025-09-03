package com.ninehub.dreamshops.service.category;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ninehub.dreamshops.execptions.AlreadyExistsException;
import com.ninehub.dreamshops.execptions.ResourceNotFoundException;
import com.ninehub.dreamshops.model.Category;
import com.ninehub.dreamshops.repositry.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    public Category findCategoryById(Long Id) {
        return categoryRepository.findById(Id)
            .orElseThrow(()-> new ResourceNotFoundException("Category not found!"));
    }

    @Override
    public Category findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category).filter(c-> !categoryRepository.existsByName(c.getName()))
            .map(categoryRepository :: save)
            .orElseThrow(() -> new AlreadyExistsException(category.getName() + " already exists!"));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
            .ifPresentOrElse(categoryRepository :: delete,
            ()->{throw new ResourceNotFoundException("Category not found!");
        });
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(findCategoryById(id)).map(oldCategory -> {
            oldCategory.setName(category.getName());
            return categoryRepository.save(oldCategory);
        }).orElseThrow(()-> new ResourceNotFoundException("Category not found!"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // @Override
    // public Long countCategory() {
    //     return categoryRepository.count();
    // }

}
