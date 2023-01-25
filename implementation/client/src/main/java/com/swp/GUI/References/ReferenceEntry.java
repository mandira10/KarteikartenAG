package com.swp.GUI.References;

import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.FontManager;
import com.gumse.maths.ivec2;
import com.gumse.system.io.Mouse;
import com.swp.GUI.References.ReferenceTypes.CardReference;
import com.swp.GUI.References.ReferenceTypes.CategoryReference;
import com.swp.GUI.References.ReferenceTypes.FileReference;
import com.swp.GUI.References.ReferenceTypes.WebReference;

public abstract class ReferenceEntry extends TextBox
{
    public ReferenceEntry(ivec2 pos, ivec2 size, String name) 
    {
        super(name, FontManager.getInstance().getDefaultFont(), pos, size);

        setTextSize(25);
        setAutoInsertLinebreaks(true);
        getBox().hide(true);
        setAlignment(Alignment.LEFT);
        onHover(null, Mouse.GUM_CURSOR_HAND);

        reposition();
        resize();
    }

    public static ReferenceEntry createFromString(String str, ivec2 pos, ivec2 size)
    {
        String[] args = str.split(";");
        if(args.length < 3)
            return null;

        ReferenceEntry retReference = null;
        
        String type = args[0].replaceAll("\\s", "");
        String destination = args[1];
        String description = args[2];
        String destDescription = "";
        switch(type)
        {
            case "ctg": retReference = new CategoryReference(pos, size, destination); destDescription = "Category"; break;
            case "crd": retReference = new CardReference(pos, size, destination);     destDescription = "Card"; break;
            case "htm": retReference = new WebReference(pos, size, destination);      destDescription = "Website"; break;
            case "fil": retReference = new FileReference(pos, size, destination);     destDescription = "File"; break;
        }

        if(retReference != null)
            retReference.setString("[" + destDescription + "] " + description);

        return retReference;
    }
}
