package com.swp.GUI.Decks;

import java.util.List;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.DeckList;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;

/**
 * Die Seite auf welcher man ein Deck auswählen kann
 */
public class DeckSelectPage extends Page
{
    private final DeckList pDeckList;
    private List<CardOverview> alCards;

    /**
     * Der Standardkonstruktor der Klasse DeckSelectPage
     */
    public DeckSelectPage()
    {
        super("Deck Selection", "deckselectionpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckselectionpage.xml"));

        RenderGUI pCanvas = findChildByID("canvas");

        pDeckList = new DeckList(new ivec2(0, 0), new ivec2(100, 100), this::selectDeck);
        pDeckList.setSizeInPercent(true, true);
        pCanvas.addGUI(pDeckList);
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> PageManager.viewLastPage() );


        this.setSizeInPercent(true, true);
        reposition();
    }

    /**
     * Setzt die auswahl zurück und lädt die Decks neu
     *
     * @param cards Die Karten welche zu dem Deck hinzugefügt werden sollen
     */
    public void reset(List<CardOverview> cards)
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
        ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("confirmaddtodeck").replace("$", String.valueOf(deck.getName())), new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                StudySystemController.getInstance().addCardsToStudySystem(alCards, deck, new SingleDataCallback<>() {
                    @Override
                    public void onSuccess(String data) {
                        if(!data.isBlank())
                            NotificationGUI.addNotification(data, Notification.NotificationType.INFO, 10);

                    }

                    @Override
                    public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });
                PageManager.viewLastPage(); //TODO unselect card??
            }
        });
    }
}