package com.swp.GUI.Category;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.GUI.PageManager.PAGES;

public class CategoryOverviewPage extends Page
{
    private class CategoryContainer extends Box
    {
        Category pCategory;

        public CategoryContainer(Category category)
        {
            super(new ivec2(), new ivec2());
            pCategory = category;

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
        }

        @Override
        public void update() 
        {
            if(isMouseInside())
            {
                Mouse.setActiveHovering(true);
                Window.CurrentlyBoundWindow.getMouse().setCursor(Mouse.GUM_CURSOR_HAND);

                if(isClicked())
                {
                    ViewSingleCategoryPage page = (ViewSingleCategoryPage)PageManager.getPage(PAGES.CATEGORY_SINGLEVIEW);
                    page.setCategory(pCategory);
                    PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW);
                }
            }
        }
    };

    
    public CategoryOverviewPage()
    {
        super("Categories");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryoverviewpage.xml"));

        Scroller canvas = (Scroller)findChildByID("canvas");

        int y = 0;
        for(Category category : CategoryController.getCategories())
        {
            CategoryContainer container = new CategoryContainer(category);
            container.setPosition(new ivec2(5, y++ * 110));
            container.setSize(new ivec2(90, 100));
            container.setColor(new vec4(0.18f, 0.19f, 0.2f, 1.0f));
            container.setSizeInPercent(true, false);
            container.setPositionInPercent(true, false);
            container.setCornerRadius(new vec4(7.0f));

            canvas.addGUI(container);
        }
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button newButton = (Button)optionsMenu.findChildByID("addcategorybutton");
        newButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).editCategory(new Category());
            }
        });

        Searchbar searchbar = new Searchbar(new ivec2(20, 100), new ivec2(40, 30), "Search Category", new SearchbarCallback() {
            @Override public void run(String query) 
            {
                //TODO search
            }
        });
        searchbar.setPositionInPercent(false, true);
        searchbar.setSizeInPercent(true, false);
        searchbar.setOrigin(new ivec2(0, 50));
        addGUI(searchbar);

        this.setSizeInPercent(true, true);
        reposition();
    }

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
