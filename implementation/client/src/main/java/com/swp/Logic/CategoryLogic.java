package com.swp.Logic;

import java.util.List;

import com.swp.DataModel.Category;
import com.swp.Persistence.CategoryRepository;

public class CategoryLogic
{
    CategoryRepository categoryRepository;

    public List<Category> getCategories() { /* TODO */ return null; }

    public void createCategory(String name, List<Category> parents, List<Category> children) { /*TODO*/ }
    public void editCategory(Category category, String name, List<Category> parents, List<Category> children) { /*TODO*/ }
    public void deleteCategory(Category category) { /*TODO*/ }
    public void getAllInfosFor(Category category) { /*TODO*/ }
    public List<Category> getParentCategories(Category category) { return null; /*TODO*/ }
    public List<Category> getChildrenCategories(Category category) { return null; /*TODO*/ }
}