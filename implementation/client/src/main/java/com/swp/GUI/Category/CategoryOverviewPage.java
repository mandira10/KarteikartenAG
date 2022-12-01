package com.swp.GUI.Category;

import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.PageManager;
import com.swp.GUI.Settings.ExportSettingsPage;

public class CategoryOverviewPage 
{
    public void showCategories()
    {
        CategoryController.getCategories();
    }

    public void getCountOfCardToCategory()
    {
        Category category = null;
        CategoryController.getCountOfCardsFor(category);
    }

    private void exportCards()
    {
        ExportSettingsPage.setToExport(null);
        PageManager.viewPage("ExportSettingsPage");
    }
}
