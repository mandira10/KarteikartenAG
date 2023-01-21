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
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.Extras.DeckList;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.DeckList.DeckListCallback;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.GUI.PageManager.PAGES;

import java.util.List;

public class DeckOverviewPage extends Page
{
    private RenderGUI pCanvas;
    private DeckList pDeckList;

    public DeckOverviewPage()
    {
        super("Decks");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckoverviewpage.xml"));

        pCanvas = findChildByID("canvas");
        pDeckList = new DeckList(new ivec2(0, 0), new ivec2(100, 100), new DeckListCallback() {
            @Override public void run(StudySystem deck)
            {
                ViewSingleDeckPage page = (ViewSingleDeckPage)PageManager.getPage(PAGES.DECK_SINGLEVIEW);
                page.setDeck(deck);
                PageManager.viewPage(PAGES.DECK_SINGLEVIEW);
            }
        });
        pDeckList.setSizeInPercent(true, true);
        pCanvas.addGUI(pDeckList);
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button newButton = (Button)optionsMenu.findChildByID("adddeckbutton");
        newButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)
            {
                PageManager.viewPage(PAGES.DECK_CREATE);
            }
        });

        Searchbar searchbar = new Searchbar(new ivec2(20, 100), new ivec2(40, 30), "Search Deck", new String[] {
            "By Content",
        }, new SearchbarCallback() {
            @Override public void run(String query, int option) 
            {
                if(query.equals(""))
                    loadDecks();
                else
                loadDecks(query, option);
            }
        });
        searchbar.setPositionInPercent(false, true);
        searchbar.setSizeInPercent(true, false);
        searchbar.setOrigin(new ivec2(0, 50));
        addGUI(searchbar);

        this.setSizeInPercent(true, true);
        reposition();
    }
    
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

        switch(option)
        {
            /*By Content*/  case 0: StudySystemController.getInstance().getStudySystemBySearchTerms(searchterm, commoncallback);  break;
        }  
    }

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
