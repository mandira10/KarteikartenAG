package com.swp.GUI.Category;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.PageManager.PAGES;

import java.util.List;

public class ViewSingleCategoryPage extends Page
{
    private Category pCategory;
    private RenderGUI pCanvas;
    private CardList pCardList;

    public ViewSingleCategoryPage()
    {
        super("View Category", "viewcategorypage");
        this.vSize = new ivec2(100, 100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryviewpage.xml"));
        pCanvas = findChildByID("canvas");

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), new CardListSelectmodeCallback() {
            @Override public void enterSelectmod() 
            {    
            }
            @Override public void exitSelectmod() 
            {   
            }
        });
        pCardList.setSizeInPercent(true, true);
        pCanvas.addGUI(pCardList);

        RenderGUI optionsMenu = findChildByID("menu");
        Button editButton = (Button)optionsMenu.findChildByID("editcategorybutton");
        editButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).editCategory(pCategory);
            }
        });

        Button deleteButton = (Button)optionsMenu.findChildByID("deletecategorybutton");
        deleteButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                deleteCategory();
            }
        });


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
                pCardList.addCards(data);
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
        ConfirmationGUI.openDialog("Are you sure that you want to delete this category?", new ConfirmationCallback() {
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
}
