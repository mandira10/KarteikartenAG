package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;

public class TestDeckFinishPage extends Page
{
    private StudySystem pDeck;
    private TextBox pFinishText;

    public TestDeckFinishPage()
    {
        super("Test Finished", "testfinishpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/decktestfinishpage.xml"));

        pFinishText = (TextBox)findChildByID("finishtext");
        pFinishText.setAutoInsertLinebreaks(true);
        pFinishText.getBox().hide(true);
        pFinishText.setAlignment(Alignment.CENTER);


        RenderGUI optionsMenu = findChildByID("menu");

        //Continue button
        Button continueButton = (Button)optionsMenu.findChildByID("continuebutton");
        continueButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) { PageManager.viewPage(PAGES.DECK_SINGLEVIEW); }
        });


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setDeck(StudySystem deck)
    {
        this.pDeck = deck;

        StudySystemController.getInstance().finishTestAndGetResult(pDeck, new SingleDataCallback<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                pFinishText.setString("Your final score is: " + data);
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });

        resize();
        reposition();
    }
}