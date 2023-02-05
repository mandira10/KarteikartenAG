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

/**
 * Eine Referenz welche auf eine Kategorie zeigt
 * 
 * @author Tom Beuke
 */
public class CategoryReference extends ReferenceEntry
{
    /**
     * Der Hauptkonstruktor der Klasse CategoryReference
     *
     * @param pos  Die Position des GUIs in Pixeln
     * @param size Die Größe des GUIs in Pixeln
     * @param uuid Die UUID der Zielkategorie
     */
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