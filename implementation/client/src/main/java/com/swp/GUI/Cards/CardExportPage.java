package com.swp.GUI.Cards;

import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.GUI.Page;
import com.swp.Persistence.Exporter.ExportFileType;

public class CardExportPage extends Page
{
    private static Card[] aCards;

    public CardExportPage() 
    {
        super("Export Cards");
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
