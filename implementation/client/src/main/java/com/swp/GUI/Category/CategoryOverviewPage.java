package com.swp.GUI.Category;

import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.PageManager;
import com.swp.GUI.Settings.ExportSettingsPage;

public class CategoryOverviewPage
{
    private void exportCards()
    {
        ExportSettingsPage.setToExport(null);
        PageManager.viewPage("ExportSettingsPage");
    }
}
