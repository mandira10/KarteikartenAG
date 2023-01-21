package com.swp.GUI.Decks;

import java.util.List;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.DataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.DeckList;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Extras.DeckList.DeckListCallback;

public class DeckSelectPage extends Page
{
    private RenderGUI pCanvas;
    private DeckList pDeckList;
    private List<Card> alCards;

    public DeckSelectPage()
    {
        super("Deck Selection");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckselectionpage.xml"));

        pCanvas = findChildByID("canvas");

        pDeckList = new DeckList(new ivec2(0, 0), new ivec2(100, 100), new DeckListCallback() {
            @Override public void run(StudySystem deck)
            {
                selectDeck(deck);
            }
        });
        pDeckList.setSizeInPercent(true, true);
        pCanvas.addGUI(pDeckList);
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                PageManager.viewLastPage();
            }
        });


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void reset(List<Card> cards)
    {
        alCards = cards;
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
    
    private void selectDeck(StudySystem deck)
    {
        ConfirmationGUI.openDialog("Do you want to add your cards to " + String.valueOf(deck.getName()) + "?", new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                //add alCards to deck
                PageManager.viewLastPage();
            }
        });
    }
}