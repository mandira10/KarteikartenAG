package com.swp.GUI.References.ReferenceTypes;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Display;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.References.ReferenceEntry;

import java.io.IOException;

/**
 * Eine Referenz welche auf eine Website zeigt
 */
public class WebReference extends ReferenceEntry
{
    /**
     * Der Hauptkonstruktor der Klasse WebReference
     *
     * @param pos  Die Position des GUIs in Pixeln
     * @param size Die Größe des GUIs in Pixeln
     * @param url  Die URL der Website
     */
    public WebReference(ivec2 pos, ivec2 size, String url)
    {
        super(pos, size, "Web");

        onClick((RenderGUI gui) -> {
            if(Display.getOSType() == Display.GUM_OS_WINDOWS)
            {
                try { Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url); } 
                catch (IOException e) {
                    NotificationGUI.addNotification("Failed to open Browser", NotificationType.WARNING, 5);
                }
            }
            else
            {
                try  { Runtime.getRuntime().exec("xdg-open " + url); } 
                catch (IOException e) {
                    NotificationGUI.addNotification("Failed to open Browser", NotificationType.WARNING, 5);
                }
            }
        });
    }
}