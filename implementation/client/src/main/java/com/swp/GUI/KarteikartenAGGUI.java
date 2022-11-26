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
    private RenderGUI pPageCanvas;

    private LoginPage pLoginPage;
    private DeckPage pDeckPage;
    private SettingsPage pSettingsPage;
    private CategoryPage pCategoryPage;
    private CardPage pCardPage;

    public KarteikartenAGGUI()
    {
        pPageCanvas = new RenderGUI();
        pPageCanvas.setPosition(new ivec2(100, 0));
        pPageCanvas.setSize(new ivec2(100, 100));
        pPageCanvas.setMargin(new ivec2(-60, 0));
        pPageCanvas.setSizeInPercent(true, true);
        addElement(pPageCanvas);

        pLoginPage = new LoginPage();
        pPageCanvas.addGUI(pLoginPage);

        pSidebar = new Sidebar();
        pSidebar.setSize(new ivec2(60, 100));
        pSidebar.setSizeInPercent(false, true);
        addElement(pSidebar);
    }
}