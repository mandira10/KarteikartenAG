package com.swp.GUI;

import java.util.HashMap;
import java.util.Map;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.swp.GUI.Cards.*;
import com.swp.GUI.Category.CategoryOverviewPage;
import com.swp.GUI.Decks.DeckOverviewPage;
import com.swp.GUI.Decks.EditDeckPage;
import com.swp.GUI.Decks.TestDeckPage;
import com.swp.GUI.Decks.ViewSingleDeckPage;
import com.swp.GUI.Settings.SettingsPage;

public class PageManager
{
    public static enum PAGES
    {
        LOGIN,
        CARD_OVERVIEW,
        CARD_SINGLEVIEW,
        CARD_EDIT,
        CARD_TEST,
        CARD_EXPORT,
        CATEGORY_OVERVIEW,
        DECK_OVERVIEW,
        DECK_SINGLEVIEW,
        DECK_EDIT,
        DECK_TEST,
        SETTINGS,
    };

    private static Map<PAGES, Page> mPages = null;
    private static Page pActivePage, pLastPage;
    private static RenderGUI pPageCanvas;

    private PageManager() {}

    public static void init(RenderGUI parent)
    {
        pActivePage = null;
        pLastPage = null;

        if(mPages == null)
        {
            mPages = new HashMap<>();
        }
        pPageCanvas = new RenderGUI();
        pPageCanvas.setPosition(new ivec2(100, 0));
        pPageCanvas.setSize(new ivec2(100, 100));
        pPageCanvas.setMargin(new ivec2(-100, 0));
        pPageCanvas.setSizeInPercent(true, true);

        parent.addGUI(pPageCanvas);

        initPages();
    }

    private static void initPages()
    {
        addPage(PAGES.CARD_OVERVIEW,     new CardOverviewPage());
        addPage(PAGES.CARD_SINGLEVIEW,   new ViewSingleCardPage());
        addPage(PAGES.CARD_EDIT,         new EditCardPage());
        addPage(PAGES.CARD_EXPORT,       new CardExportPage());
        addPage(PAGES.CATEGORY_OVERVIEW, new CategoryOverviewPage());
        addPage(PAGES.DECK_OVERVIEW,     new DeckOverviewPage());
        addPage(PAGES.DECK_SINGLEVIEW,   new ViewSingleDeckPage());
        addPage(PAGES.DECK_EDIT,         new EditDeckPage());
        addPage(PAGES.DECK_TEST,         new TestDeckPage());
        addPage(PAGES.LOGIN,             new LoginPage());
        addPage(PAGES.SETTINGS,          new SettingsPage());
    }


    private static void addPage(PAGES type, Page page)
    {
        mPages.put(type, page);
        pPageCanvas.addGUI(page);
        if(pActivePage == null)
            pActivePage = page;
    }

    public static Page viewPage(PAGES name)
    {
        if(!mPages.containsKey(name))
            return null;

        pLastPage = pActivePage;
        pActivePage = mPages.get(name);
        return pActivePage;
    }

    public static void viewLastPage()
    {
        pActivePage = pLastPage;
    }

    public static Page getPage(PAGES name)
    {
        if(!mPages.containsKey(name))
            return null;

        return mPages.get(name);
    }

    public static void render()
    {
        pActivePage.render();
    }

    public static void update()
    {
        pActivePage.update();
    }
}