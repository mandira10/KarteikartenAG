package com.swp.GUI.Extras;

import java.util.ArrayList;
import java.util.List;

import com.gumse.gui.GUI;
import com.gumse.gui.Theme;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Speechbubble;
import com.gumse.gui.Basics.Speechbubble.Side;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI.XMLGUICreator;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.filesystem.XML.XMLNode;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Output;

public class ListOrder extends Button
{

    public enum Order
    {
        ALPHABETICAL,
        DATE,
        NUM_DECKS
    };

    public interface ListOrderCallback
    {
        public void run(Order order, boolean reverse);
    }

    private ListOrderCallback pCallback;
    private Speechbubble pBubble;
    private List<Option> alOptions;

    private class Option extends RenderGUI
    {
        private Text pName, pSymbol;
        private String sSymbol, sReverseSymbol;
        private Order iOrder;
        private boolean bReverse, bIsActive;

        public Option(String name, Order order, String symbol, String reversesymbol, ivec2 pos)
        {
            this.vPos.set(pos);
            this.vSize.set(new ivec2(100, 30));
            this.sSymbol = symbol;
            this.sReverseSymbol = reversesymbol;
            this.iOrder = order;
            this.bReverse = false;

            pSymbol = new Text(symbol, FontManager.getInstance().getFont("FontAwesome"), new ivec2(0, 0), 0);
            pSymbol.setCharacterHeight(25);
            addElement(pSymbol);

            pName = new Text(name, FontManager.getInstance().getDefaultFont(), new ivec2(40, 0), 0);
            pName.setCharacterHeight(25);
            addElement(pName);

            this.onHover(null, Mouse.GUM_CURSOR_HAND);

            this.onEnter((RenderGUI gui) -> {
                pSymbol.setColor(GUI.getTheme().accentColor);
                pName.setColor(GUI.getTheme().accentColor);
            });

            this.onLeave((RenderGUI gui) -> {
                if(!bIsActive)
                {
                    pSymbol.setColor(GUI.getTheme().textColor);
                    pName.setColor(GUI.getTheme().textColor);
                }
            });

            this.onClick((RenderGUI gui) -> {
                bReverse = !bReverse;
                pSymbol.setString(bReverse ? sReverseSymbol : sSymbol);
                if(pCallback != null)
                    pCallback.run(iOrder, bReverse);
                
                for(Option op : alOptions)
                    op.setActive(false);
                setActive(true);
            });

            vSize.x = pName.getSize().x + 40;

            resize();
            reposition();
        }

        public void setActive(boolean active)
        {
            this.bIsActive = active;
            if(bIsActive)
            {
                pSymbol.setColor(GUI.getTheme().accentColor);
                pName.setColor(GUI.getTheme().accentColor);
            }
            else
            {
                pSymbol.setColor(GUI.getTheme().textColor);
                pName.setColor(GUI.getTheme().textColor);
            }
        }
    };


    public ListOrder(ivec2 pos, ivec2 size, String[] localeids)
    {
        super(pos, size, "", FontManager.getInstance().getFont("FontAwesome"));
        this.sType = "ListOrder";
        this.alOptions = new ArrayList<>();
        
        onClick((RenderGUI gui) -> { pBubble.show(); });

        pBubble = new Speechbubble(new ivec2(size.y / 2, 0), new ivec2(200, 120), Side.ABOVE);
        pBubble.setColor(vec4.sub(GUI.getTheme().primaryColor, 0.06f));
        pBubble.onClick((RenderGUI gui) -> { /*Prevent clickthrough*/ });
        pBubble.hide(true);
        addElement(pBubble);

        alOptions.add(new Option(localeids[0], Order.ALPHABETICAL, "", "", new ivec2(5, 5)));
        if(localeids.length > 1)
        {
            alOptions.add(new Option(localeids[1], Order.NUM_DECKS,    "", "", new ivec2(5, 45)));
            alOptions.add(new Option(localeids[2], Order.DATE,         "", "", new ivec2(5, 80)));
        }

        alOptions.get(0).setActive(true);

        int width = 0;
        for(Option op : alOptions)
        {
            if(op.getSize().x > width)
                width = op.getSize().x;
            pBubble.addGUI(op);
        }
        pBubble.setSize(new ivec2(width + 10, alOptions.size() * 40 - 5));

        resize();
        reposition();
    }

    public void setCallback(ListOrderCallback callback)
    {
        this.pCallback = callback;
    }

    @Override
    protected void updateOnThemeChange() 
    {
        pBubble.setColor(vec4.sub(GUI.getTheme().primaryColor, 0.06f));
    }

    public static XMLGUICreator createFromXMLNode() 
    {
        return (XMLNode node) -> { 
			String[] optionids = node.getAttribute("option-ids").split(",");
			
			ListOrder listordergui = new ListOrder(new ivec2(0,0), new ivec2(30,30), optionids);
			return listordergui;
        };
    };
}