package com.swp.GUI;

import com.gumse.basics.SmoothFloat;
import com.gumse.gui.GUI;
import com.gumse.gui.Locale;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.*;
import com.gumse.system.io.Mouse;
import com.swp.DataModel.User;
import com.swp.GUI.Cards.CardOverviewPage;
import com.swp.GUI.Category.CategoryOverviewPage;
import com.swp.GUI.Decks.DeckOverviewPage;
import com.swp.GUI.PageManager.PAGES;

/**
 * Die Sidebar wird einmalig erstellt und
 * ist zu jederzeit in Programm an der
 * Seite zu sehen
 */
public class Sidebar extends RenderGUI
{
    private class SidebarItem extends RenderGUI
    {
        private final TextBox pSymbol;
        private final Text pTitle;
        private final SmoothFloat pColorSmoothFloat;

        public SidebarItem(char symbol, String title, ivec2 pos, GUICallback callback)
        {
            this.sType = "SidebarItem";
            this.vPos.set(pos);
            this.vSize.set(new ivec2(100, 100));
            this.setSizeInPercent(true, false);

            Font pFontAwesome = FontManager.getInstance().getFont("FontAwesome");
            pSymbol = new TextBox(Character.toString(symbol), pFontAwesome, new ivec2(0, 0), new ivec2(100, 100));
            pSymbol.setTextSize(60);
            pSymbol.setAlignment(TextBox.Alignment.CENTER);
            pSymbol.getBox().hide(true);
            addElement(pSymbol);

            pTitle = new Text(Locale.getCurrentLocale().getString(title), FontManager.getInstance().getDefaultFont(), new ivec2(120, 50 - 15), 0);
            pTitle.setLocaleID(title);
            pTitle.setCharacterHeight(30);
            pTitle.hide(true);
            addElement(pTitle);

            pColorSmoothFloat = new SmoothFloat(0, 30, 0);

            onClick(callback);

            onHover(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    pColorSmoothFloat.setTarget(1.0f);
                }
            }, Mouse.GUM_CURSOR_HAND);

            onLeave(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    pColorSmoothFloat.setTarget(0.0f);
                }
            });

            reposition();
            resize();
        }

        @Override
        public void updateextra()
        {
            if(pColorSmoothFloat.update())
            {
                vec4 color = vec4.mix(GUI.getTheme().textColor, GUI.getTheme().accentColor, pColorSmoothFloat.get());

                pSymbol.setTextColor(color);
                pTitle.setColor(color);
            }
        }

        public void setVisibility(float v, int boxsize)
        {
            if(v > 0.1f)
            {
                pTitle.hide(false);
                pTitle.setRenderBox(new bbox2i(pTitle.getPosition(), new ivec2(boxsize, 100)));
            }
            else
            {
                pTitle.hide(true);
            }
        }

        @Override
        protected void updateOnThemeChange() 
        {
            pSymbol.setTextColor(GUI.getTheme().textColor);
            pTitle.setColor(GUI.getTheme().textColor);
        }
    }


    private final Box pBackground;

    private final SmoothFloat pSmoothFloat;

    /**
     * Der Standardkonstruktor der Klasse Sidebar
     */
    public Sidebar()
    {
        pBackground = new Box(new ivec2(0,0), new ivec2(100, 100));
        pBackground.setSizeInPercent(false, true);
        pBackground.setColor(GUI.getTheme().secondaryColor);
        pBackground.setCornerRadius(new vec4(0,0,0,0));
        addElement(pBackground);

        pSmoothFloat = new SmoothFloat(0, 5, 0);

        refresh();


        pBackground.onEnter(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                pSmoothFloat.setTarget(1.0f);
            }
        });

        pBackground.onLeave(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                pSmoothFloat.setTarget(0.0f);
            }
        });

        resize();
        reposition();
    }

    /**
     * Lädt die Sidebar elemente neu
     */
    public void refresh()
    {
        pBackground.destroyChildren();
        
        int offset = -100;
        if(!User.isLoggedIn())
        {
            pBackground.addGUI(new SidebarItem('', "sidebarlogin", new ivec2(0,offset += 110), (RenderGUI gui) -> {
                PageManager.viewPage(PAGES.LOGIN);
            }));
        }
        else
        {
            pBackground.addGUI(new SidebarItem('', "sidebardecks", new ivec2(0,offset += 110), (RenderGUI gui) -> {
                ((DeckOverviewPage)PageManager.viewPage(PAGES.DECK_OVERVIEW)).loadDecks();
            }));
            pBackground.addGUI(new SidebarItem('', "sidebarcards", new ivec2(0,offset += 110), (RenderGUI gui) -> {
                ((CardOverviewPage)PageManager.viewPage(PAGES.CARD_OVERVIEW)).loadCards(0, 30);
            }));
            pBackground.addGUI(new SidebarItem('', "sidebarcategories", new ivec2(0,offset += 110), (RenderGUI gui) -> {
                ((CategoryOverviewPage)PageManager.viewPage(PAGES.CATEGORY_OVERVIEW)).loadCategories();
            }));
        }


        SidebarItem settingsItem = new SidebarItem('', "sidebarsettings", new ivec2(0, 100), (RenderGUI gui) -> {
            PageManager.viewPage(PAGES.SETTINGS);
        });
        settingsItem.setPositionInPercent(false, true);
        settingsItem.setOrigin(new ivec2(0, 100));
        pBackground.addGUI(settingsItem);
    }
    

    @Override
    public void updateextra()
    {
        if(pSmoothFloat.update())
        {
            int boxSize = (int)(200 * pSmoothFloat.get());
            pBackground.setSize(new ivec2(100 + boxSize, 100));
            for(int i = 0; i < pBackground.numChildren(); i++)
            {
                ((SidebarItem)pBackground.getChild(i)).setVisibility(pSmoothFloat.get(), boxSize);
            }
        }
    }

    @Override
    protected void updateOnThemeChange() 
    {
        pBackground.setColor(GUI.getTheme().secondaryColor);
    }
}
