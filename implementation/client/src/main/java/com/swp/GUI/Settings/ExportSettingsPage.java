package com.swp.GUI.Settings;

import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.Persistence.Exporter.ExportFileType;

public class ExportSettingsPage 
{
    private static Card[] aCards;

    public ExportSettingsPage() 
    {
    }

    public static void setToExport(Card[] cards)
    {
        aCards = cards;
    }

    private void doExport()
    {
        ExportFileType type = ExportFileType.EXPORT_XML;
        CardController.exportCards(aCards, type);
    }
}
