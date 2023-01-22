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
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.Controller.SingleDataCallback;

public class EditCategoryPage extends Page
{
    private Button pApplyButton;
    private RenderGUI pCanvas;
    private Category pOldCategory, pNewCategory;
    private boolean bIsNewCategory;
    private boolean nameChange;

    private TextField pTitleField;

    private CategoryController categoryController = CategoryController.getInstance();

    public EditCategoryPage()
    {
        super("Edit Category", "editcategorypage");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/categories/categoryeditpage.xml"));

        pCanvas = findChildByID("canvas");

        pTitleField = (TextField)findChildByID("titlefield");
        pTitleField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) 
            {
                pNewCategory.setName(complete);
                nameChange = true;
            }
            @Override public void input(String input, String complete) {}
        });

        
        RenderGUI optionsMenu = findChildByID("menu");
        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                PageManager.viewLastPage();
            }
        });

        Button applyButton = (Button)optionsMenu.findChildByID("applybutton");
        applyButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                applyChanges();
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void editCategory(String uuid) {
        categoryController.getCategoryByUUID(uuid, new SingleDataCallback<Category>() {
            @Override
            public void onSuccess(Category data) {
                editCategory(data,false);
            }

            @Override
            public void onFailure(String msg) {
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
        categoryController.updateCategoryData(pNewCategory,bIsNewCategory, nameChange, new SingleDataCallback<>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

        });
    }
}
