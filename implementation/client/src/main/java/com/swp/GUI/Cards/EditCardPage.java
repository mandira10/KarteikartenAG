package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.GUI.Extras.ListTextField;

public class EditCardPage 
{
    Dropdown pDropdown;
    Button pApplyButton;
    ListTextField pListTextField;
    Card pOldCard, pNewCard;

    public EditCardPage(Card card)
    {
        pOldCard = card;
        pNewCard = Card.copyCard(card);
        pListTextField = new ListTextField();
        //oDropdown = new Dropdown(null, null, null, null, 0)

    }

    private void deleteCard()
    {
        CardController.deleteCard(pOldCard);
    }

    private void applyChanges()
    {
        CardController.editCard(pOldCard, pNewCard);
    }
}