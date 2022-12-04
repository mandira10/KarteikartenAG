package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Primitives.RenderGUI;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;

public class EditDeckPage extends RenderGUI
{
    private Dropdown pStudySystemDropdown; //TODO
    private Button pApplyButton;
    private Deck pOldDeck, pNewDeck;

    public EditDeckPage()
    {

    }

    public void editDeck(String uuid) { editDeck(DeckController.getDeckByUUID(uuid)); }
    public void editDeck(Deck deck)
    {
        if(deck == null)
        {

        }
        else
        {

        }
        pOldDeck = deck;
        //pNewDeck = Deck.copyDeck(deck);
    }

    private void deleteDeck()
    {
        DeckController.deleteDeck(pOldDeck);
    }

    private void applyChanges()
    {
        DeckController.updateDeckData(pOldDeck, pNewDeck);
    }
}
