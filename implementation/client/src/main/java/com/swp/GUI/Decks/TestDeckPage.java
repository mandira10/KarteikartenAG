package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.Card;
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
    boolean bAnswerChecked;

    public TestDeckPage()
    {
        super("Test Deck");
        this.vSize = new ivec2(100,100);
        this.bAnswerChecked = false;

        addGUI(XMLGUI.loadFile("guis/decks/decktestpage.xml"));
        pCanvas = findChildByID("canvas");

        RenderGUI optionsMenu = findChildByID("menu");

        Button checkButton = (Button)optionsMenu.findChildByID("checkbutton");
        checkButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                if(!bAnswerChecked)
                {
                    if(pTestGUI.checkAnswers())
                    {
                        checkButton.setTitle("Next");
                        bAnswerChecked = true;
                    }
                }
                else
                {
                    bAnswerChecked = false;
                    checkButton.setTitle("Check");
                    nextCard();
                }
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void startTests(Deck deck)
    {
        this.pDeck = deck;
        nextCard();
    }

    private void nextCard()
    {
        pCanvas.destroyChildren();

        Card nextCard = pDeck.getPStudySystem().getNextCard();
        if(nextCard == null)
        {
            finishTest();
            return;
        }

        pTestGUI = new TestCardGUI(nextCard);
        pCanvas.addGUI(pTestGUI);
        reposition();
        resize();
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