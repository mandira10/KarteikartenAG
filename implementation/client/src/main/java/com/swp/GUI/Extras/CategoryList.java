package com.swp.GUI.Extras;

import java.util.List;

import com.gumse.gui.HierarchyList.HierarchyList;
import com.gumse.gui.HierarchyList.HierarchyListEntry;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Output;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.DataModel.Category;
import com.swp.GUI.PageManager;
import com.swp.GUI.Category.ViewSingleCategoryPage;
import com.swp.GUI.PageManager.PAGES;

public class CategoryList extends RenderGUI
{
    public interface CategoryListCallback
    {
        void run(Category category);
    }

    private HierarchyList pList;

    public CategoryList(ivec2 pos, ivec2 size, CategoryListCallback onclick)
    {
        this.vPos.set(pos);
        this.vSize.set(size);

        pList = new HierarchyList(new ivec2(0, 0), new ivec2(100, 100), "Categories", "Root", false);
        pList.setSizeInPercent(true, true);

        addElement(pList);

        resize();
        reposition();
    }

    
    public void reset()
    {
        pList.reset();
    }

    public void addCategory(Category category, HierarchyListEntry listentry)
    {
        if(listentry == null)
            listentry = pList.getRootEntry();

        final HierarchyListEntry entry = new HierarchyListEntry(category.getName(), pList, (RenderGUI gui) -> {
            ((ViewSingleCategoryPage)PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW)).setCategory(category);
            pList.selectEntry(null);
        });
        entry.onHover(null, Mouse.GUM_CURSOR_HAND);
        CategoryController.getInstance().getChildrenForCategory(category, new DataCallback<Category>() {
            @Override public void onFailure(String msg) {}
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> categories) 
            {
                for(Category cat : categories)
                    addCategory(cat, entry);
            }
        });

        listentry.addEntry(entry);
    }
}
