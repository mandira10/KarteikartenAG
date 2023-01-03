package com.swp.GUI;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.*;
import com.gumse.system.Window;
import com.gumse.system.io.Keyboard;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.GUI.Cards.EditCardPage;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;

public class KarteikartenAGGUI extends RenderGUI
{
    private Sidebar pSidebar;
    private NotificationGUI pNotifications;

    private CategoryPage pCategoryPage;

    public KarteikartenAGGUI()
    {
        PageManager.init(this);

        EditCardPage page = (EditCardPage)PageManager.getPage(PAGES.CARD_EDIT);
        //page.setCard(new TrueFalseCard());
        page.editCard(new MultipleChoiceCard());
        PageManager.viewPage(PAGES.CARD_EDIT);

        pNotifications = NotificationGUI.getInstance();
        addElement(pNotifications);


        //NotificationGUI.addNotification("Info Message with a little more text test test test test test test test", NotificationType.INFO, 5);
        //NotificationGUI.addNotification("Warning Message", NotificationType.WARNING, 10);
        //NotificationGUI.addNotification("Error Message", NotificationType.ERROR, 5);
        //NotificationGUI.addNotification("Critical Error Message", NotificationType.CRITICAL, 5);
        //NotificationGUI.addNotification("Debug Message", NotificationType.DEBUG, 3);
        //NotificationGUI.addNotification("Some Connection Message/Error", NotificationType.CONNECTION, 3);

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
        pNotifications.render();
    }

    @Override
    public void update()
    {
        pNotifications.update();
        pSidebar.update();
        PageManager.update();
    }
}