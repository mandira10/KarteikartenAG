package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.PageManager.PAGES;

public class ViewSingleDeckPage extends Page
{
    private Deck pDeck;
    private RenderGUI pCanvas;

    public ViewSingleDeckPage()
    {
        super("View Deck");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckviewpage.xml"));
        pCanvas = findChildByID("canvas");

        RenderGUI optionsMenu = findChildByID("menu");
        Button startButton = (Button)optionsMenu.findChildByID("starttestbutton");
        startButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                ((TestDeckPage)PageManager.viewPage(PAGES.DECK_TEST)).startTests(pDeck);
            }
        });

        Button editButton = (Button)optionsMenu.findChildByID("editdeckbutton");
        editButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(pDeck);
            }
        });


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setDeck(Deck deck)
    {
        this.pDeck = deck;
        pCanvas.destroyChildren();

        CardList cardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), DeckController.getCardsInDeck(this.pDeck));
        cardList.setSizeInPercent(true, true);
        pCanvas.addGUI(cardList);

        resize();
        reposition();
    }
}