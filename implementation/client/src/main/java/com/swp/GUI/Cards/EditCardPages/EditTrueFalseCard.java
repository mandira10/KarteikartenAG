package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Switch;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.DataModel.Card;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.TrueFalseCard;

/**
 * Wird verwendet, um Wahr/Falsch Karten zu bearbeiten
 */
public class EditTrueFalseCard extends EditCardGUI
{
    private TrueFalseCard pCard;
    private final Switch pTrueFalseSwitch;

    /**
     * Der Standardkonstruktor für die Klasse EditTrueFalseCard
     */
    public EditTrueFalseCard()
    {
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/edittruefalsecardpage.xml"));
        pTrueFalseSwitch = (Switch)findChildByID("truefalsefield");
        pTrueFalseSwitch.onTick((boolean ticked) -> pCard.setAnswer(ticked));

        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    @Override 
    public void setCard(Card card) 
    {
        if(card.getType() != CardType.TRUEFALSE)
        {
            Output.error("Wrong card type given!");
            return;
        }
        
        this.pCard = (TrueFalseCard)card;
        pTrueFalseSwitch.tick(pCard.isAnswer());
    }

    @Override
    public boolean checkMandatory() 
    {
        return true;
    }
}