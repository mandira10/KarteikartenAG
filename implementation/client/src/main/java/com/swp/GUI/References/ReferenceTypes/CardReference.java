package com.swp.GUI.References.ReferenceTypes;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.ViewSingleCardPage;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.References.ReferenceEntry;

/**
 * Eine Referenz welche auf eine andere Karte zeigt
 */
public class CardReference extends ReferenceEntry
{
    /**
     * Der Hauptkonstruktor der Klasse CardReference
     *
     * @param pos  Die Position des GUIs in Pixeln
     * @param size Die Größe des GUIs in Pixeln
     * @param uuid Die UUID der Zielkarte
     */
    public CardReference(ivec2 pos, ivec2 size, String uuid)
    {
        super(pos, size, "Card");

        onClick((RenderGUI gui) -> {
            CardController.getInstance().getCardByUUID(uuid, new SingleDataCallback<Card>() {
                @Override public void onSuccess(Card card) 
                {
                    ((ViewSingleCardPage)PageManager.viewPage(PAGES.CARD_SINGLEVIEW)).setCard(card, PAGES.CARD_OVERVIEW);
                }

                @Override public void onFailure(String msg) 
                {
                    NotificationGUI.addNotification(msg, NotificationType.WARNING, 5);
                }
            });
        });
    }
}