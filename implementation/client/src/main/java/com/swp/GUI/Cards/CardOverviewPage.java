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
                    Debug.info("Add Card Button");
                }
            });
        }

        Button editCardButton = (Button)findChildByID("editcardbutton");
        if(editCardButton != null)
        {
            editCardButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    Debug.info("Edit Card Button");
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
        Scroller canvasScroller = new Scroller(new ivec2(0, 0), new ivec2(100, 100));
        canvasScroller.setSizeInPercent(true, true);
        canvas.addGUI(canvasScroller);
        
        int entryHeight = 40;
        int gapSize = 5;
        int numentries = 0;
        for(Card card : CardController.getCardsToShow(0, 100))
        {
            CardOverviewEntry entry = new CardOverviewEntry(card, new ivec2(0, numentries++ * (entryHeight + gapSize)), new ivec2(100, entryHeight));
            entry.setSizeInPercent(true, false);
            canvasScroller.addGUI(entry);
        }

        this.setSizeInPercent(true, true);
        reposition();
        resize();
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
