package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.Card;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardRenderer.CardRenderer;
import com.swp.GUI.PageManager.PAGES;

public class ViewSingleCardPage extends Page
{
    //RatingGUI pRatingGUI; //TODO
    Card pCard;
    RenderGUI pCanvas;
    CardRenderer pCardRenderer;

    public ViewSingleCardPage()
    {
        super("View Card");
        this.vSize = new ivec2(100,100);

        //pRatingGUI = new RatingGUI(card);
        addGUI(XMLGUI.loadFile("guis/cards/viewcardpage.xml"));

        pCanvas = findChildByID("canvas");
        pCardRenderer = new CardRenderer();
        pCanvas.addGUI(pCardRenderer);
        
        Button editButton = (Button)findChildByID("editbutton");
        if(editButton != null)
        {
            editButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    EditCardPage page = (EditCardPage)PageManager.getPage(PAGES.CARD_EDIT);
                    page.editCard(pCard);
                    PageManager.viewPage(PAGES.CARD_EDIT);
                }
            });
        }

        Button backButton = (Button)findChildByID("backbutton");
        if(backButton != null)
        {
            backButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    PageManager.viewPage(PAGES.CARD_OVERVIEW);
                }
            });
        }

        Button flipButton = (Button)findChildByID("flipcardbutton");
        if(flipButton != null)
        {
            flipButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    pCardRenderer.flip();
                }
            });
        }

        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void setCard(Card card)
    {
        pCard = card;
        pCardRenderer.setCard(card);
    }
}
