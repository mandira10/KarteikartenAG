package com.swp.GUI.Category;

import java.io.ObjectOutputStream.PutField;
import java.util.List;

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
import com.swp.Persistence.DataCallback;
import com.swp.Persistence.SingleDataCallback;

public class EditCategoryPage extends Page
{
    private Button pApplyButton;
    private RenderGUI pCanvas;
    private Category pOldCategory, pNewCategory;
    private TextField pTitleField;

    public EditCategoryPage()
    {
        super("Edit Category");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/categories/categoryeditpage.xml"));

        pCanvas = findChildByID("canvas");

        pTitleField = (TextField)findChildByID("titlefield");
        pTitleField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) 
            {
                pNewCategory.setName(complete);
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

    public void editCategory(String uuid) { editCategory(CategoryController.getCategoryByUUID(uuid)); }
    public void editCategory(Category category)
    {
        pNewCategory = new Category(category);

        pTitleField.setString(pNewCategory.getName());
    }

    private void applyChanges()
    {
        boolean neu = true;
        CategoryController.updateCategoryData(pNewCategory,neu, new SingleDataCallback<>() {
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
