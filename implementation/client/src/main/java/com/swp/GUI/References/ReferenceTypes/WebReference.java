package com.swp.GUI.References.ReferenceTypes;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Display;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.References.ReferenceEntry;

import java.io.IOException;

/**
 * Eine Referenz welche auf eine Website zeigt
 * 
 * @author Tom Beuke
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
            try  {
            if(Display.getOSType() == Display.GUM_OS_WINDOWS)
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            else if(Display.getOSType() == Display.GUM_OS_MAC)
                Runtime.getRuntime().exec("/usr/bin/open " + url);
            else
                Runtime.getRuntime().exec("xdg-open " + url);
            }
            catch (IOException e) {
                NotificationGUI.addNotification("Failed to open Browser", NotificationType.WARNING, 5);
            }
        });
    }
}