package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.DataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.DeckList;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.PageManager.PAGES;

import java.util.List;

/**
 * Die Seite welche einem alle Decks in einer Liste darstellt
 */
public class DeckOverviewPage extends Page
{
    private final RenderGUI pCanvas;
    private final DeckList pDeckList;

    /**
     * Der Standardkonstruktor der Klasse DeckOverviewPage
     */
    public DeckOverviewPage()
    {
        super("Decks", "deckoverviewpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckoverviewpage.xml"));

        pCanvas = findChildByID("canvas");
        pDeckList = new DeckList(new ivec2(0, 0), new ivec2(100, 100), (StudySystem deck) -> {
            ViewSingleDeckPage page = (ViewSingleDeckPage)PageManager.getPage(PAGES.DECK_SINGLEVIEW);
            page.setDeck(deck);
            PageManager.viewPage(PAGES.DECK_SINGLEVIEW);
        });
        pDeckList.setSizeInPercent(true, true);
        pCanvas.addGUI(pDeckList);
        
        Button newButton = (Button)findChildByID("adddeckbutton");
        newButton.onClick((RenderGUI gui) -> { PageManager.viewPage(PAGES.DECK_CREATE); });

        Searchbar searchbar = (Searchbar)findChildByID("searchbar");
        searchbar.setCallback((String query, int option) -> {
            if(query.equals(""))
                loadDecks();
            else
                loadDecks(query, option);
        });

        this.setSizeInPercent(true, true);
        reposition();
    }

    /**
     * Lädt alle verfügbaren Decks in die Liste
     */
    public void loadDecks()
    {
        pDeckList.reset();
        StudySystemController.getInstance().getStudySystems(new DataCallback<>() {
            @Override
            public void onSuccess(List<StudySystem> data) {
                pDeckList.addDecks(data);
            }
            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
            @Override
            public void onInfo(String msg) {
                NotificationGUI.addNotification(msg,Notification.NotificationType.INFO,5);
            }
        });
    }

    /**
     * Lädt die Kategorien anhand eines Suchbegriffs
     *
     * @param searchterm Der Suchbegriff
     * @param option     Die Suchoptionen
     */
    public void loadDecks(String searchterm, int option)
    {
        pDeckList.reset();

        DataCallback<StudySystem> commoncallback = new DataCallback<StudySystem>() {
            @Override
            public void onSuccess(List<StudySystem> data) {
                pDeckList.addDecks(data);
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

            @Override
            public void onInfo(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.INFO, 5);
            }
        };

        /*By Content*/
        if (option == 0) {
            StudySystemController.getInstance().getStudySystemBySearchTerms(searchterm, commoncallback);
        }
    }
}
