package com.swp.GUI;

import java.util.HashMap;
import java.util.Map;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.swp.GUI.Cards.*;
import com.swp.GUI.Cards.EditCardPages.EditImageDescriptionCardAnswersPage;
import com.swp.GUI.Category.CategoryOverviewPage;
import com.swp.GUI.Category.CategorySelectPage;
import com.swp.GUI.Category.EditCategoryPage;
import com.swp.GUI.Category.ViewSingleCategoryPage;
import com.swp.GUI.Decks.DeckOverviewPage;
import com.swp.GUI.Decks.EditDeckPage;
import com.swp.GUI.Decks.TestDeckFinishPage;
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
        CARD_CREATE,
        CARD_EDIT,
        CARD_TEST,
        CARD_EXPORT,
        CARD_IMAGE_ANSWERS,
        CATEGORY_OVERVIEW,
        CATEGORY_SINGLEVIEW,
        CATEGORY_EDIT,
        CATEGORY_SELECTION,
        DECK_OVERVIEW,
        DECK_SINGLEVIEW,
        DECK_EDIT,
        DECK_TEST,
        DECK_TEST_FINAL,
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
        addPage(PAGES.CARD_OVERVIEW,       new CardOverviewPage());
        addPage(PAGES.CARD_SINGLEVIEW,     new ViewSingleCardPage());
        addPage(PAGES.CARD_EDIT,           new EditCardPage());
        addPage(PAGES.CARD_CREATE,         new CreateCardPage());
        addPage(PAGES.CARD_EXPORT,         new CardExportPage());
        addPage(PAGES.CATEGORY_OVERVIEW,   new CategoryOverviewPage());
        addPage(PAGES.CATEGORY_SINGLEVIEW, new ViewSingleCategoryPage());
        addPage(PAGES.CATEGORY_EDIT,       new EditCategoryPage());
        addPage(PAGES.CATEGORY_SELECTION,  new CategorySelectPage());
        addPage(PAGES.DECK_OVERVIEW,       new DeckOverviewPage());
        addPage(PAGES.DECK_SINGLEVIEW,     new ViewSingleDeckPage());
        addPage(PAGES.DECK_EDIT,           new EditDeckPage());
        addPage(PAGES.DECK_TEST,           new TestDeckPage());
        addPage(PAGES.DECK_TEST_FINAL,     new TestDeckFinishPage());
        addPage(PAGES.LOGIN,               new LoginPage());
        addPage(PAGES.SETTINGS,            new SettingsPage());
        addPage(PAGES.CARD_IMAGE_ANSWERS,  new EditImageDescriptionCardAnswersPage());
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