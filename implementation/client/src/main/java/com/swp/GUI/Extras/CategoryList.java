package com.swp.GUI.Extras;

import java.util.List;

import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Output;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;

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
    };

    private Scroller pScroller;
    private CategoryListCallback pCallback;

    public CategoryList(ivec2 pos, ivec2 size, CategoryListCallback onclick)
    {
        this.vPos.set(pos);
        this.vSize.set(size);
        this.pCallback = onclick;

        pScroller = new Scroller(new ivec2(0, 0), new ivec2(100, 100));
        pScroller.setSizeInPercent(true, true);
        addElement(pScroller);

        resize();
        reposition();
    }

    
    public void reset()
    {
        pScroller.destroyChildren();
    }

    public void addCategories(List<Category> categories)
    {
        int y = 0;
        for(Category category : categories)
        {
            CategoryContainer container = new CategoryContainer(category);
            container.setPosition(new ivec2(5, y++ * 110));
            container.setSize(new ivec2(90, 100));
            container.setColor(new vec4(0.18f, 0.19f, 0.2f, 1.0f));
            container.setSizeInPercent(true, false);
            container.setPositionInPercent(true, false);
            container.setCornerRadius(new vec4(7.0f));

            pScroller.addGUI(container);
        }
    }
}
