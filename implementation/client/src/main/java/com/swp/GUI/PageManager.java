package com.swp.GUI;

import java.util.ArrayList;

import com.gumse.gui.Primitives.RenderGUI;

public class PageManager
{
    private static ArrayList<RenderGUI> vPages = null;

    private PageManager() {}

    public static void init()
    {
        if(vPages == null)
        {
            vPages = new ArrayList<>();
        }
    }


    public static void addPage(RenderGUI page)
    {
        vPages.add(page);
    }

    public static void viewPage(RenderGUI page)
    {
        if(!vPages.contains(page))
            return;

        for(RenderGUI p : vPages)
            p.hide(true);
        page.hide(false);
    }
}