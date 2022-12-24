package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.Deck;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.TestCardGUI;
import com.swp.GUI.PageManager.PAGES;

public class TestDeckPage extends Page
{
    RenderGUI pCanvas;
    Deck pDeck;
    TestCardGUI pTestGUI;

    public TestDeckPage()
    {
        super("Test Deck");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/decktestpage.xml"));
        pCanvas = findChildByID("canvas");

        RenderGUI optionsMenu = findChildByID("menu");

        Button checkButton = (Button)optionsMenu.findChildByID("checkbutton");
        checkButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                pTestGUI.checkAnswers();
            }
        });




        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void startTests(Deck deck)
    {
        this.pDeck = deck;
        pCanvas.destroyChildren();

        pTestGUI = new TestCardGUI(pDeck.getPStudySystem().getNextCard());
        pCanvas.addGUI(pTestGUI);

        reposition();
        resize();
    }

    private void nextCard()
    {

    }

    private void finishTest()
    {

    }

    @Override
    public void update()
    {

        updatechildren();
    }
}