package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.store.CategoryStore;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryStore categoryStore;

    public CategoryService(CategoryStore categoryStore) {
        this.categoryStore = categoryStore;
    }

    public List<Category> getAllCategories() {
        return categoryStore.getAllCategories();
    }
}
