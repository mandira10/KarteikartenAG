package com.swp.GUI.Cards;

import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.Persistence.Exporter.ExportFileType;
import com.swp.Controller.SingleDataCallback;

public class CardExportPage extends Page
{
    private static Card[] aCards;

    public CardExportPage() 
    {
        super("Export Cards", "exportcardspage");
    }

    public static void setToExport(Card[] cards)
    {
        aCards = cards;
    }

    private void doExport()
    {
        ExportFileType type = ExportFileType.EXPORT_XML;
        CardController.getInstance().exportCards(aCards, type, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }
}
