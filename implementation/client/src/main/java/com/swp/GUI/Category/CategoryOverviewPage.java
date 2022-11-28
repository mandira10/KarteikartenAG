package com.swp.GUI.Category;

import com.swp.Controller.Controller;

public class CategoryOverviewPage {
    Controller controller;

    public void showCategories()
    {
        controller.getCategories();
    }

    public void showParentCategories()
    {
        String category  ="";
        controller.getParentCategories(category);
    }

    public void showChildrenCategories()
    {
        String category  ="";
        controller.getChildrenCategories(category);
    }

    public void getCountOfCardToCategory()
    {
        String category  ="";
        controller.getCountOfCardsFor(category);
    }
}
