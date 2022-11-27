package com.swp.GUI;

import com.gumse.basics.SmoothFloat;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.*;

public class KarteikartenAGGUI extends RenderGUI
{
    private Sidebar pSidebar;

    private LoginPage pLoginPage;
    private DeckPage pDeckPage;
    private SettingsPage pSettingsPage;
    private CategoryPage pCategoryPage;
    private CardPages pCardPages;

    public KarteikartenAGGUI()
    {
        PageManager.init(this);

        PageManager.addPage("LoginPage", new LoginPage());
        pCardPages = new CardPages();



        pSidebar = new Sidebar();
        pSidebar.setSize(new ivec2(60, 100));
        pSidebar.setSizeInPercent(false, true);
        addElement(pSidebar);
    }

    @Override
    public void render()
    {
        PageManager.render();
        pSidebar.render();
    }
}