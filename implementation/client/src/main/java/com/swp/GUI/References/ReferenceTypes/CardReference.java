package com.swp.GUI.References.ReferenceTypes;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.Controller.CardController;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.ViewSingleCardPage;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.References.ReferenceEntry;

public class CardReference extends ReferenceEntry
{
    public CardReference(ivec2 pos, ivec2 size, String uuid)
    {
        super(pos, size, "Card");

        onClick((RenderGUI gui) -> {
            CardController.getInstance().getCardByUUID(uuid, new SingleDataCallback<Card>() {
                @Override public void onSuccess(Card card) 
                {
                    ((ViewSingleCardPage)PageManager.viewPage(PAGES.CARD_SINGLEVIEW)).setCard(card);
                }

                @Override public void onFailure(String msg) 
                {
                    NotificationGUI.addNotification(msg, NotificationType.WARNING, 5);
                }
            });
        });
    }
}