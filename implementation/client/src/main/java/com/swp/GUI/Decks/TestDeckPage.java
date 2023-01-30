package com.swp.GUI.Decks;

import java.util.concurrent.TimeUnit;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.StudySystem.StudySystemType;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.TestCardGUI;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.RatingGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.PageManager.PAGES;

public class TestDeckPage extends Page
{
    private RenderGUI pCanvas;
    private StudySystem pDeck;
    private TestCardGUI pTestGUI;
    private Text pTimeText;
    private boolean bAnswerChecked, bNextCardAllowed, bCurrentAnswer;
    private boolean bStopTime;
    private long lStartTime, lTimeLimit;
    private float fElapsedSeconds;
    private Card pCardToLearn;
    private Button pCheckButton, pAnswerOverrideButton;
    private RatingGUI pRatingGUI;

    private StudySystemController studySystemController = StudySystemController.getInstance();

    public TestDeckPage()
    {
        super("Test Deck", "testdeckpage");
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
        pRatingGUI = (RatingGUI)findChildByID("ratinggui");
        pRatingGUI.hide(true);
        pRatingGUI.setCallback((int rating) -> {
            bNextCardAllowed = true;
            studySystemController.giveRating(pDeck, pCardToLearn, rating, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {
                    NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                }
            });
        });
        optionsMenu.addGUI(pRatingGUI);

        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> { cancelTest(); });

        pAnswerOverrideButton = (Button)findChildByID("wascorrectbutton");
        pAnswerOverrideButton.onClick((RenderGUI gui) -> { giveAnswer(true); });
        pAnswerOverrideButton.hide(true);

        pCheckButton = (Button)optionsMenu.findChildByID("checkbutton");
        pCheckButton.onClick((RenderGUI gui) -> {
            if(!bAnswerChecked)
            {
                checkAnswer();
            }
            else if(bNextCardAllowed)
            {
                giveAnswer(bCurrentAnswer);
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void startTests(StudySystem deck)
    {
        this.pDeck = deck;
        pTimeText.hide(true);
        pAnswerOverrideButton.hide(true);
        if(pDeck.getType() == StudySystem.StudySystemType.TIMING)
        {
            pTimeText.hide(false);
            lTimeLimit = (long)((TimingSystem)pDeck).getTimeLimit();
        }
        nextCard();
    }

    private void nextCard()
    {
        pAnswerOverrideButton.hide(true);
        this.lStartTime = System.nanoTime();
        this.bStopTime = false;
        this.bCurrentAnswer = false;
        pCanvas.destroyChildren();
        pTimeText.setColor(GUI.getTheme().textColor);
        studySystemController.getNextCard(pDeck,  new SingleDataCallback<Card>() {
            @Override public void onSuccess(Card data) 
            {
                pCardToLearn = data;
                if(pCardToLearn == null)
                {
                    finishTest();
                }
                else
                {
                    pTestGUI = new TestCardGUI(pCardToLearn);
                    pCanvas.addGUI(pTestGUI);
                    reposition();
                    resize();
                }
            }

            @Override public void onFailure(String msg) 
            {

            }
        });
    }


    private void finishTest()
    {
        ((TestDeckFinishPage)PageManager.viewPage(PAGES.DECK_TEST_FINAL)).setDeck(pDeck);
    }

    private void cancelTest()
    {
        ConfirmationGUI.openDialog("Are you sure that you want to Cancel?", new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() {
                finishTest();
            }
        });
    }

    @Override
    public void updateextra()
    {
        if(!bStopTime)
        {
            long convert = TimeUnit.SECONDS.convert(System.nanoTime() - lStartTime, TimeUnit.NANOSECONDS);
            pTimeText.setString(String.valueOf(convert));
            if(!pTimeText.isHidden() && !bAnswerChecked && convert > lTimeLimit)
            {
                checkAnswer();
                pTimeText.setColor(new vec4(0.93f, 0.32f, 0.33f, 1));
            }
        }
    }

    public void checkAnswer()
    {
        pCheckButton.setTitle("Next");
        bAnswerChecked = true;
        bNextCardAllowed = true;
        bStopTime = true;

        bCurrentAnswer = pTestGUI.checkAnswers();
        pAnswerOverrideButton.hide(bCurrentAnswer || pDeck.getType() == StudySystemType.VOTE);

        switch(pDeck.getType())
        {
            case VOTE:   
                bNextCardAllowed = false; 
                pRatingGUI.setRating(0);
                pRatingGUI.hide(false);
                break;

            case TIMING:
                studySystemController.giveTime(pDeck, ((int) fElapsedSeconds), new SingleDataCallback<Boolean>() {
                    @Override public void onSuccess(Boolean data) {}
                    @Override public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });
                break;

            default:
                break;
        }
    }

    void giveAnswer(boolean answer)
    {
        pRatingGUI.hide(true);
        bAnswerChecked = false;
        pCheckButton.setTitle("Check");
        studySystemController.giveAnswer(pDeck, answer, new SingleDataCallback<Boolean>() {
            @Override public void onSuccess(Boolean data) 
            { 
                nextCard(); 
            }
            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }
}