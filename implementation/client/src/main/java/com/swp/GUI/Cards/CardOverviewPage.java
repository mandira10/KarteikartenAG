package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Debug;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.SettingsPage;
import com.swp.GUI.Settings.ExportSettingsPage;

public class CardOverviewPage extends Page 
{
    public void getCardsToShowInitially()
    {
        long begin = 0;
        long end = 0;
        CardController.getCardsToShow(begin,end);
    }

    class CardOverviewEntry extends RenderGUI
    {
        private Card pCard;
        private TextBox pCardNameBox;
        private Switch pSelectSwitch;

        public CardOverviewEntry(Card card, ivec2 pos, ivec2 size)
        {
            this.vSize = size;
            this.vPos = pos;
            this.pCard = card;
            FontManager fonts = FontManager.getInstance();

            this.pCardNameBox = new TextBox(card.getUUID(), fonts.getDefaultFont(), new ivec2(0, 0), new ivec2(100, 100));
            pCardNameBox.setSizeInPercent(true, true);
            pCardNameBox.setAlignment(TextBox.Alignment.LEFT);
            pCardNameBox.setTextSize(20);
            pCardNameBox.setTextOffset(new ivec2(-10, 0));
            //pCardNameBox.setCornerRadius(new vec4(cornerradius));
            addElement(pCardNameBox);

            pSelectSwitch = new Switch(new ivec2(100, 10), new ivec2(20, 20), 0.0f);
            pSelectSwitch.setPositionInPercent(true, false);
            pSelectSwitch.setOrigin(new ivec2(30, 0));
            addElement(pSelectSwitch);

            reposition();
            resize();
        }

        @Override
        public void update()
        {
            if(bIsHidden)
                return;

            if(isMouseInside())
            {
                Mouse.setActiveHovering(true);
                Mouse mouse = Window.CurrentlyBoundWindow.getMouse();
                mouse.setCursor(Mouse.GUM_CURSOR_HAND);

                if(hasClickedInside())
                {
                    ViewSingleCardPage page = (ViewSingleCardPage)PageManager.getPage("ViewSingleCard");
                    page.setCard(pCard);
                    PageManager.viewPage(page);
                }
            }

            updatechildren();
        }
    };

    private TextField pSearchField; //TODO

    public CardOverviewPage()
    {
        super("Card Overview");
        this.vSize = new ivec2(100,100);

        float cornerradius = 7.0f;

        int buttonWidth = 200;
        int textSize = 24;
        addGUI(XMLGUI.loadFile("guis/cardoverviewpage.xml"));

        RenderGUI optionsMenu = findChildByID("menu");

        Button addCardButton = (Button)findChildByID("addcardbutton");
        if(addCardButton != null)
        {
            addCardButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    Debug.info("Add Card Button");
                }
            });
        }

        Button editCardButton = (Button)findChildByID("editcardbutton");
        if(editCardButton != null)
        {
            editCardButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    Debug.info("Edit Card Button");
                }
            });
        }

        Button deleteCardButton = (Button)findChildByID("deletecardbutton");
        if(deleteCardButton != null)
        {
            deleteCardButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    Debug.info("Delete Card Button");
                }
            });
        }


        RenderGUI canvas = findChildByID("canvas");
        Scroller canvasScroller = new Scroller(new ivec2(0, 0), new ivec2(100, 100));
        canvasScroller.setSizeInPercent(true, true);
        canvas.addGUI(canvasScroller);

        
        int entryHeight = 40;
        int gapSize = 5;
        int numentries = 0;
        for(Card card : CardController.getCardsToShow(0, 100))
        {
            CardOverviewEntry entry = new CardOverviewEntry(card, new ivec2(0, numentries++ * (entryHeight + gapSize)), new ivec2(100, entryHeight));
            entry.setSizeInPercent(true, false);
            canvasScroller.addGUI(entry);
        }

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void deleteCards(){
        CardController.deleteCards(null);
    }

    private void deleteCard()
    {
        CardController.deleteCard(null);
    }

    private void exportCards()
    {
        ExportSettingsPage.setToExport(null);
        PageManager.viewPage("ExportSettingsPage");
    }
}
