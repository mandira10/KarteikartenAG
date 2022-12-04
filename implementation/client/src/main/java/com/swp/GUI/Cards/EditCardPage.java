package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.GUI.Extras.ListTextField;

public class EditCardPage 
{
    private Dropdown pDropdown;
    private Button pApplyButton;
    private ListTextField pListTextField;
    private Card pOldCard, pNewCard;

    public EditCardPage()
    {
        pOldCard = null;
        pNewCard = null;
        pListTextField = new ListTextField();
        //oDropdown = new Dropdown(null, null, null, null, 0)

    }

    public void editCard(String uuid) { editCard(CardController.getCardByUUID(uuid)); }
    public void editCard(Card card)
    {
        if(card == null)
        {

        }
        else
        {

        }
        pOldCard = card;
        pNewCard = Card.copyCard(card);
    }

    private void deleteCard()
    {
        CardController.deleteCard(pOldCard);
    }

    private void applyChanges()
    {
        CardController.updateCardData(pOldCard, pNewCard);
    }
}