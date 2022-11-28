package com.swp.GUI.Category;

import com.swp.Controller.Controller;
import com.swp.DataModel.Category;

public class CategoryOverviewPage {
    Controller controller;

    public void showCategories()
    {
        controller.getCategories();
    }

    public void showParentCategories()
    {
        Category category = null;
        controller.getParentCategories(category);
    }

    public void showChildrenCategories()
    {
        Category category = null;
        controller.getChildrenCategories(category);
    }

    public void getCountOfCardToCategory()
    {
        Category category = null;
        controller.getCountOfCardsFor(category);
    }
}
