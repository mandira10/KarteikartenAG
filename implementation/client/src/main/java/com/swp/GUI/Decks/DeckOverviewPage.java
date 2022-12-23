package com.swp.GUI.Decks;

import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.PageManager.PAGES;

public class DeckOverviewPage 
{

    

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
