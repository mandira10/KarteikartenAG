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
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.PageManager.PAGES;

public class ViewSingleCategoryPage extends Page
{
    private Category pCategory;
    private RenderGUI pCanvas;

    public ViewSingleCategoryPage()
    {
        super("View Category");
        this.vSize = new ivec2(100, 100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryviewpage.xml"));
        pCanvas = findChildByID("canvas");

        RenderGUI optionsMenu = findChildByID("menu");
        Button editButton = (Button)optionsMenu.findChildByID("editcategorybutton");
        editButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).editCategory(pCategory);
            }
        });


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setCategory(Category category)
    {
        this.pCategory = category;
        pCanvas.destroyChildren();

        CardList cardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), CategoryController.getCardsInCategory(this.pCategory));
        cardList.setSizeInPercent(true, true);
        pCanvas.addGUI(cardList);

        resize();
        reposition();
    }
}
