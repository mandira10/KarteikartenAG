package com.swp.GUI.Category;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.MenuOptions;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.PageManager.PAGES;

import java.util.List;

/**
 * Die Seite auf welcher eine einzelne Kategorie angesehen werden kann
 */
public class ViewSingleCategoryPage extends Page
{
    private Category pCategory;
    private final RenderGUI pCanvas;
    private final CardList pCardList;

    /**
     * Der Standardkonstruktor für die Klasse ViewSingleCategoryPage
     */
    public ViewSingleCategoryPage()
    {
        super("View Category", "viewcategorypage");
        this.vSize = new ivec2(100, 100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryviewpage.xml"));
        pCanvas = findChildByID("canvas");


        MenuOptions menu = (MenuOptions)findChildByID("menu");
        Button editButton = (Button)findChildByID("editcategorybutton");
        editButton.onClick((RenderGUI gui) -> {
            ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).editCategory(pCategory,false);
        });

        Button deleteButton = (Button)findChildByID("deletecategorybutton");
        deleteButton.onClick((RenderGUI gui) -> {
            deleteCategory();
        });

        Button removeCardsButton = (Button)findChildByID("removecardbutton");
        removeCardsButton.onClick((RenderGUI gui) -> {
            removeCards();
        });
        removeCardsButton.hide(true);

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), false, new CardListSelectmodeCallback() {
            @Override public void enterSelectmode()
            {    
                removeCardsButton.hide(false);
                menu.resize();
            }
            @Override public void exitSelectmode()
            {   
                removeCardsButton.hide(true);
                menu.resize();
            }
        });
        pCardList.setSizeInPercent(true, true);
        pCanvas.addGUI(pCardList);


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setCategory(Category category)
    {
        this.pCategory = category;

        pCardList.reset();
        CategoryController.getInstance().getCardsInCategory(this.pCategory, new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
                pCardList.addCards(data, PAGES.CATEGORY_SINGLEVIEW);
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

            @Override
            public void onInfo(String msg) {
            }
        });

        resize();
        reposition();
    }

    private void deleteCategory()
    {
        ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("confirmcategorydelete"), new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                CategoryController.getInstance().deleteCategory(pCategory, new SingleDataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {

                    }

                    @Override
                    public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });
                ((CategoryOverviewPage)PageManager.viewPage(PAGES.CATEGORY_OVERVIEW)).loadCategories();
            }
        });
    }

    private void removeCards()
    {
        int numCards = pCardList.getSelection().size();
        ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("confirmremovecardscat").replace("$", String.valueOf(numCards)), new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                CategoryController.getInstance().removeCardsFromCategory(pCardList.getSelection(), pCategory, new SingleDataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                       setCategory(pCategory);
                    }

                    @Override
                    public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });

            }
        });
    }
}
