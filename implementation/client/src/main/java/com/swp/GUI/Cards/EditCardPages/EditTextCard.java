package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Locale;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.DataModel.Card;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;

/**
 * Wird verwendet, um Text Karten zu bearbeiten
 * 
 * @author Tom Beuke
 */
public class EditTextCard extends EditCardGUI
{
    TextCard pCard;
    TextField pAnswerField;

    /**
     * Der Standardkonstruktor für die Klasse EditTextCard
     */
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

    @Override 
    public void setCard(Card card) 
    {
        if(card.getType() != CardType.TEXT)
        {
            Output.error("Wrong card type given!");
            return;
        }
        
        this.pCard = (TextCard)card;
        pAnswerField.setString(pCard.getAnswer());
    }

    @Override
    public boolean checkMandatory() 
    {
        if(pAnswerField.getString().isBlank())
        {
            NotificationGUI.addNotification(Locale.getCurrentLocale().getString("mandatoryanswer"), NotificationType.WARNING, 5);
            return false;
        }
        return true;
    }
}