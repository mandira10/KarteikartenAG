package com.swp.GUI;

import com.gumse.gui.Primitives.RenderGUI;

public class KarteikartenAGGUI extends RenderGUI
{
    private LoginPage pLoginPage;
    private DeckPage pDeckPage;
    private SettingsPage pSettingsPage;
    private CategoryPage pCategoryPage;
    private CardPage pCardPage;

    public KarteikartenAGGUI()
    {
        pLoginPage = new LoginPage();
        addElement(pLoginPage);
    }
}