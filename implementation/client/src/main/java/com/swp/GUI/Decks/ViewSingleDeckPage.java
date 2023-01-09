package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.PageManager.PAGES;

public class ViewSingleDeckPage extends Page
{
    private Deck pDeck;
    private RenderGUI pCanvas;
    private CardList pCardList;

    public ViewSingleDeckPage()
    {
        super("View Deck");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckviewpage.xml"));
        pCanvas = findChildByID("canvas");

        RenderGUI optionsMenu = findChildByID("menu");
        //Start test button
        Button startButton = (Button)optionsMenu.findChildByID("starttestbutton");
        startButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  { ((TestDeckPage)PageManager.viewPage(PAGES.DECK_TEST)).startTests(pDeck); }
        });

        //Edit button
        Button editButton = (Button)optionsMenu.findChildByID("editdeckbutton");
        editButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  { ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(pDeck); }
        });

        //Delete button
        Button deleteDeckButton = (Button)findChildByID("deletedeckbutton");
        deleteDeckButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  { deleteDeck();}
        });

        //Remove cards button
        Button removeCardsButton = (Button)findChildByID("removecardbutton");
        removeCardsButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  { removeCards();}
        });
        removeCardsButton.hide(true);

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), new CardListSelectmodeCallback() {
            @Override public void enterSelectmod() 
            {
                removeCardsButton.hide(false);
            }
            @Override public void exitSelectmod() 
            {
                removeCardsButton.hide(true);
            }
        });
        pCardList.setSizeInPercent(true, true);
        pCanvas.addGUI(pCardList);


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setDeck(Deck deck)
    {
        this.pDeck = deck;
        pCardList.reset();
        pCardList.addCards(DeckController.getCardsInDeck(this.pDeck));

        resize();
        reposition();
    }

    private void deleteDeck()
    {
        ConfirmationGUI.openDialog("Are you sure that you want to delete this deck?", new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                DeckController.deleteDeck(pDeck);
            }
        });
    }

    private void removeCards()
    {
        int numCards = pCardList.getSelection().size();
        ConfirmationGUI.openDialog("Are you sure that you want to remove " + String.valueOf(numCards) + " cards from this deck?", new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                DeckController.removeCardsFromDeck(pCardList.getSelection(), pDeck);
            }
        });
    }
}