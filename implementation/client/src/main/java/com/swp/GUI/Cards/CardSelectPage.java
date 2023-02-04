package com.swp.GUI.Cards;

import java.util.List;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.CardOverview;
import com.swp.GUI.Extras.ListOrder;
import com.swp.GUI.Page;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.GUI.PageManager.PAGES;
import com.swp.Controller.DataCallback;

/**
 * Die Seite auf welcher man eine Liste an Karten auswählen kann
 */
public class CardSelectPage extends Page
{
    /**
     * Gibt die Liste der ausgewählten Karten wieder
     */
    public interface CardReturnFunc
    {
        /**
         * Wird ausgeführt, wenn auf 'Apply' gedrückt wird
         * @param cards Die ausgewählten karten
         */
        void run(CardOverview cards);
    }


    private Searchbar pSearchbar;
    private CardList pCardList;
    private CardReturnFunc pReturnFunc;
    private int iCurrentLastIndex;

    /**
     * Der Standardkonstruktor der Klasse CardSelectPage
     */
    public CardSelectPage()
    {
        super("Card select", "cardselectpage");
        this.vSize = new ivec2(100,100);
        this.iCurrentLastIndex = 0;

        addGUI(XMLGUI.loadFile("guis/cards/cardselectpage.xml"));

        RenderGUI canvas = findChildByID("canvas");

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), true, new CardListSelectmodeCallback() {
            @Override public void enterSelectmode()
            { 
                if(pReturnFunc != null && pCardList.getSelection().size() > 0)
                    pReturnFunc.run(pCardList.getSelection().get(0)); 
            }
            @Override public void exitSelectmode()  { }
        });
        pCardList.setSizeInPercent(true, true);
        pCardList.setSelectMode(true);
        pCardList.onBottomHit((RenderGUI gui) -> {
            if(iCurrentLastIndex != -1)
                loadCards(iCurrentLastIndex, iCurrentLastIndex + 30);
        });
        canvas.addGUI(pCardList);

        RenderGUI optionsMenu = findChildByID("menu");
        Button doneButton = (Button)optionsMenu.findChildByID("donebutton");
        doneButton.onClick((RenderGUI gui) -> {
            if(pReturnFunc != null && pCardList.getSelection().size() > 0)
                pReturnFunc.run(pCardList.getSelection().get(0));
        });

        pSearchbar = new Searchbar(new ivec2(20, 100), new ivec2(40, 30), "cardsearch", new String[] {
            "bycontentsearch",
            "bytagsearch",
            "bycategorysearch"
        });
        pSearchbar.setCallback(new SearchbarCallback() {
            @Override public void run(String query, int option)
            {
                if(query.equals(""))
                    loadCards(0, 30);
                else
                    loadCards(query, option);
            }
        });
        pSearchbar.setPositionInPercent(false, true);
        pSearchbar.setSizeInPercent(true, false);
        pSearchbar.setOrigin(new ivec2(0, 50));
        addGUI(pSearchbar);

        this.setSizeInPercent(true, true);
        reposition();
    }

    /**
     * Setzt die auswahl zurück und lädt die Karten neu
     *
     * @param returnfunc Die Funktion, für die übergabe der ausgewählten Karten
     */
    public void reset(CardReturnFunc returnfunc)
    {
        this.pReturnFunc = returnfunc;
        loadCards(0, 30);
    }

    /**
     * Lädt die Karten von einem Index bis zu einem anderen
     *
     * @param from Von-Index
     * @param to   Bis-Index
     */
    public void loadCards(int from, int to)
    {
        if(from == 0)
        {
            iCurrentLastIndex = 0;
            pCardList.reset();
        }
        CardController.getInstance().getCardsToShow(from, to, new DataCallback<CardOverview>() {
            @Override public void onSuccess(List<CardOverview> data)
            {
                iCurrentLastIndex += data.size();
                pCardList.addCards(data, PAGES.REFERENCES_EDIT_PAGE);
            }

            @Override public void onFailure(String msg) 
            {
                NotificationGUI.addNotification(msg, NotificationType.ERROR, 5);     
            }
            @Override public void onInfo(String msg) {}
        });
        reposition();
        resize();
    }

    /**
     * Lädt Karten anhand eines Suchbegriffs
     *
     * @param str    Suchbegriff
     * @param option Die ausgewählte Suchoption
     */
    public void loadCards(String str, int option)
    {
        pCardList.reset();
        iCurrentLastIndex = -1;

        DataCallback<CardOverview> commoncallback = new DataCallback<CardOverview>() {
            @Override public void onSuccess(List<CardOverview> data) {
                pCardList.addCards(data, PAGES.REFERENCES_EDIT_PAGE);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg,NotificationType.ERROR,5);
            }

            @Override public void onInfo(String msg) {
                NotificationGUI.addNotification(msg, NotificationType.INFO, 5);
            }
        };

        switch(option)
        {
            /*By Content*/  case 0: CardController.getInstance().getCardsBySearchterms(str, commoncallback);  break;
            /*By Tag*/      case 1: CardController.getInstance().getCardsByTag(str, commoncallback);          break;
            /*By Category*/ case 2: CategoryController.getInstance().getCardsInCategory(str, commoncallback); break;
        }

        reposition();
        resize();
    }
}
