package com.swp.GUI.Cards;

import java.util.List;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.CardOverview;
import com.swp.GUI.Page;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.Controller.DataCallback;

public class CardSelectPage extends Page 
{
    public interface CardReturnFunc
    {
        void run(CardOverview categories);
    }


    private Searchbar pSearchbar;
    private CardList pCardList;
    private CardReturnFunc pReturnFunc;

    public CardSelectPage()
    {
        super("Card select", "cardselectpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/cardselectpage.xml"));

        RenderGUI canvas = findChildByID("canvas");

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), true, new CardListSelectmodeCallback() {
            @Override public void enterSelectmod() 
            { 
                if(pReturnFunc != null && pCardList.getSelection().size() > 0)
                    pReturnFunc.run(pCardList.getSelection().get(0)); 
            }
            @Override public void exitSelectmod()  { }
        });
        pCardList.setSizeInPercent(true, true);
        pCardList.setSelectMode(true);
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
        }, new SearchbarCallback() {
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

    public void reset(CardReturnFunc returnfunc)
    {
        this.pReturnFunc = returnfunc;
        loadCards(0, 30);
    }

    public void loadCards(int from, int to)
    {
        if(from == 0)
            pCardList.reset();
        CardController.getInstance().getCardsToShow(from, to, new DataCallback<CardOverview>() {
            @Override public void onSuccess(List<CardOverview> data)
            {
                pCardList.addCards(data);
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

    public void loadCards(String str, int option)
    {
        pCardList.reset();

        DataCallback<CardOverview> commoncallback = new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
                pCardList.addCards(data);
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
}
