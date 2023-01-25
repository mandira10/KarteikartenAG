package com.swp.GUI.Category;

import java.util.List;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
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
    private List<Category> aParents, aChildren;
    private TextBox pCategoriesParentsBox, pCategoriesChildrenBox;

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


        pCategoriesParentsBox = (TextBox)findChildByID("categoryparentslist");
        pCategoriesParentsBox.setAlignment(Alignment.LEFT);
        pCategoriesParentsBox.setAutoInsertLinebreaks(true);

        Button categoryParentsButton = (Button)findChildByID("editparentsbutton");
        categoryParentsButton.onClick((RenderGUI gui) -> { 
            ((CategorySelectPage)PageManager.viewPage(PAGES.CATEGORY_SELECTION)).reset(false, (categories -> {
                ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).updateParentCategories(categories);
            }), aParents);
        });


        pCategoriesChildrenBox = (TextBox)findChildByID("categorychildrenlist");
        pCategoriesChildrenBox.setAlignment(Alignment.LEFT);
        pCategoriesChildrenBox.setAutoInsertLinebreaks(true);

        Button categoryChildrenButton = (Button)findChildByID("editchildrenbutton");
        categoryChildrenButton.onClick((RenderGUI gui) -> { 
            ((CategorySelectPage)PageManager.viewPage(PAGES.CATEGORY_SELECTION)).reset(false, (categories -> {
                ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).updateChildCategories(categories);
            }), aChildren);
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
            @Override public void onSuccess(Category data) { editCategory(data, false); }
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

        pCategoriesChildrenBox.setString("");
        CategoryController.getInstance().getChildrenForCategory(category, new DataCallback<Category>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> children) {
                updateChildCategories(children);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);    
            }            
        });

        pCategoriesParentsBox.setString("");
        CategoryController.getInstance().getParentsForCategory(category, new DataCallback<Category>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> parents) {
                updateParentCategories(parents);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);    
            }            
        });
    }

    public void updateChildCategories(List<Category> children)
    {
        this.aChildren = children;
        String catString = "";
        for(Category category : aChildren)
            catString += category.getName() + ", ";
        if(catString.length() > 0)
            catString = catString.substring(0, catString.length() - 2);
        pCategoriesChildrenBox.setString(catString);
    }

    public void updateParentCategories(List<Category> parents)
    {
        this.aParents = parents;
        String catString = "";
        for(Category category : aParents)
            catString += category.getName() + ", ";
        if(catString.length() > 0)
            catString = catString.substring(0, catString.length() - 2);
        pCategoriesParentsBox.setString(catString);
    }


    private void applyChanges()
    {
        CategoryController.getInstance().updateCategoryData(pNewCategory,bIsNewCategory, nameChange, new SingleDataCallback<>() {
            @Override public void onSuccess(Boolean data) 
            {
                CategoryController.getInstance().editCategoryHierarchy(pNewCategory, aParents, aChildren, new SingleDataCallback<>() {
                    @Override public void onSuccess(Boolean data) 
                    {
                        PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW);
                    }
        
                    @Override public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }
}
