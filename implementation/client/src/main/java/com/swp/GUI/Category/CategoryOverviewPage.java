package com.swp.GUI.Category;

import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.PageManager.PAGES;

public class CategoryOverviewPage extends Page
{
    public CategoryOverviewPage()
    {
        super("Categories");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryoverviewpage.xml"));


        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
