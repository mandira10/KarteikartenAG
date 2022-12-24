package com.swp.GUI;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.*;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.GUI.Cards.ViewSingleCardPage;
import com.swp.GUI.PageManager.PAGES;

public class KarteikartenAGGUI extends RenderGUI
{
    private Sidebar pSidebar;

    private CategoryPage pCategoryPage;

    public KarteikartenAGGUI()
    {
        PageManager.init(this);

        ViewSingleCardPage page = (ViewSingleCardPage)PageManager.getPage(PAGES.CARD_SINGLEVIEW);
        page.setCard(new TrueFalseCard());
        PageManager.viewPage(PAGES.DECK_OVERVIEW);


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

    @Override
    public void update()
    {
        PageManager.update();
        pSidebar.update();
    }
}