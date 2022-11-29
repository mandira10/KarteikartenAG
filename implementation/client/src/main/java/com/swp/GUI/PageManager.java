package com.swp.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;

public class PageManager
{
    private static Map<String, RenderGUI> mPages = null;
    private static RenderGUI pActivePage = null;
    private static RenderGUI pPageCanvas;

    private PageManager() {}

    public static void init(RenderGUI parent)
    {
        if(mPages == null)
        {
            mPages = new HashMap<>();
        }
        pPageCanvas = new RenderGUI();
        pPageCanvas.setPosition(new ivec2(100, 0));
        pPageCanvas.setSize(new ivec2(100, 100));
        pPageCanvas.setMargin(new ivec2(-60, 0));
        pPageCanvas.setSizeInPercent(true, true);

        parent.addGUI(pPageCanvas);
    }


    public static void addPage(String name, RenderGUI page)
    {
        mPages.put(name, page);
        pPageCanvas.addGUI(page);
        if(pActivePage == null)
            pActivePage = page;
    }

    public static void viewPage(RenderGUI page)
    {
        if(!mPages.containsValue(page))
            return;
        pActivePage = page;
    }

    public static void viewPage(String name)
    {
        if(!mPages.containsKey(name))
            return;

        pActivePage = mPages.get(name);
    }

    public static void render()
    {
        pActivePage.render();
    }

    public static void update()
    {
        pActivePage.update();
    }
}