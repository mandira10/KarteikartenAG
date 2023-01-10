package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.DataModel.Tag;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardRenderer.CardRenderer;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.RatingGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Extras.RatingGUI.RateCallback;
import com.swp.GUI.PageManager.PAGES;

public class ViewSingleCardPage extends Page
{
    RatingGUI pRatingGUI;
    Card pCard;
    RenderGUI pCanvas;
    CardRenderer pCardRenderer;
    Text pTagsText, pCategoriesText;
    Box pReferencesBox;
    TextBox pReferencesText;

    public ViewSingleCardPage()
    {
        super("View Card");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/viewcardpage.xml"));

        pTagsText = (Text)findChildByID("tagstext");
        pCategoriesText = (Text)findChildByID("categoriestext");

        pCanvas = findChildByID("canvas");
        pCardRenderer = new CardRenderer();
        pCardRenderer.setPositionInPercent(true, false);
        pCanvas.addGUI(pCardRenderer);

        pReferencesBox = new Box(new ivec2(10, 20), new ivec2(80, 70));
        pReferencesBox.setPositionInPercent(true, true);
        pReferencesBox.setSizeInPercent(true, true);
        pReferencesBox.hide(true);
        pCanvas.addGUI(pReferencesBox);

        pReferencesText = new TextBox("References", FontManager.getInstance().getDefaultFont(), new ivec2(15, 15), new ivec2(70, 70));
        pReferencesText.setPositionInPercent(true, true);
        pReferencesText.setSizeInPercent(true, true);
        pReferencesText.setTextSize(50);
        pReferencesText.setAlignment(Alignment.LEFT);
        pReferencesText.setAutoInsertLinebreaks(true);
        pReferencesText.getBox().hide(true);
        pReferencesBox.addGUI(pReferencesText);

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
            editButton.onClick(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    EditCardPage page = (EditCardPage)PageManager.getPage(PAGES.CARD_EDIT);
                    page.editCard(pCard, false);
                    PageManager.viewPage(PAGES.CARD_EDIT);
                }
            });
        }

        Button backButton = (Button)findChildByID("backbutton");
        if(backButton != null)
        {
            backButton.onClick(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    //PageManager.viewPage(PAGES.CARD_OVERVIEW);
                    PageManager.viewLastPage();
                }
            });
        }

        Button flipButton = (Button)findChildByID("flipcardbutton");
        flipButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                pCardRenderer.flip();
            }
        });

        Button referencesButton = (Button)findChildByID("referencesbutton");
        referencesButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                pReferencesBox.hide(!pReferencesBox.isHidden());
                pCardRenderer.hide(!pReferencesBox.isHidden());
            }
        });

        Button deleteButton = (Button)findChildByID("deletebutton");
        deleteButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                deleteCard();
            }
        });
        
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

        updateReferences();
    }

    private void updateReferences()
    {
        String referencesStr = "";
        for(String line : pCard.getReferences().split("\n"))
        {
            String[] args = line.split(";");
            if(args.length < 2)
                continue;
            String type = args[0];
            String uuid = args[1];
            String desc = "";
            for(int i = 2; i < args.length; i++)
                desc += args[i];
            
            referencesStr += desc + "\n";
        }
        pReferencesText.setString(referencesStr);
    }


    private void deleteCard()
    {
        ConfirmationGUI.openDialog("Are you sure that you want to delete this card?", new ConfirmationCallback() {
            @Override public void onConfirm() 
            {  
                if(CardController.deleteCard(pCard))
                    ((CardOverviewPage)PageManager.viewPage(PAGES.CARD_OVERVIEW)).loadCards(0, 30);
            }
            @Override public void onCancel() 
            {
            }
        });
    }
}
