package com.swp.GUI.Category;

import java.util.ArrayList;
import java.util.List;

import com.gumse.gui.Locale;
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
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.Controller.SingleDataCallback;

/**
 * Die Seite welche es einem ermöglicht, Kategorien zu bearbeiten
 */
public class EditCategoryPage extends Page
{
    private Category pNewCategory;
    private TextField pTitleField;
    private boolean bIsNewCategory;
    private boolean nameChange;
    private List<Category> aParents, aChildren;
    private TextBox pCategoriesParentsBox, pCategoriesChildrenBox;

    /**
     * Der Standardkonstruktor der Klasse EditCategoryPage
     */
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

    /**
     * Bearbeitet eine Kategorie anhand ihrer UUID
     *
     * @param uuid Die UUID einer Kategorie
     */
    public void editCategory(String uuid)
    {
        CategoryController.getInstance().getCategoryByUUID(uuid, new SingleDataCallback<Category>() {
            @Override public void onSuccess(Category data) { editCategory(data, false); }
            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }

    /**
     * Übergibt eine zu bearbeitende Kategorie
     *
     * @param category    Eine Kategorie
     * @param newCategory Ist die übergebene Kategorie neu?
     */
    public void editCategory(Category category, boolean newCategory)
    {
        bIsNewCategory = newCategory;
        pNewCategory = new Category(category);

        pTitleField.setString(pNewCategory.getName());

        aChildren = new ArrayList<>();
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

        aParents = new ArrayList<>();
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

    /**
     * Aktualisiert die Unterkategorie-Einträge der zu bearbeitenden Kategorie
     *
     * @param children Die unterkategorien
     */
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


    /**
     * Aktualisiert die Überkategorie-Einträge der zu bearbeitenden Kategorie
     *
     * @param parents Die überkategorien
     */
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
        if(pTitleField.getString().isEmpty())
        {
            NotificationGUI.addNotification(Locale.getCurrentLocale().getString("mandatorytitle"), NotificationType.WARNING, 5);
            return;
        }
        
        CategoryController.getInstance().updateCategoryData(pNewCategory,bIsNewCategory, nameChange, new SingleDataCallback<>() {
            @Override public void onSuccess(Boolean data) 
            {
                CategoryController.getInstance().editCategoryHierarchy(pNewCategory, aParents, aChildren, new SingleDataCallback<>() {
                    @Override public void onSuccess(String data)
                    {
                        if(!data.equals(""))
                            NotificationGUI.addNotification(data, Notification.NotificationType.INFO, 10);

                        if(bIsNewCategory)
                            ((CategoryOverviewPage)PageManager.viewPage(PAGES.CATEGORY_OVERVIEW)).loadCategories();
                        else
                            ((ViewSingleCategoryPage)PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW)).setCategory(pNewCategory);
                    }
        
                    @Override public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR, 5);
                    }
                });
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR, 5);
            }

        });
        nameChange = false;
    }
}
