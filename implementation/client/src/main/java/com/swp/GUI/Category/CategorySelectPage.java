package com.swp.GUI.Category;

import java.util.ArrayList;
import java.util.List;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.io.Mouse;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.DataModel.Category;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.EditCardPage;
import com.swp.GUI.PageManager.PAGES;

public class CategorySelectPage extends Page 
{
    private final CategoryController categoryController = CategoryController.getInstance();
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

            onHover(null, Mouse.GUM_CURSOR_HAND);
            onClick(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    pSelectSwitch.tick(!pSelectSwitch.isTicked());
                }
            });

            setSizeInPercent(true, false);
            reposition();
        }


        //
        // Getter
        //
        public boolean isSelected()   { return pSelectSwitch.isTicked(); }
        public Category getCategory() { return pCategory; }
    };

    private Scroller pCanvas;

    public CategorySelectPage()
    {
        super("Category Selection", "categoryselectionpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryselectionpage.xml"));

        pCanvas = (Scroller)findChildByID("canvas");
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button doneButton = (Button)optionsMenu.findChildByID("donebutton");
        doneButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
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

        final List<Category> categories = new ArrayList<>();
            categoryController.getCategories(new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
                categories.addAll(data);
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

            @Override
            public void onInfo(String msg) {}
        });

            for(Category category : categories)
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