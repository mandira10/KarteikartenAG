package com.swp.GUI.Category;

import com.swp.Controller.Controller;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;

import java.util.List;

public class EditCategoryPage {
    Controller controller;

    public void createCategory()
    {
        String name = "";
        List<Category> parents = null;
        List<Category> children = null;
        controller.createCategory(name, parents, children);
    }

    public void editCategory()
    {
        Category category = null;
        String name = "";
        List<Category> parents = null;
        List<Category> children = null;
        controller.editCategory(category, name, parents, children);
    }

    public void deleteCategory()
    {
        Category category = null;
        controller.deleteCategory(category);
    }

    public void addCardToCategory()
    {
        Card card = null;
        Category category = null;
        controller.createCardToCategory(card,category);
    }
}
