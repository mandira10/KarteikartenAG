package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.CardTypes.TrueFalseCard;

public class EditTrueFalseCard extends RenderGUI
{
    private TrueFalseCard pCard;
    private Switch pTrueFalseSwitch;

    public EditTrueFalseCard()
    {
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/edittruefalsecardpage.xml"));
        pTrueFalseSwitch = (Switch)findChildByID("truefalsefield");
        pTrueFalseSwitch.onTick((boolean ticked) -> {
            pCard.setAnswer(ticked);
        });

        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void setCard(TrueFalseCard card)
    {
        this.pCard = card;
        pTrueFalseSwitch.tick(pCard.isAnswer());
    }
}