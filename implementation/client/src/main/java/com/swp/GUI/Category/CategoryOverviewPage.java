package com.swp.GUI.Category;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.Extras.CategoryList;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.CategoryList.CategoryListCallback;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.GUI.PageManager.PAGES;

public class CategoryOverviewPage extends Page
{
    RenderGUI pCanvas;
    CategoryList pCategoryList;
   
    public CategoryOverviewPage()
    {
        super("Categories");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryoverviewpage.xml"));

        pCanvas = findChildByID("canvas");
        pCategoryList = new CategoryList(new ivec2(0, 0), new ivec2(100, 100), new CategoryListCallback() {
            @Override public void run(Category category) 
            {
                ViewSingleCategoryPage page = (ViewSingleCategoryPage)PageManager.getPage(PAGES.CATEGORY_SINGLEVIEW);
                page.setCategory(category);
                PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW);
            }
        });
        pCategoryList.setSizeInPercent(true, true);
        pCanvas.addGUI(pCategoryList);
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button newButton = (Button)optionsMenu.findChildByID("addcategorybutton");
        newButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  
            {
                ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).editCategory(new Category());
            }
        });

        Searchbar searchbar = new Searchbar(new ivec2(20, 100), new ivec2(40, 30), "Search Category", new String[] {
            "By Content",
        }, new SearchbarCallback() {
            @Override public void run(String query, int option) 
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
    
    public void loadCategories()
    {
        pCategoryList.reset();
        pCategoryList.addCategories(CategoryController.getCategories().stream().toList());
    }
    
    public void loadCategories(String searchterm)
    {
        pCategoryList.reset();
        //pCategoryList.addCategories(CategoryController.getCategoriesBySearchterm(searchterm));
    }

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
