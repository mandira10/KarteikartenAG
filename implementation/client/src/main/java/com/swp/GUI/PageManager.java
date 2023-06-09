package com.swp.GUI;

import com.gumse.gui.Locale;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.swp.GUI.Cards.*;
import com.swp.GUI.Cards.EditCardPages.EditImageDescriptionCardAnswersPage;
import com.swp.GUI.Category.*;
import com.swp.GUI.Decks.*;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.References.EditReferencesPage;
import com.swp.GUI.Settings.SettingsPage;

import java.util.HashMap;
import java.util.Map;

/**
 * Verwaltet alle verfügbaren Seiten und sorgt dafür,
 * dass zu jeder Zeit nur eine Seite angezeigt werden kann
 * 
 * @author Tom Beuke
 */
public class PageManager
{
    /**
     * Die verfügbaren Seiten
     */
    public enum PAGES
    {
        /** LoginPage */                           LOGIN,
        /** CardOverviewPage */                    CARD_OVERVIEW,
        /** ViewSingleCardPage */                  CARD_SINGLEVIEW,
        /** CreateCardPage */                      CARD_CREATE,
        /** EditCardPage */                        CARD_EDIT,
        /** CardExportPage */                      CARD_EXPORT,
        /** EditImageDescriptionCardAnswersPage */ CARD_IMAGE_ANSWERS,
        /** CardSelectPage */                      CARD_SELECT,
        /** EditReferencesPage */                  REFERENCES_EDIT_PAGE,
        /** CategoryOverviewPage */                CATEGORY_OVERVIEW,
        /** ViewSingleCategoryPage */              CATEGORY_SINGLEVIEW,
        /** EditCategoryPage */                    CATEGORY_EDIT,
        /** CategorySelectPage */                  CATEGORY_SELECTION,
        /** ViewCategoryTreePage */                CATEGORY_TREE,
        /** DeckOverviewPage */                    DECK_OVERVIEW,
        /** ViewSingleDeckPage */                  DECK_SINGLEVIEW,
        /** EditDeckPage */                        DECK_EDIT,
        /** TestDeckPage */                        DECK_TEST,
        /** TestDeckFinishPage */                  DECK_TEST_FINAL,
        /** DeckSelectPage */                      DECK_SELECTION,
        /** CreateDeckPage */                      DECK_CREATE,
        /** SettingsPage */                        SETTINGS,
    }

    private static Map<PAGES, Page> mPages = null;
    private static Page pActivePage, pLastPage;
    private static RenderGUI pPageCanvas;
    private static Runnable pCallback, pLockCallback;
    private static boolean bIsLocked;

    private PageManager() {}

    /**
     * Initialisiert den PageManager und erstellt alle verfügbaren Seiten
     * @param parent Das GUI element zu welchem der PageManager hinzugefügt werden soll
     */
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
        pPageCanvas.hideChildren(true);

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
        addPage(PAGES.CARD_SELECT,         new CardSelectPage());
        addPage(PAGES.REFERENCES_EDIT_PAGE,new EditReferencesPage());
        addPage(PAGES.CATEGORY_OVERVIEW,   new CategoryOverviewPage());
        addPage(PAGES.CATEGORY_SINGLEVIEW, new ViewSingleCategoryPage());
        addPage(PAGES.CATEGORY_EDIT,       new EditCategoryPage());
        addPage(PAGES.CATEGORY_SELECTION,  new CategorySelectPage());
        addPage(PAGES.CATEGORY_TREE,       new ViewCategoryTreePage());
        addPage(PAGES.DECK_OVERVIEW,       new DeckOverviewPage());
        addPage(PAGES.DECK_SINGLEVIEW,     new ViewSingleDeckPage());
        addPage(PAGES.DECK_EDIT,           new EditDeckPage());
        addPage(PAGES.DECK_TEST,           new TestDeckPage());
        addPage(PAGES.DECK_TEST_FINAL,     new TestDeckFinishPage());
        addPage(PAGES.DECK_SELECTION,      new DeckSelectPage());
        addPage(PAGES.DECK_CREATE,         new CreateDeckPage());
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

    /**
     * Zeigt eine gewünschte Seite an
     *
     * @param name Die anzuzeigende Seite
     * @return Gibt die angezeigte Seite wieder
     */
    public static Page viewPage(PAGES name)
    {
        if(!mPages.containsKey(name))
            return null;

        if(bIsLocked)
        {
            ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("pagelock"), new ConfirmationCallback() {
                @Override public void onCancel() {}
                @Override public void onConfirm() {
                    unlockPage(false);
                    pLastPage = pActivePage;
                    pActivePage = mPages.get(name);
                    if(pCallback != null)
                        pCallback.run();
                }
            });

            return mPages.get(name);
        }
        else
        {
    
            if(pActivePage != mPages.get(name))
            {
                pLastPage = pActivePage;
                pActivePage = mPages.get(name);
                if(pCallback != null)
                    pCallback.run();
            }
            return pActivePage;
        }
    }

    /**
     * Zeigt die letzte Seite an
     */
    public static void viewLastPage()
    {
        pActivePage = pLastPage;
    }

    /**
     * Gibt eine angegebene Seite wieder
     * @param name Die wiederzugebene Seite
     * @return Die Seite
     */
    public static Page getPage(PAGES name)
    {
        if(!mPages.containsKey(name))
            return null;

        return mPages.get(name);
    }

    public static void setCallback(Runnable callback)
    {
        pCallback = callback;
    }

    /**
     * Sperrt die aktuelle Seite
     * 
     * @param callback Die callbackfunktion welche bei einem unlock aufgerufen wird
     */
    public static void lockPage(Runnable callback)
    {
        bIsLocked = true;
        pLockCallback = callback;
    }

    /**
     * Entsperrt die aktuelle Seite
     * 
     * @param skipcallback Ob das Callback übersprungen werden soll
     */
    public static void unlockPage(boolean skipcallback)
    {
        bIsLocked = false;
        if(pLockCallback != null && !skipcallback)
            pLockCallback.run();
    }

    /**
     * Rendert die aktuelle Seite
     */
    public static void render()
    {
        pActivePage.render();
    }

    /**
     * Aktualisiert die aktuelle Seite
     */
    public static void update()
    {
        pActivePage.update();
    }
}