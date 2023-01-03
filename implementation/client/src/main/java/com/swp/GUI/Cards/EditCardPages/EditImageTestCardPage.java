package com.swp.GUI.Cards.EditCardPages;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;

public class EditImageTestCardPage extends RenderGUI
{
    private TextBox pImageBox;

    public EditImageTestCardPage()
    {
        this.vSize = new ivec2(100,100);

        //pRatingGUI = new RatingGUI(card);
        addGUI(XMLGUI.loadFile("guis/cards/edit/editimagetestcardpage.xml"));

        pImageBox = (TextBox)findChildByID("imagebox");

        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    @Override
    public void update() 
    {
        if(bIsHidden)
            return;
            
        if(pImageBox.isMouseInside())
        {
            Mouse.setActiveHovering(true);
            Window.CurrentlyBoundWindow.getMouse().setCursor(Mouse.GUM_CURSOR_HAND);
            
            if(pImageBox.isClicked())
            {
                selectImageFile();
            }
        }
    }


    private void selectImageFile()
    {
        System.out.println("\nOpening file open dialog...");
        System.out.println(tinyfd_openFileDialog("Open File(s)", "", null, null, true));
    }
}