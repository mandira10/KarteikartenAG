package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.GUI.PageManager.PAGES;

public class CardOverviewPage extends Page 
{
    public void getCardsToShowInitially()
    {
        long begin = 0;
        long end = 0;
        CardController.getCardsToShow(begin,end);
    }


    private TextField pSearchField; //TODO

    public CardOverviewPage()
    {
        super("Card Overview");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/cardoverviewpage.xml"));

        RenderGUI optionsMenu = findChildByID("menu");

        Button addCardButton = (Button)findChildByID("addcardbutton");
        if(addCardButton != null)
        {
            addCardButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    PageManager.viewPage(PAGES.CARD_CREATE);
                }
            });
        }

        Button deleteCardButton = (Button)findChildByID("deletecardbutton");
        if(deleteCardButton != null)
        {
            deleteCardButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    Debug.info("Delete Card Button");
                }
            });
        }


        RenderGUI canvas = findChildByID("canvas");

        CardList cardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), CardController.getCardsToShow(0, 100));
        cardList.setSizeInPercent(true, true);
        canvas.addGUI(cardList);

        Searchbar searchbar = new Searchbar(new ivec2(20, 100), new ivec2(40, 30), "Search Card", new SearchbarCallback() {
            @Override public void run() 
            {
                //TODO search
            }
        });
        searchbar.setPositionInPercent(false, true);
        searchbar.setSizeInPercent(true, false);
        searchbar.setOrigin(new ivec2(0, 50));
        addGUI(searchbar);

        this.setSizeInPercent(true, true);
        reposition();
    }

    public void deleteCards(){
        CardController.deleteCards(null);
    }

    private void deleteCard()
    {
        CardController.deleteCard(null);
    }

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
