package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;

public class EditTextCard extends RenderGUI
{
    public EditTextCard()
    {
        this.vSize = new ivec2(100,100);

        //pRatingGUI = new RatingGUI(card);
        addGUI(XMLGUI.loadFile("guis/cards/edit/edittextcardpage.xml"));
        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }
}