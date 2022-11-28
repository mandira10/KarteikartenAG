package com.swp.GUI.Category;

import com.swp.Controller.Controller;

public class ViewCategoryPage {
    Controller controller;

    public void displayInformationToCard(){
        String category="";
        controller.getAllInfosFor(category);
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
}
