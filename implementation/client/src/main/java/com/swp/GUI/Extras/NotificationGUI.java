package com.swp.GUI.Extras;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.swp.GUI.Extras.Notification.NotificationType;

/**
 * NotificationGUI zeigt Benachrichtigungen, an welche
 * Global zu jeder Zeit hinzugefügt werden können.
 * Es darf nur eine Instanz existieren
 */
public class NotificationGUI extends RenderGUI
{
    private static NotificationGUI pInstance;
    private static final int iGapSize = 10;

    private NotificationGUI()
    {
        this.vSize = new ivec2(100, 100);

        setSizeInPercent(true, true);
        reposition();
    }

    public static NotificationGUI getInstance()
    {
        if(pInstance == null)
            pInstance = new NotificationGUI();

        return pInstance;
    }

    @Override
    public void updateextra()
    {
        for(int i = 0; i < numChildren(); i++)
        {
            RenderGUI entry = getChild(i);
            //entry.update();

            Notification notification = (Notification)entry;
            if(notification.canBeRemoved())
            {
                removeChild(i);
                //Move everyone up one spot
                if(numChildren() >= i)
                {
                    for(int j = i; j < numChildren(); j++)
                    {
                        int ypos = 20;
                        if(j > 0)
                        {
                            RenderGUI lastEntry = getChild(j - 1);
                            ypos = lastEntry.getPosition().y + lastEntry.getSize().y + iGapSize;
                        }

                        RenderGUI nextentry = getChild(j);
                        nextentry.setPosition(new ivec2(100, ypos));
                    }
                }
            }
        }
    }

    public static void addNotification(String str, NotificationType type, float seconds)
    {
        NotificationGUI notificationgui = getInstance();

        int yoffset = 20;
        if(notificationgui.numChildren() > 0)
            yoffset = notificationgui.getChild(notificationgui.numChildren() - 1).getPosition().y
                    + notificationgui.getChild(notificationgui.numChildren() - 1).getSize().y;

        Notification entry = new Notification(str, new ivec2(100, yoffset + iGapSize), type, 1.0f / seconds);
        entry.setPositionInPercent(true, false);
        entry.setOrigin(new ivec2(entry.getSize().x + 20, 0));
        

        notificationgui.addGUI(entry);
    }
}