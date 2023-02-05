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

/**
 * Die Seite auf welcher man eine Liste an Kategorien auswählen kann
 */
public class CategorySelectPage extends Page
{
    /**
     * Gibt die Liste der ausgewählten Kategorien wieder
     */
    public interface CategoryReturnFunc
    {
        /**
         * @param categories Die ausgewählten karten
         */
        void run(List<Category> categories);
    }

    private Scroller pCanvas;
    private boolean bSingleselect;
    private CategoryReturnFunc pReturnFunc;

    private final CategoryController categoryController = CategoryController.getInstance();
    private class CategoryListEntry extends RenderGUI
    {
        private Category pCategory;
        private TextBox pCardNameBox;
        private Switch pSelectSwitch;

        public CategoryListEntry(Category category, ivec2 pos, ivec2 size, boolean selected)
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
            pSelectSwitch.tick(selected);
            addElement(pSelectSwitch);

            onHover(null, Mouse.GUM_CURSOR_HAND);
            onClick((RenderGUI gui) -> {
                pSelectSwitch.tick(!pSelectSwitch.isTicked());

                if(bSingleselect && pReturnFunc != null)
                    pReturnFunc.run(getSelection());
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

    /**
     * Der Standardkonstruktor der Klasse CategorySelectPage
     */
    public CategorySelectPage()
    {
        super("Category Selection", "categoryselectionpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryselectionpage.xml"));

        pCanvas = (Scroller)findChildByID("canvas");
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button doneButton = (Button)optionsMenu.findChildByID("donebutton");
        doneButton.onClick((RenderGUI gui) -> {
            if(pReturnFunc != null)
                pReturnFunc.run(getSelection());
        });


        this.setSizeInPercent(true, true);
        reposition();
    }

    /**
     * Setzt die auswahl zurück und lädt die Kategorien neu
     *
     * @param singleselect Ob nur eine Kategorie zurzeit ausgewählt werden darf
     * @param returnfunc   Die Funktion, für die übergabe der ausgewählten Karten
     * @param selected     Die Liste der bereits ausgewählten Kategorien
     */
    public void reset(boolean singleselect, CategoryReturnFunc returnfunc, List<Category> selected)
    {
        this.bSingleselect = singleselect;
        this.pReturnFunc = returnfunc;

        pCanvas.destroyChildren();

        categoryController.getCategories(new DataCallback<Category>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> categories) 
            {
                int y = 0;
                for(Category category : categories)
                {
                    CategoryListEntry container = new CategoryListEntry(
                        category, 
                        new ivec2(0, y++ * 50), 
                        new ivec2(100, 40), 
                        selected != null && selected.contains(category)
                    );
                    pCanvas.addGUI(container);
                }
            }

            @Override public void onFailure(String msg) 
            {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
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