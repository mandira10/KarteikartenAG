package com.swp.GUI.Extras;

import java.util.List;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.HierarchyList.HierarchyList;
import com.gumse.gui.HierarchyList.HierarchyListEntry;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Output;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.PageManager;
import com.swp.GUI.Category.ViewSingleCategoryPage;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;

public class CategoryList extends RenderGUI
{
    public interface CategoryListCallback
    {
        void run(Category category);
    }

    private class CategoryContainer extends Box
    {
        public CategoryContainer(Category category)
        {
            super(new ivec2(), new ivec2());
            setColor(GUI.getTheme().secondaryColor);
            
            Font defaultFont = FontManager.getInstance().getDefaultFont();
            Font fontAwesome = FontManager.getInstance().getFont("FontAwesome");

            Text categoryName = new Text(category.getName(), defaultFont, new ivec2(10,10), 0);
            categoryName.setCharacterHeight(35);  
            addElement(categoryName);

            Text numcardsText = new Text("", defaultFont, new ivec2(10, 50), 0);
            numcardsText.setCharacterHeight(35);

            Text iconText = new Text("", fontAwesome, new ivec2(100, 100), 0);
            iconText.setPositionInPercent(true, true);
            iconText.setOrigin(new ivec2(45, 40));
            iconText.setCharacterHeight(30);
            iconText.setColor(new vec4(0.13f, 0.13f, 0.14f, 1));

            int numCards = CategoryController.numCardsInCategory(category);
            numcardsText.setString("Cards: " + numCards);
            iconText.setString("");
            addElement(numcardsText);
            addElement(iconText);

            onHover(null, Mouse.GUM_CURSOR_HAND);
            onClick(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    pCallback.run(category);
                }
            });
        }

        @Override
        protected void updateOnThemeChange() 
        {
            setColor(GUI.getTheme().secondaryColor);
        }
    };

    private HierarchyList pList;
    private CategoryListCallback pCallback;

    public CategoryList(ivec2 pos, ivec2 size, CategoryListCallback onclick)
    {
        this.vPos.set(pos);
        this.vSize.set(size);
        this.pCallback = onclick;

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
            Output.info(category.getName());
            HierarchyListEntry entry = new HierarchyListEntry(category.getName(), pList, (RenderGUI gui) -> {
                ((ViewSingleCategoryPage)PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW)).setCategory(category);
                pList.selectEntry(null);
            });
            entry.onHover(null, Mouse.GUM_CURSOR_HAND);
            pList.addEntry(entry);
        }
    }
}
