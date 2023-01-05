package com.swp.GUI.Category;

import java.util.ArrayList;
import java.util.List;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Category;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.EditCardPage;
import com.swp.GUI.PageManager.PAGES;

public class CategorySelectPage extends Page 
{
    private class CategoryListEntry extends RenderGUI
    {
        private Category pCategory;
        private TextBox pCardNameBox;
        private Switch pSelectSwitch;

        public CategoryListEntry(Category category, ivec2 pos, ivec2 size)
        {
            this.vSize = size;
            this.vPos = pos;
            this.pCategory = category;
            FontManager fonts = FontManager.getInstance();

            this.pCardNameBox = new TextBox(category.getName(), fonts.getDefaultFont(), new ivec2(0, 0), new ivec2(100, 100));
            pCardNameBox.setSizeInPercent(true, true);
            pCardNameBox.setAlignment(TextBox.Alignment.LEFT);
            pCardNameBox.setTextSize(20);
            pCardNameBox.setTextOffset(new ivec2(-10, 0));
            //pCardNameBox.setCornerRadius(new vec4(cornerradius));
            addElement(pCardNameBox);

            pSelectSwitch = new Switch(new ivec2(100, 10), new ivec2(20, 20), 0.0f);
            pSelectSwitch.setPositionInPercent(true, false);
            pSelectSwitch.setOrigin(new ivec2(30, 0));
            addElement(pSelectSwitch);

            setSizeInPercent(true, false);
            reposition();
        }

        @Override
        public void update()
        {
            if(bIsHidden)
                return;

            updatechildren();
            if(isMouseInside())
            {
                Mouse.setActiveHovering(true);
                Mouse mouse = Window.CurrentlyBoundWindow.getMouse();
                mouse.setCursor(Mouse.GUM_CURSOR_HAND);

                if(isClicked())
                {
                    pSelectSwitch.tick(!pSelectSwitch.isTicked());
                }
            }
        }

        public boolean isSelected()
        {
            return pSelectSwitch.isTicked();
        }

        public Category getCategory()
        {
            return pCategory;
        }
    };

    private Scroller pCanvas;

    public CategorySelectPage()
    {
        super("Category Selection");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryselectionpage.xml"));

        pCanvas = (Scroller)findChildByID("canvas");
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button doneButton = (Button)optionsMenu.findChildByID("donebutton");
        doneButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).updateCategories(getSelection());
            }
        });


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void reset()
    {
        pCanvas.destroyChildren();

        int y = 0;
        for(Category category : CategoryController.getCategories())
        {
            CategoryListEntry container = new CategoryListEntry(category, new ivec2(0, y++ * 50), new ivec2(100, 40));
            pCanvas.addGUI(container);
        }
    }

    private List<Category> getSelection()
    {
        List<Category> retList = new ArrayList<>();

        for(RenderGUI child : pCanvas.getChildren())
        {
            CategoryListEntry entry = (CategoryListEntry)child;
            if(entry.isSelected())
                retList.add(entry.getCategory());
        }

        return retList;
    }
}