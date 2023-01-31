package com.swp.GUI.Extras;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

import java.io.File;

public class FileDialog 
{
    /**
     * Öffnet den Dateidialog des jeweiligen Betriebssystems,
     * um eine Datei zu öffnen
     * @param title        Der titel des Dialogs
     * @param description  Die beschreibung des Dialogs
     * @param allowedTypes Eine liste an erlaubten Dateiendungen
     */
    public static String openFile(String title, String description, String[] allowedTypes)
    {
        MemoryStack stack = MemoryStack.stackPush();
        PointerBuffer filters = stack.mallocPointer(1 + allowedTypes.length);

        filters.put(stack.UTF8("*." + "txt"));
        for(String type : allowedTypes)
            filters.put(stack.UTF8("*." + type));
        filters.flip();

        File defaultPath = new File(".");
        defaultPath = defaultPath.getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator))
            defaultString += File.separator;
       
        String result = tinyfd_openFileDialog(title, defaultString, filters, description, false);

        stack.pop();
        return result;
    }


    /**
     * Öffnet den Dateidialog des jeweiligen Betriebssystems,
     * um eine Datei zu speichern
     * @param title        Der titel des Dialogs
     * @param description  Die beschreibung des Dialogs
     * @param allowedTypes Eine liste an erlaubten Dateiendungen
     */
    public static String saveFile(String title, String description, String[] allowedTypes)
    {
        MemoryStack stack = MemoryStack.stackPush();
        PointerBuffer filters = stack.mallocPointer(1 + allowedTypes.length);

        filters.put(stack.UTF8("*." + "txt"));
        for(String type : allowedTypes)
            filters.put(stack.UTF8("*." + type));
        filters.flip();

        File defaultPath = new File(".");
        defaultPath = defaultPath.getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator))
            defaultString += File.separator;
       
        String result = tinyfd_saveFileDialog(title, defaultString, filters, description);

        stack.pop();
        return result;
    }
}
