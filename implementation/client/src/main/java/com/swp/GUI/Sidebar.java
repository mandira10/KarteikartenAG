package com.swp.GUI;

import java.util.ArrayList;

import com.gumse.basics.SmoothFloat;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.*;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;

public class Sidebar extends RenderGUI
{
    public interface SidebarCallbackFunction 
    {
        void run();
    }

    private class SidebarItem extends RenderGUI
    {
        private TextBox pSymbol;
        private Text pTitle;
        private SmoothFloat pColorSmoothFloat;
        private SidebarCallbackFunction pCallback;

        public SidebarItem(char symbol, String title, ivec2 pos, SidebarCallbackFunction callback)
        {
            this.sType = "SidebarItem";
            this.vPos.set(pos);
            this.vSize.set(new ivec2(100, 100));
            this.setSizeInPercent(true, false);
            this.pCallback = callback;

            Font pFontAwesome = FontManager.getInstance().getFont("FontAwesome");
            pSymbol = new TextBox(Character.toString(symbol), pFontAwesome, new ivec2(0, 0), new ivec2(100, 100));
            pSymbol.setTextSize(60);
            pSymbol.setAlignment(TextBox.Alignment.CENTER);
            pSymbol.getBox().hide(true);
            addElement(pSymbol);

            pTitle = new Text(title, FontManager.getInstance().getDefaultFont(), new ivec2(120, 50 - 15), 0);
            pTitle.setCharacterHeight(30);
            pTitle.setColor(new vec4(0.9f, 0.9f, 0.9f, 1.0f));
            addElement(pTitle);

            pColorSmoothFloat = new SmoothFloat(0, 30, 0);

            reposition();
        }

        @Override
        public void update()
        {
            if(bIsHidden)
                return;
            
            if(pColorSmoothFloat.update())
            {
                vec4 color = vec4.mix(new vec4(0.9f, 0.9f, 0.9f, 1.0f), new vec4(0.19f, 0.2f, 0.42f, 1.0f), pColorSmoothFloat.get());

                pSymbol.setTextColor(color);
                pTitle.setColor(color);
            }

            if(isClicked())
            {
                pCallback.run();
            }

            if(isMouseInside())
            {
                pColorSmoothFloat.setTarget(1.0f);
                Mouse.setActiveHovering(true);
                Window.CurrentlyBoundWindow.getMouse().setCursor(Mouse.GUM_CURSOR_HAND);
            }
            else
            {
                pColorSmoothFloat.setTarget(0.0f);
            }

            updatechildren();
        }

        public void setVisibility(float v, int boxsize)
        {
            if(v > 0.1f)
            {
                pTitle.hide(false);
                pTitle.setRenderBox(new bbox2i(pTitle.getPosition(), new ivec2(boxsize, 100)));
            }
            else
            {
                pTitle.hide(true);
            }
        }
    }


    private Box pBackground;

    private SmoothFloat pSmoothFloat;

    public Sidebar()
    {
        pBackground = new Box(new ivec2(0,0), new ivec2(100, 100));
        pBackground.setSizeInPercent(false, true);
        pBackground.setColor(new vec4(0.3f, 0.3f, 0.3f, 1.0f));
        addElement(pBackground);

        pSmoothFloat = new SmoothFloat(0, 10, 0);

        pBackground.addGUI(new SidebarItem('', "Home",       new ivec2(0,10), new SidebarCallbackFunction() {
            @Override public void run() 
            {
                PageManager.viewPage("LoginPage");
            }
        }));
        pBackground.addGUI(new SidebarItem('', "Cards",      new ivec2(0,120), new SidebarCallbackFunction() {
            @Override public void run() 
            {
                PageManager.viewPage("CardOverview");
            }
        }));
        pBackground.addGUI(new SidebarItem('', "Decks",      new ivec2(0,230), new SidebarCallbackFunction() {
            @Override public void run() 
            {
            }
        }));
        pBackground.addGUI(new SidebarItem('', "Categories", new ivec2(0,340), new SidebarCallbackFunction() {
            @Override public void run() 
            {
            }
        }));


        SidebarItem settingsItem = new SidebarItem('', "Settings", new ivec2(0, 100), new SidebarCallbackFunction() {
            @Override public void run() 
            {
            }
        });
        settingsItem.setPositionInPercent(false, true);
        settingsItem.setOrigin(new ivec2(0, 100));
        pBackground.addGUI(settingsItem);

        resize();
        reposition();
    }
    

    @Override
    public void update()
    {
        if(bIsHidden)
            return;

        if(pBackground.isMouseInside())
        {
            pSmoothFloat.setTarget(1);
            Mouse.setBusiness(true);
        }
        else
        {
            pSmoothFloat.setTarget(0);
            if(Mouse.isBusy())
                Mouse.setBusiness(false);
        }

        if(pSmoothFloat.update())
        {
            //System.out.println(v);
            int boxSize = (int)(150 * pSmoothFloat.get());
            pBackground.setSize(new ivec2(100 + boxSize, 100));
            for(int i = 0; i < pBackground.numChildren(); i++)
            {
                ((SidebarItem)pBackground.getChild(i)).setVisibility(pSmoothFloat.get(), boxSize);
            }
        }

        
        updatechildren();
    }
}
