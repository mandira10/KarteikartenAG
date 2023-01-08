package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardRenderer.CardRenderer;
import com.swp.GUI.Extras.RatingGUI;
import com.swp.GUI.Extras.RatingGUI.RateCallback;
import com.swp.GUI.PageManager.PAGES;

public class ViewSingleCardPage extends Page
{
    RatingGUI pRatingGUI;
    Card pCard;
    RenderGUI pCanvas;
    CardRenderer pCardRenderer;
    Text pTagsText, pCategoriesText;

    public ViewSingleCardPage()
    {
        super("View Card");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/viewcardpage.xml"));

        pTagsText = (Text)findChildByID("tagstext");
        pCategoriesText = (Text)findChildByID("categoriestext");

        pCanvas = findChildByID("canvas");
        pCardRenderer = new CardRenderer();
        pCanvas.addGUI(pCardRenderer);

        pRatingGUI = new RatingGUI(new ivec2(100, 70), 30, 5);
        pRatingGUI.setPositionInPercent(true, false);
        pRatingGUI.setOrigin(new ivec2(180, 0));
        pRatingGUI.setCallback(new RateCallback() {
            @Override public void run(int rating) { pCard.setRating(rating); }
        });
        addElement(pRatingGUI);
        
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
                    //PageManager.viewPage(PAGES.CARD_OVERVIEW);
                    PageManager.viewLastPage();
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
    }

    public void setCard(Card card)
    {
        pCard = card;
        pCardRenderer.setCard(card);
        pRatingGUI.setRating(card.getRating());
        String tagStr = "";
        for(Tag tag : CardController.getTagsToCard(card))
            tagStr += tag.getVal() + ", ";

        if(tagStr.length() > 0)
            tagStr = tagStr.substring(0, tagStr.length() - 2);
        pTagsText.setString(tagStr);

        String categoriesStr = "TODO Categories";
        //TODO
        //for(Category tag : CategoryController.getCategoriesOfCard(card))
        //    categoriesStr += tag.getVal() + ", ";
        if(categoriesStr.length() > 0)
            categoriesStr = categoriesStr.substring(0, categoriesStr.length() - 2);
        pTagsText.setString(categoriesStr);
    }
}
