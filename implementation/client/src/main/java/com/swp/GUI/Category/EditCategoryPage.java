package com.swp.GUI.Category;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;

public class EditCategoryPage extends Page
{
    private Button pApplyButton;
    private RenderGUI pCanvas;
    private Category pOldCategory, pNewCategory;

    public EditCategoryPage()
    {
        super("Edit Category");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/categories/categoryeditpage.xml"));

        pCanvas = findChildByID("canvas");

        
        RenderGUI optionsMenu = findChildByID("menu");
        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                PageManager.viewLastPage();
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void editCategory(String uuid) { editCategory(CategoryController.getCategoryByUUID(uuid)); }
    public void editCategory(Category category)
    {
        if(category == null)
        {

        }
        else
        {

        }
        pOldCategory = category;
        //pNewCategory = Category.copyCategory(category);
    }

    private void deleteCategory()
    {
        CategoryController.deleteCategory(pOldCategory);
    }

    private void applyChanges()
    {
        CategoryController.updateCategoryData(pOldCategory, pNewCategory);
    }
}
