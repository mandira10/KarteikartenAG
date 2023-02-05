package com.swp.GUI.References;

import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;

/**
 * Das ReferenceGUI zeigt einem alle Referenzen,
 * welche eine Karte zu externen Orten hat an
 * 
 * @author Tom Beuke
 */
public class ReferencesGUI extends RenderGUI
{
    private static final int iGapSize = 20;
    
    private final Scroller pContent;

    /**
     * @param pos  Position des GUIs in Pixeln
     * @param size Größe des GUIs in Pixeln
     */
    public ReferencesGUI(ivec2 pos, ivec2 size)
    {
        this.vSize.set(size);
        this.vPos.set(pos);

        pContent = new Scroller(new ivec2(30, 30), new ivec2(100, 300));
        pContent.setMargin(new ivec2(-60, -60));
        pContent.setSizeInPercent(true, false);
        addElement(pContent);

        reposition();
        resize();
    }


    @Override
    protected void updateOnSizeChange()
    {
        if(pContent == null)
            return;
        
        int maxheight = 0;
        for(RenderGUI child : pContent.getChildren())
        {
            //child.resize();
            child.setPosition(new ivec2(0, maxheight));
            maxheight += child.getSize().y + iGapSize;
        }

        //this.vSize.y = maxheight;
    }

    /**
     * Fügt eine Referenz zur Liste hinzu
     *
     * @param entry Ein Referenzeintrag
     */
    public void addReference(ReferenceEntry entry)
    {
        pContent.addGUI(entry);
    }

    /**
     * Interpretiert einen Referenzstring zu mehreren Referenzobjekten
     *
     * @param str Der Referenzstring aus der Datenbank
     */
    public void interpreteString(String str)
    {
        pContent.destroyChildren();


        if(!str.isBlank())
        {
            str.lines().forEach((String line) -> {
                if(line.isBlank())
                    return;
                    
                ReferenceEntry entry = ReferenceEntry.createFromString(line, new ivec2(0, pContent.numChildren() * (30 + iGapSize)), new ivec2(100, 30));
                if(entry != null)
                {
                    entry.setSizeInPercent(true, false);
                    addReference(entry);
                }
            });
        }
    }
}