package com.swp.GUI.References.ReferenceTypes;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CategoryController;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Category;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Category.ViewSingleCategoryPage;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.References.ReferenceEntry;

public class CategoryReference extends ReferenceEntry
{
    public CategoryReference(ivec2 pos, ivec2 size, String uuid)
    {
        super(pos, size, "Category");

        onClick((RenderGUI gui) -> {
            CategoryController.getInstance().getCategoryByUUID(uuid, new SingleDataCallback<Category>() {
                @Override public void onSuccess(Category category) 
                {
                    ((ViewSingleCategoryPage)PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW)).setCategory(category);
                }

                @Override public void onFailure(String msg) 
                {
                    NotificationGUI.addNotification(msg, NotificationType.WARNING, 5);
                }
            });
        });
    }
}