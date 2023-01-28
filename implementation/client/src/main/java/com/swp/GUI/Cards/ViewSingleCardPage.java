package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardRenderer.CardRenderer;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.RatingGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.RatingGUI.RateCallback;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.References.ReferencesGUI;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;

import java.util.List;


public class ViewSingleCardPage extends Page
{
    private RatingGUI pRatingGUI;
    private Card pCard;
    private RenderGUI pCanvas;
    private CardRenderer pCardRenderer;
    private Text pTagsText, pCategoriesText;
    private ReferencesGUI pReferences;
    private PAGES iLastPage;

    public ViewSingleCardPage()
    {
        super("View Card", "viewcardpage");
        this.vSize = new ivec2(100,100);
        this.iLastPage = PAGES.CARD_OVERVIEW;

        addGUI(XMLGUI.loadFile("guis/cards/viewcardpage.xml"));

        pTagsText = (Text)findChildByID("tagstext");
        pCategoriesText = (Text)findChildByID("categoriestext");

        pCanvas = findChildByID("canvas");
        pCardRenderer = new CardRenderer();
        pCardRenderer.setPositionInPercent(true, false);
        pCanvas.addGUI(pCardRenderer);

        pReferences = new ReferencesGUI(new ivec2(10, 20), new ivec2(80, 70));
        pReferences.setPositionInPercent(true, true);
        pReferences.setSizeInPercent(true, true);
        pReferences.hide(true);
        pCanvas.addGUI(pReferences);

        pRatingGUI = new RatingGUI(new ivec2(100, 70), 30, 5);
        pRatingGUI.setPositionInPercent(true, false);
        pRatingGUI.setOrigin(new ivec2(180, 0));
        pRatingGUI.setCallback(new RateCallback() {
            @Override public void run(int rating) 
            { 
                pCard.setRating(rating); 
                CardController.getInstance().updateCardData(pCard, false, new SingleDataCallback<Boolean>() {
                    @Override public void onSuccess(Boolean data) {}
                    @Override public void onFailure(String msg) {
                        NotificationGUI.addNotification("Failed to submit rating: " + msg, NotificationType.ERROR, 5);
                    }
                });
            }
        });
        addElement(pRatingGUI);
        
        Button editButton = (Button)findChildByID("editbutton");
        if(editButton != null)
        {
            editButton.onClick((RenderGUI gui) -> {
                EditCardPage page = (EditCardPage)PageManager.getPage(PAGES.CARD_EDIT);
                page.editCard(pCard, false);
                PageManager.viewPage(PAGES.CARD_EDIT);
            });
        }

        Button backButton = (Button)findChildByID("backbutton");
        backButton.onClick((RenderGUI gui) -> { PageManager.viewPage(iLastPage); });

        Button flipButton = (Button)findChildByID("flipcardbutton");
        flipButton.onClick((RenderGUI gui) -> { pCardRenderer.flip(); });

        Button referencesButton = (Button)findChildByID("referencesbutton");
        referencesButton.onClick((RenderGUI gui) -> {
            pReferences.hide(!pReferences.isHidden());
            pCardRenderer.hide(!pReferences.isHidden());
            updateReferences();
        });

        Button deleteButton = (Button)findChildByID("deletebutton");
        deleteButton.onClick((RenderGUI gui) -> { deleteCard(); });
        
        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setCard(Card card, PAGES lastpage)
    {
        iLastPage = lastpage;
        pCard = card;
        pCardRenderer.setCard(card);
        pRatingGUI.setRating(card.getRating());
        CardController.getInstance().getTagsToCard(card, new DataCallback<Tag>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Tag> tags)
            {
                String tagStr = "";
                for(Tag tag : tags)
                    tagStr += tag.getVal() + ", ";
                if(tagStr.length() > 0)
                    tagStr = tagStr.substring(0, tagStr.length() - 2);
                pTagsText.setString(tagStr);
            }

            @Override public void onFailure(String msg) 
            {
                NotificationGUI.addNotification("Failed to get Tags: " + msg, NotificationType.WARNING, 5);
            }
        });
        CategoryController.getInstance().getCategoriesToCard(card, new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
                String categoriesStr = "";
                for(Category category : data)
                    categoriesStr += category.getName() + ", ";
                if(categoriesStr.length() > 0)
                    categoriesStr = categoriesStr.substring(0, categoriesStr.length() - 2);
                pCategoriesText.setString(categoriesStr);

            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onInfo(String msg) {

            }
        });

        pReferences.hide(true);
        pCardRenderer.hide(false);
    }

    public void setCard(CardOverview cardoverview, PAGES lastpage)
    {
        CardController.getInstance().getCardByUUID(cardoverview.getUUUID(), new SingleDataCallback<Card>() {
            @Override public void onSuccess(Card card) {
                setCard(card, lastpage);    
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, NotificationType.ERROR, 5);
            }
        });
    }

    private void updateReferences()
    {
        if(!pReferences.isHidden())
            pReferences.interpreteString(pCard.getReferences());
    }


    private void deleteCard()
    {
        ConfirmationGUI.openDialog("Are you sure that you want to delete this card?", new ConfirmationCallback() {
            @Override public void onConfirm() 
            {  
                CardController.getInstance().deleteCard(pCard, new SingleDataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        ((CardOverviewPage)PageManager.viewPage(PAGES.CARD_OVERVIEW)).loadCards(0, 30);
                    }

                    @Override
                    public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                        ((CardOverviewPage)PageManager.viewPage(PAGES.CARD_OVERVIEW)).loadCards(0, 30);
                    }
                });

            }
            @Override public void onCancel() 
            {
            }
        });
    }
}
