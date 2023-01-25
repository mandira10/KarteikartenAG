package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Dropdown.DropdownSelectionCallback;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;

public class EditDeckPage extends Page
{
    private Dropdown pStudySystemDropdown;
    private Dropdown pCardOrderDropdown;
    private StudySystem pNewDeck;
    private boolean bNewDeck;
    private StudySystem pOldDeck;
    private TextField pTitleField;

    private RenderGUI pCanvas;

    public EditDeckPage()
    {
        super("Edit Deck", "editdeckpage");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/decks/deckeditpage.xml"));

        pCanvas = findChildByID("canvas");
        pCanvas.hide(true);

        
        RenderGUI optionsMenu = findChildByID("menu");
        //Cancel Button
        Button cancelButton = (Button)optionsMenu.findChildByID("cancelbutton");
        Button applyButton = (Button)optionsMenu.findChildByID("applybutton");
        cancelButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) { PageManager.viewLastPage(); }
        });


        applyButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)
            {
                applyChanges();
            }
        });

        //Titlefield
        pTitleField = (TextField)findChildByID("titlefield");
        pTitleField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete)                {}
            @Override public void input(String input, String complete)  { pNewDeck.setName(complete); }
        });

        //CardOrder dropdown
        pCardOrderDropdown = (Dropdown)findChildByID("cardorderdd");
        pCardOrderDropdown.onSelection(new DropdownSelectionCallback() {
            @Override public void run(String str) 
            {
            }
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

    
    public void editDeck(String uuid) 
    {
        StudySystemController.getInstance().getStudySystemByUUID(uuid, new SingleDataCallback<StudySystem>() {
            @Override
            public void onSuccess(StudySystem data) {
                editDeck(data, false);
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        }); 
    }


    public void editDeck(StudySystem deck, boolean newdeck)
    {
        if(deck == null)
        {
            Output.error("EditDeckPage: Cannot edit deck: is null");
            return;
        }
        pOldDeck = deck;
        pNewDeck = StudySystem.copyStudySystem(deck);
        bNewDeck = newdeck;

        pCanvas.hide(true);
        pTitleField.setString(pNewDeck.getName());
        switch(pNewDeck.getType())
        {
            case LEITNER: pStudySystemDropdown.setTitle("Leitner"); break;
            case TIMING:  pStudySystemDropdown.setTitle("Timing");  break;
            case VOTE:    pStudySystemDropdown.setTitle("Voting");  break;
            case CUSTOM:  pStudySystemDropdown.setTitle("Custom"); pCanvas.hide(false);  break;
            default:      Output.error("Unknown Studysystem type!"); break;
        }

        switch(pNewDeck.getCardOrder())
        {
            case ALPHABETICAL:           pCardOrderDropdown.setTitle("Alphabetical"); break;
            case REVERSED_ALPHABETICAL:  pCardOrderDropdown.setTitle("Reversed Alphabetical");  break;
            case RANDOM:                 pCardOrderDropdown.setTitle("Random");  break;
            default:                     Output.error("Unknown CardOrder type!"); break;
        }
    }

    private void applyChanges()
    {
        StudySystemController.getInstance().updateDeckData(pOldDeck, pNewDeck, bNewDeck, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
        //TODO TBD: fügst du die ausgewählten Karten bei einem neuen Deck bereits in das neue StudySystem vom neuen Deck ein?
        //DeckController.updateDeckData(pOldDeck, pNewDeck, boolean if new // CardSet wenn die von uns eingefügt werden sollen );
    }
}
