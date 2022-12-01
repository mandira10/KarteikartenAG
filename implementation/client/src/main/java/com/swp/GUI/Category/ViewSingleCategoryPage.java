package com.swp.GUI.Category;

import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;

public class ViewSingleCategoryPage 
{
    public void displayInformationToCard(){
        Category category = null;
        CategoryController.getAllInfosFor(category);
    }
}
