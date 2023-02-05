package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Locale;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.Extras.*;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;

import java.util.List;

/**
 * Die Seite auf welcher ein einzelnes Deck angesehen werden kann
 * 
 * @author Tom Beuke
 */
public class ViewSingleDeckPage extends Page
{
    private StudySystem pDeck;
    private final RenderGUI pCanvas;
    private final CardList pCardList;

    /**
     * Der Standardkonstruktor der Klasse ViewSingleDeckPage
     */
    public ViewSingleDeckPage()
    {
        super("View Deck", "viewdeckpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckviewpage.xml"));
        pCanvas = findChildByID("canvas");

        MenuOptions menu = (MenuOptions)findChildByID("menu");

        //Start test button
        Button startButton = (Button)findChildByID("starttestbutton");
        startButton.getBox().setTextColor(new vec4(0,0,0,1));
        startButton.onClick((RenderGUI gui) -> { ((TestDeckPage)PageManager.viewPage(PAGES.DECK_TEST)).startTests(pDeck); });

        //Edit button
        Button editButton = (Button)findChildByID("editdeckbutton");
        editButton.onClick((RenderGUI gui) -> { ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(pDeck.getUuid()); });

        //Edit button
        Button resetProgressButton = (Button)findChildByID("resetbutton");
        resetProgressButton.onClick((RenderGUI gui) -> { resetProgress(); });

        //Export button
        Button exportDeckButton = (Button)findChildByID("exportbutton");
        exportDeckButton.onClick((RenderGUI gui) -> { exportCards();});

        //Delete button
        Button deleteDeckButton = (Button)findChildByID("deletedeckbutton");
        deleteDeckButton.onClick((RenderGUI gui) -> { deleteDeck();});

        //Remove cards button
        Button removeCardsButton = (Button)findChildByID("removecardbutton");
        removeCardsButton.onClick((RenderGUI gui) -> { removeCards();});
        removeCardsButton.hide(true);

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), false, new CardListSelectmodeCallback() {
            @Override public void enterSelectmode()  { removeCardsButton.hide(false); menu.resize(); }
            @Override public void exitSelectmode()   { removeCardsButton.hide(true);  menu.resize(); }
        });
        pCardList.setSizeInPercent(true, true);
        pCanvas.addGUI(pCardList);


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setDeck(StudySystem deck)
    {
        this.pDeck = deck;
        pCardList.reset();

        StudySystemController.getInstance().getAllCardsInStudySystem(this.pDeck, new DataCallback<CardOverview>() {
            @Override public void onSuccess(List<CardOverview> data) {
                pCardList.addCards(data, PAGES.DECK_SINGLEVIEW);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

            @Override public void onInfo(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.INFO,5);
            }
        });

        resize();
        reposition();
    }

    private void deleteDeck()
    {
        ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("confirmdeletedeck"), new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                StudySystemController.getInstance().deleteStudySystem(pDeck, new SingleDataCallback<Boolean>() {
                    @Override public void onSuccess(Boolean data) {
                        ((DeckOverviewPage)PageManager.viewPage(PAGES.DECK_OVERVIEW)).loadDecks();
                    }

                    @Override public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });
            }
        });
    }

    private void removeCards()
    {
        int numCards = pCardList.getSelection().size();
        ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("confirmremovecardsdeck").replace("$", String.valueOf(numCards)), new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                StudySystemController.getInstance().removeCardsFromStudySystem(pCardList.getSelection(), pDeck, new SingleDataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                       setDeck(pDeck);
                    }

                    @Override
                    public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });

            }
        });
    }

    private void exportCards()
    {
        ((CardExportPage)PageManager.viewPage(PAGES.CARD_EXPORT)).setCards(pCardList.getCards());
    }

    private void resetProgress()
    {
        ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("confirmresetdeck"), new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                StudySystemController.getInstance().resetLearnStatus(pDeck, new SingleDataCallback<Boolean>() {
                    @Override public void onSuccess(Boolean data) {
                    }

                    @Override public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });

            }
        });
    }
}