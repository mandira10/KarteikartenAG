package com.swp.GUI.Cards;

import java.util.List;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Decks.DeckSelectPage;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.ListOrder;
import com.swp.GUI.Extras.MenuOptions;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Extras.ListOrder.Order;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.CardOverview;

public class CardOverviewPage extends Page 
{
    private Searchbar pSearchbar;
    private CardList pCardList;
    private int iCurrentLastIndex;
    private Order iOrder;
    private boolean bReverseOrder;

    public CardOverviewPage()
    {
        super("Cards", "cardoverviewpage");
        this.vSize = new ivec2(100,100);
        this.iCurrentLastIndex = 0;

        addGUI(XMLGUI.loadFile("guis/cards/cardoverviewpage.xml"));

        MenuOptions menu = (MenuOptions)findChildByID("menu");

        Button addCardButton = (Button)findChildByID("addcardbutton");
        addCardButton.onClick((RenderGUI gui) -> { PageManager.viewPage(PAGES.CARD_CREATE); });

        Button deleteCardButton = (Button)findChildByID("deletecardbutton");
        deleteCardButton.onClick((RenderGUI gui) -> { deleteCards(); });
        deleteCardButton.hide(true);

        Button exportCardsButton = (Button)findChildByID("exportbutton");
        exportCardsButton.onClick((RenderGUI gui) -> { exportCards(); });
        exportCardsButton.hide(true);

        Button addToDeckButton = (Button)findChildByID("addtodeckbutton");
        addToDeckButton.onClick((RenderGUI gui) -> {
            ((DeckSelectPage)PageManager.viewPage(PAGES.DECK_SELECTION)).reset(pCardList.getSelection());
        });
        addToDeckButton.hide(true);

        RenderGUI canvas = findChildByID("canvas");

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), false, new CardListSelectmodeCallback() {
            @Override public void enterSelectmod() { exportCardsButton.hide(false); deleteCardButton.hide(false); addToDeckButton.hide(false); menu.resize(); }
            @Override public void exitSelectmod()  { exportCardsButton.hide(true);  deleteCardButton.hide(true);  addToDeckButton.hide(true);  menu.resize(); }
        }, (RenderGUI gui) -> {
            if(iCurrentLastIndex != -1)
                loadCards(iCurrentLastIndex, iCurrentLastIndex + 30);
        });
        pCardList.setSizeInPercent(true, true);
        canvas.addGUI(pCardList);

        pSearchbar = (Searchbar)findChildByID("searchbar");
        pSearchbar.setCallback((String query, int option) -> {
            if(query.equals(""))
                loadCards(0, 30);
            else
                loadCards(query, option);
        });

        ListOrder listorder = (ListOrder)findChildByID("listorder");
        listorder.setCallback((Order order, boolean reverse) -> {
            iOrder = order;
            bReverseOrder = reverse;
            loadCards(0, 30);
        });

        this.setSizeInPercent(true, true);
        reposition();
    }

    public void loadCards(int from, int to)
    {
        if(from == 0)
        {
            iCurrentLastIndex = 0;
            pCardList.reset();
        }

        // Use order:
        //iOrder 
        //bReverseOrder

        CardController.getInstance().getCardsToShow(from, to, new DataCallback<CardOverview>() {
            @Override public void onSuccess(List<CardOverview> data)
            {
                iCurrentLastIndex += data.size();
                pCardList.addCards(data, PAGES.CARD_OVERVIEW);
            }

            @Override public void onFailure(String msg) 
            {
                NotificationGUI.addNotification(msg, NotificationType.ERROR, 5);     
            }
            @Override public void onInfo(String msg) {}
        });
    }

    public void loadCards(String str, int option)
    {
        iCurrentLastIndex = -1;
        pCardList.reset();

        DataCallback<CardOverview> commoncallback = new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
                pCardList.addCards(data, PAGES.CARD_OVERVIEW);
            }

            @Override
            public void onFailure(String msg) {
            NotificationGUI.addNotification(msg,NotificationType.ERROR,5);
            }

            @Override
            public void onInfo(String msg) {
            NotificationGUI.addNotification(msg,NotificationType.INFO,5);
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

    public void deleteCards()
    {
        int numCards = pCardList.getSelection().size();
        ConfirmationGUI.openDialog("Are you sure that you want to delete " + String.valueOf(numCards) + " cards?", new ConfirmationCallback() {
            @Override public void onConfirm() 
            {  
                CardController.getInstance().deleteCards(pCardList.getSelection(), new SingleDataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {loadCards(0,30);}

                    @Override
                    public void onFailure(String msg) {
                    NotificationGUI.addNotification("Beim Löschen der Karte ist ein Fehler aufgetreten",NotificationType.ERROR,5);
                    }
                });
            }
            @Override public void onCancel() 
            {
            }
        });
    }

    private void exportCards()
    {
        ((CardExportPage)PageManager.viewPage(PAGES.CARD_EXPORT)).setCards(pCardList.getSelection());
    }
}
