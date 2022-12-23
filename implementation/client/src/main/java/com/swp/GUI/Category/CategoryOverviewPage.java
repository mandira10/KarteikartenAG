package com.swp.GUI.Category;

import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.PageManager.PAGES;

public class CategoryOverviewPage
{
    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
