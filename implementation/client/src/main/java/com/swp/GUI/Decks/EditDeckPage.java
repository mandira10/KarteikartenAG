package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Basics.Dropdown.DropdownSelectionCallback;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;

public class EditDeckPage extends Page
{
    private Dropdown pStudySystemDropdown;
    private Button pApplyButton;
    private Deck pOldDeck, pNewDeck;
    private TextField pTitleField;

    private RenderGUI pCanvas;

    public EditDeckPage()
    {
        super("Edit Deck");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/decks/deckeditpage.xml"));

        pCanvas = findChildByID("canvas");
        pCanvas.hide(true);

        
        RenderGUI optionsMenu = findChildByID("menu");
        //Cancel Button
        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        cancelButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() { PageManager.viewLastPage(); }
        });

        //Titlefield
        pTitleField = (TextField)findChildByID("titlefield");
        pTitleField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete)                {}
            @Override public void input(String input, String complete)  { pNewDeck.setName(complete); }
        });

        //Studysystem dropdown
        pStudySystemDropdown = (Dropdown)findChildByID("studysystemdd");
        pStudySystemDropdown.onSelection(new DropdownSelectionCallback() {
            @Override public void run(String str) 
            {
                if(str.equals("Custom"))
                    pCanvas.hide(false);
                else
                    pCanvas.hide(true);
            }
        });



        this.setSizeInPercent(true, true);
        reposition();
    }

    public void editDeck(String uuid) { editDeck(DeckController.getDeckByUUID(uuid)); }
    public void editDeck(Deck deck)
    {
        if(deck == null)
        {
            Debug.error("EditDeckPage: Cannot edit deck: is null");
            return;
        }

        pOldDeck = deck;
        pNewDeck = new Deck(deck);

        pCanvas.hide(true);
        pTitleField.setString(pNewDeck.getName());
        switch(pNewDeck.getStudySystem().getType().getType())
        {
            case LEITNER: pStudySystemDropdown.setTitle("Leitner"); break;
            case TIMING:  pStudySystemDropdown.setTitle("Timing");  break;
            case VOTE:    pStudySystemDropdown.setTitle("Voting");  break;
            case CUSTOM:  pStudySystemDropdown.setTitle("Custom"); pCanvas.hide(false);  break;
            default:      Debug.error("Unknown Studysystem type!"); break;
        }
    }

    private void applyChanges()
    {
        DeckController.updateDeckData(pOldDeck, pNewDeck);
    }
}
