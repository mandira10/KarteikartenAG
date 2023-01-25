package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.CardTypes.TextCard;

public class EditTextCard extends RenderGUI
{
    TextCard pCard;
    TextField pAnswerField;

    public EditTextCard()
    {
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/edittextcardpage.xml"));

        pAnswerField = (TextField)findChildByID("answerfield");
        pAnswerField.setCallback(new TextFieldInputCallback() { 
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            {
                pCard.setAnswer(complete);
            } 
        });
        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void setCard(TextCard card)
    {
        pCard = card;
        pAnswerField.setString(pCard.getAnswer());
    }
}