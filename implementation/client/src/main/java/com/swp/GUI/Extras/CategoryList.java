package com.swp.GUI.Extras;

import java.util.List;

import com.gumse.gui.HierarchyList.HierarchyList;
import com.gumse.gui.HierarchyList.HierarchyListEntry;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.io.Mouse;
import com.swp.Controller.CategoryController;
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

    public void addCategories(List<Category> categories)
    {
        for(Category category : categories)
        {
            HierarchyListEntry entry = new HierarchyListEntry(category.getName(), pList, (RenderGUI gui) -> {
                ((ViewSingleCategoryPage)PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW)).setCategory(category);
                pList.selectEntry(null);
            });
            entry.onHover(null, Mouse.GUM_CURSOR_HAND);
            pList.addEntry(entry);
        }
    }
}
