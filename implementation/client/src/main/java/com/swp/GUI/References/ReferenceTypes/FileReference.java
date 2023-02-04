package com.swp.GUI.References.ReferenceTypes;

import java.io.IOException;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Display;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.References.ReferenceEntry;

/**
 * Eine Referenz welche auf eine Datei zeigt
 */
public class FileReference extends ReferenceEntry
{
    /**
     * Der Hauptkonstruktor der Klasse FileReference
     *
     * @param pos  Die Position des GUIs in Pixeln
     * @param size Die Größe des GUIs in Pixeln
     * @param path Der Dateipfad
     */
    public FileReference(ivec2 pos, ivec2 size, String path)
    {
        super(pos, size, "File");
        
        onClick((RenderGUI gui) -> {
            try 
            {
                if(Display.getOSType() == Display.GUM_OS_WINDOWS)
                    Runtime.getRuntime().exec("explorer " + path.replace("/", "\\"));
                else if(Display.getOSType() == Display.GUM_OS_MAC)
                    Runtime.getRuntime().exec("/usr/bin/open " + path);
                else
                    Runtime.getRuntime().exec("xdg-open " + path);                
            } 
            catch (IOException e) 
            {   
                NotificationGUI.addNotification("Failed to open file explorer", NotificationType.WARNING, 5);
            }
        });
    }
}