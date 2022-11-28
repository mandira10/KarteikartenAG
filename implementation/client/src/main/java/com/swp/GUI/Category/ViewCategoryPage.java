package com.swp.GUI.Category;

import com.swp.Controller.Controller;
import com.swp.DataModel.Category;

public class ViewCategoryPage {
    Controller controller;

    public void displayInformationToCard(){
        Category category = null;
        controller.getAllInfosFor(category);
    }

    public void showParentCategories()
    {
        Category category = null;
        controller.getParentCategories(category);
    }

    public void showChildrenCategories()
    {
        Category category  = null;
        controller.getChildrenCategories(category);
    }
}
