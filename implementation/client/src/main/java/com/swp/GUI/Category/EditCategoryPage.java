package com.swp.GUI.Category;

import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;

import java.util.List;

public class EditCategoryPage 
{

    public void createCategory()
    {
        String name = "";
        List<Category> parents = null;
        List<Category> children = null;
        CategoryController.createCategory(name, parents, children);
    }

    public void editCategory()
    {
        Category category = null;
        String name = "";
        List<Category> parents = null;
        List<Category> children = null;
        CategoryController.updateCategoryData(category, category); //TODO
    }

    public void deleteCategory()
    {
        Category category = null;
        CategoryController.deleteCategory(category);
    }

    public void addCardToCategory()
    {
        Card card = null;
        Category category = null;
        CategoryController.createCardToCategory(card,category);
    }
}
