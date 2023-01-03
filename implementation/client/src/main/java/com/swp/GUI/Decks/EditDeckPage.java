package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;

public class EditDeckPage extends Page
{
    private Dropdown pStudySystemDropdown; //TODO
    private Button pApplyButton;
    private Deck pOldDeck, pNewDeck;

    private RenderGUI pCanvas;

    public EditDeckPage()
    {
        super("Edit Deck");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/decks/deckeditpage.xml"));

        pCanvas = findChildByID("canvas");

        
        RenderGUI optionsMenu = findChildByID("menu");
        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                PageManager.viewLastPage();
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
        resize();
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
