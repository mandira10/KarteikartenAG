package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardRenderer.CardRenderer;

public class ViewSingleCardPage extends Page
{
    //RatingGUI pRatingGUI; //TODO
    Card pCard;
    RenderGUI pCanvas;

    public ViewSingleCardPage()
    {
        super("View Card");
        this.vSize = new ivec2(100,100);

        //pRatingGUI = new RatingGUI(card);
        addGUI(XMLGUI.loadFile("guis/viewcardpage.xml"));

        pCanvas = findChildByID("canvas");
        
        Button editButton = (Button)findChildByID("editbutton");
        if(editButton != null)
        {
            editButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    EditCardPage page = (EditCardPage)PageManager.getPage("EditCard");
                    page.editCard(pCard);
                    PageManager.viewPage(page);
                }
            });
        }

        Button backButton = (Button)findChildByID("backbutton");
        if(backButton != null)
        {
            backButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    PageManager.viewPage("CardOverview");
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
        pCanvas.destroyChildren();
        CardRenderer cardRenderer = new CardRenderer(card);
        pCanvas.addGUI(cardRenderer);
    }
}
