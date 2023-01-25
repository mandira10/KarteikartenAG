package com.swp.GUI.Category;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.Controller.SingleDataCallback;

public class EditCategoryPage extends Page
{
    private Category pNewCategory;
    private TextField pTitleField;
    private boolean bIsNewCategory;
    private boolean nameChange;

    public EditCategoryPage()
    {
        super("Edit Category", "editcategorypage");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/categories/categoryeditpage.xml"));

        //pCanvas = findChildByID("canvas");

        pTitleField = (TextField)findChildByID("titlefield");
        pTitleField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) {
                pNewCategory.setName(complete);
                nameChange = true;
            }
        });

        
        RenderGUI optionsMenu = findChildByID("menu");
        // Cancel Button
        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> { PageManager.viewLastPage(); });

        // Apply Button
        Button applyButton = (Button)optionsMenu.findChildByID("applybutton");
        applyButton.onClick((RenderGUI gui) -> { applyChanges(); });

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void editCategory(String uuid) 
    {
        CategoryController.getInstance().getCategoryByUUID(uuid, new SingleDataCallback<Category>() {
            @Override public void onSuccess(Category data) {
                editCategory(data,false);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }

    public void editCategory(Category category, boolean newCategory)
    {
        bIsNewCategory = newCategory;
        pNewCategory = new Category(category);

        pTitleField.setString(pNewCategory.getName());
    }

    private void applyChanges()
    {
        CategoryController.getInstance().updateCategoryData(pNewCategory,bIsNewCategory, nameChange, new SingleDataCallback<>() {
            @Override public void onSuccess(Boolean data) {
                PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

        });
    }
}
