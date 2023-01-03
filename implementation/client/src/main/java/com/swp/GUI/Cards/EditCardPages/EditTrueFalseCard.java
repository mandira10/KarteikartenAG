package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;

public class EditTrueFalseCard extends RenderGUI
{
    public EditTrueFalseCard()
    {
        this.vSize = new ivec2(100,100);

        //pRatingGUI = new RatingGUI(card);
        addGUI(XMLGUI.loadFile("guis/cards/edit/edittruefalsecardpage.xml"));
        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }
}