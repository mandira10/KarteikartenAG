package com.swp.GUI.Decks;

import com.swp.Controller.CardController;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;
import com.swp.GUI.PageManager;
import com.swp.GUI.SettingsPage;
import com.swp.GUI.Settings.ExportSettingsPage;

public class DeckOverviewPage 
{

    

    private void exportCards()
    {
        ExportSettingsPage.setToExport(null);
        PageManager.viewPage("ExportSettingsPage");
    }
}
