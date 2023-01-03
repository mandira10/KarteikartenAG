package com.swp.GUI.Cards.EditCardPages;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;

public class EditImageTestCard extends RenderGUI
{
    private Button pImageButton;

    public EditImageTestCard()
    {
        this.vSize = new ivec2(100,100);

        //pRatingGUI = new RatingGUI(card);
        addGUI(XMLGUI.loadFile("guis/cards/edit/editimagetestcardpage.xml"));

        pImageButton = (Button)findChildByID("imagebox");
        pImageButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                selectImageFile();
            }
        });

        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }


    private void selectImageFile()
    {
        System.out.println("\nOpening file open dialog...");
        System.out.println(tinyfd_openFileDialog("Open File(s)", "", null, null, true));
    }
}