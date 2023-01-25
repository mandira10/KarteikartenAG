package com.swp.GUI.References.ReferenceTypes;

import java.io.IOException;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Display;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.References.ReferenceEntry;

public class FileReference extends ReferenceEntry
{
    public FileReference(ivec2 pos, ivec2 size, String path)
    {
        super(pos, size, "File");
        
        onClick((RenderGUI gui) -> {
            try 
            {
                if(Display.getOSType() == Display.GUM_OS_WINDOWS)
                    Runtime.getRuntime().exec("explorer " + path.replace("/", "\\\\"));
                else
                    Runtime.getRuntime().exec("xdg-open " + path);

                //Mac specific:
                //Runtime.getRuntime().exec(new String[]{"/usr/bin/open", file.getAbsolutePath()});
            } 
            catch (IOException e) 
            {   
                NotificationGUI.addNotification("Failed to open file explorer", NotificationType.WARNING, 5);
            }
        });
    }
}