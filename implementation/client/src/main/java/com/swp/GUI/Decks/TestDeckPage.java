package com.swp.GUI.Decks;

import java.util.concurrent.TimeUnit;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
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
    private StudySystem pDeck;
    private TestCardGUI pTestGUI;
    private Text pTimeText;
    private boolean bAnswerChecked, bNextCardAllowed;
    private long lStartTime;
    private float fElapsedSeconds;
    private boolean bStopTime;

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
        RatingGUI ratingGUI = new RatingGUI(new ivec2(50, 0), 20, 5);
        ratingGUI.setPositionInPercent(true, false);
        ratingGUI.setOrigin(new ivec2(50, 0));
        ratingGUI.setOriginInPercent(true, false);
        ratingGUI.hide(true);
        ratingGUI.setCallback(new RateCallback() {
            @Override public void run(int rating) 
            {
                bNextCardAllowed = true;
                studySystemController.giveRating(pDeck,rating, new SingleDataCallback<>() {
                    @Override
                    public void onSuccess(Object data) {}

                    @Override
                    public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });
            }
        });
        optionsMenu.addGUI(ratingGUI);

        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                cancelTest();
            }
        });

        Button checkButton = (Button)optionsMenu.findChildByID("checkbutton");
        checkButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                if(!bAnswerChecked)
                {
                    checkButton.setTitle("Next");
                    bAnswerChecked = true;
                    bNextCardAllowed = true;
                    bStopTime = true;

                    studySystemController.giveAnswer(pDeck, pTestGUI.checkAnswers(),  new SingleDataCallback<Object>() {
                        @Override
                        public void onSuccess(Object data) {}

                        @Override
                        public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                        }
                    });
                    switch(pDeck.getType())
                    {
                        case VOTE:   
                            bNextCardAllowed = false; 
                            ratingGUI.hide(false);
                            break;

                        case TIMING:
                            studySystemController.giveTime(pDeck, ((int) fElapsedSeconds), new SingleDataCallback<Object>() {
                                @Override
                                public void onSuccess(Object data) {}

                                @Override
                                public void onFailure(String msg) {
                                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                                }
                            });
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

    public void startTests(StudySystem deck)
    {
        this.pDeck = deck;
        pTimeText.hide(pDeck.getType() != StudySystem.StudySystemType.TIMING);
        nextCard();
    }

    private void nextCard()
    {
        this.lStartTime = System.nanoTime();
        this.bStopTime = false;
        pCanvas.destroyChildren();

        studySystemController.getNextCard(pDeck,  new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card data) {
             Card nextCard = data;
                if(nextCard == null)
        {
            studySystemController.finishTestAndGetResult(pDeck, new SingleDataCallback<Integer>() {
                @Override
                public void onSuccess(Integer data) {

                }

                @Override
                public void onFailure(String msg) {

                }
            });
            return;
        }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
        //Card nextCard = pDeck.getStudySystem().getNextCard(0);//TODO
//        if(nextCard == null)
//        {
//            finishTest();
//            return;
//        }

        //pTestGUI = new TestCardGUI(nextCard);//TODO
        pCanvas.addGUI(pTestGUI);
        reposition();
        resize();
    }

    private void finishTest()
    {
        //pDeck.getStudySystem().finishTest();//TODO
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
    public void updateextra()
    {
        if(!bStopTime)
        {
            long convert = TimeUnit.SECONDS.convert(System.nanoTime() - lStartTime, TimeUnit.NANOSECONDS);
            pTimeText.setString(String.valueOf(convert));
        }
    }
}