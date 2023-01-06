package com.swp.GUI.Decks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem.StudySystemType;
import com.swp.DataModel.StudySystem.StudySystemType.KNOWN_TYPES;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.TestCardGUI;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.RatingGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Extras.RatingGUI.RateCallback;
import com.swp.GUI.PageManager.PAGES;

public class TestDeckPage extends Page
{
    private RenderGUI pCanvas;
    private Deck pDeck;
    private TestCardGUI pTestGUI;
    private Text pTimeText;
    private boolean bAnswerChecked, bNextCardAllowed;
    private long lStartTime;
    private float fElapsedSeconds;
    private boolean bStopTime;

    public TestDeckPage()
    {
        super("Test Deck");
        this.vSize = new ivec2(100,100);
        this.bAnswerChecked = false;
        this.bNextCardAllowed = false;
        this.bStopTime = false;

        addGUI(XMLGUI.loadFile("guis/decks/decktestpage.xml"));
        pCanvas = findChildByID("canvas");

        pTimeText = new Text("", FontManager.getInstance().getDefaultFont(), new ivec2(10, 10), 0);
        pTimeText.setCharacterHeight(40);
        pTimeText.hide(true);
        addGUI(pTimeText);

        RenderGUI optionsMenu = findChildByID("menu");
        RatingGUI ratingGUI = new RatingGUI(new ivec2(50, 0), 20, 5);
        ratingGUI.setPositionInPercent(true, false);
        ratingGUI.setOrigin(new ivec2(50, 0));
        ratingGUI.setOriginInPercent(true, false);
        ratingGUI.hide(true);
        ratingGUI.setCallback(new RateCallback() {
            @Override public void run(int rating) 
            {
                bNextCardAllowed = true;
                pDeck.getStudySystem().giveRating(rating);
            }
        });
        optionsMenu.addGUI(ratingGUI);

        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                cancelTest();
            }
        });

        Button checkButton = (Button)optionsMenu.findChildByID("checkbutton");
        checkButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                if(!bAnswerChecked)
                {
                    checkButton.setTitle("Next");
                    bAnswerChecked = true;
                    bNextCardAllowed = true;
                    bStopTime = true;

                    pDeck.getStudySystem().giveAnswer(pTestGUI.checkAnswers());
                    switch(pDeck.getStudySystem().getType().getType())
                    {
                        case VOTE:   
                            bNextCardAllowed = false; 
                            ratingGUI.hide(false);
                            break;

                        case TIMING: 
                            pDeck.getStudySystem().giveTime((int)fElapsedSeconds);
                            break;

                        default:
                            break;
                    }
                }
                else
                {
                    if(bNextCardAllowed)
                    {
                        ratingGUI.hide(true);
                        bAnswerChecked = false;
                        checkButton.setTitle("Check");
                        nextCard();
                    }
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
        pTimeText.hide(pDeck.getStudySystem().getType().getType() != KNOWN_TYPES.TIMING);
        nextCard();
    }

    private void nextCard()
    {
        this.lStartTime = System.nanoTime();
        this.bStopTime = false;
        pCanvas.destroyChildren();

        Card nextCard = pDeck.getStudySystem().getNextCard(0);
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
        pDeck.getStudySystem().finishTest();
        ((TestDeckFinishPage)PageManager.viewPage(PAGES.DECK_TEST_FINAL)).setDeck(pDeck);
    }

    private void cancelTest()
    {
        ConfirmationGUI.openDialog("Are you sure that you want to Cancel?", new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {
                finishTest();
            }
        });
    }

    @Override
    public void update()
    {
        if(bIsHidden)
            return;

        if(!bStopTime)
        {
            long convert = TimeUnit.SECONDS.convert(System.nanoTime() - lStartTime, TimeUnit.NANOSECONDS);
            pTimeText.setString(String.valueOf(convert));
        }

        updatechildren();
    }
}