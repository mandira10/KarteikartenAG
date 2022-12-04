package com.swp.GUI.Category;

import com.gumse.gui.Basics.Button;
import com.swp.Controller.CategoryController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.DataModel.Category;

import java.util.List;

public class EditCategoryPage 
{
    private Button pApplyButton;
    private Category pOldCategory, pNewCategory;

    public EditCategoryPage()
    {

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
