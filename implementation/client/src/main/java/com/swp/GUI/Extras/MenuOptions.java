package com.swp.GUI.Extras;

import java.util.ArrayList;
import java.util.List;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Speechbubble;
import com.gumse.gui.Basics.Speechbubble.Side;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI.XMLGUICreator;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.filesystem.XML.XMLNode;

public class MenuOptions extends RenderGUI
{
    private Button pOptionsButton;
    private Speechbubble pBubble;
    private static final int GAP_SIZE = 20;

    public MenuOptions(ivec2 pos, ivec2 size)
    {
        this.sType = "MenuOptions";
        this.vPos.set(pos);
        this.vSize.set(size);


        FontManager fonts = FontManager.getInstance();
        
        pOptionsButton = new Button(new ivec2(0, 0), new ivec2(30), "", fonts.getFont("FontAwesome"));
        pOptionsButton.getBox().setTextSize(20);
        pOptionsButton.setCornerRadius(new vec4(0,0,0,0));
        pOptionsButton.onClick((RenderGUI gui) -> { pBubble.show(); });
        addElement(pOptionsButton);


        pBubble = new Speechbubble(new ivec2(size.y / 2, 0), new ivec2(200, 120), Side.ABOVE);
        pBubble.setColor(vec4.sub(GUI.getTheme().primaryColor, 0.01f));
        pBubble.onClick((RenderGUI gui) -> { /*Prevent clickthrough*/ });
        pBubble.hide(true);
        pOptionsButton.addGUI(pBubble);

        reposition();
        resize();
    }

    @Override
    protected void updateOnSizeChange() 
    {
        int xpos = vActualSize.x;
        List<RenderGUI> allEntries = new ArrayList<>();
        allEntries.addAll(pBubble.getChildren());
        allEntries.addAll(getChildren());
        vChildren.clear();
        pBubble.getChildren().clear();

        for(RenderGUI child : allEntries)
        {
            if(!child.getType().equals("button") || child.isHidden())
            {
                addGUI(child);
                continue;   
            }
            
            Button entry = (Button)child;
            entry.setSize(new ivec2(entry.getBox().getTextSize().x + 20, 30));
            xpos -= entry.getSize().x + GAP_SIZE;
            int maxx = 0;
            if(xpos < 0)
            {
                pBubble.addGUI(entry);
                entry.getBox().getBox().hide(true);
                entry.setPosition(new ivec2(0, (pBubble.numChildren() - 1) * 35));
                if(entry.getSize().x > maxx)
                    maxx = entry.getSize().x;
            } 
            else
            {
                addGUI(entry);
                entry.getBox().getBox().hide(false);
                entry.setPosition(new ivec2(xpos, 0));
            }

            pBubble.setSize(new ivec2(maxx, pBubble.numChildren() * 35));
            pOptionsButton.hide(pBubble.numChildren() == 0);
            pBubble.reposition();
        }
    }

    @Override
    public RenderGUI findChildByID(String id)
    {
        if(getID().equals(id))
            return this;
        
        for(RenderGUI child : vChildren)
        {
            RenderGUI res = child.findChildByID(id);
            if(res != null)
                return res;
        }
        for(RenderGUI child : vElements)
        {
            RenderGUI res = child.findChildByID(id);
            if(res != null)
                return res;
        }

        return null;
    }

    public static XMLGUICreator createFromXMLNode() 
    {
        return (XMLNode node) -> { 
			//int maxlength = node.getIntAttribute("maxlength", 0);
			MenuOptions menuoptionsgui = new MenuOptions(new ivec2(0, 0), new ivec2(0, 30));
			return menuoptionsgui;
        };
    };
}
