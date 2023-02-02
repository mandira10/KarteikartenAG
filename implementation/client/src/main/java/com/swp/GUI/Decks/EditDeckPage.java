package com.swp.GUI.Decks;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import com.swp.DataModel.StudySystem.StudySystem.CardOrder;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;

public class EditDeckPage extends Page
{
    private Dropdown pStudySystemDropdown;
    private Dropdown pCardOrderDropdown;
    private StudySystem pNewDeck;
    private boolean bIsNewDeck;
    private StudySystem pOldDeck;
    private TextField pTitleField;
    private RenderGUI pLeitnersettings;
    private RenderGUI pVotingsettings;
    private RenderGUI pTimingsettings;
    private TextBox pStudysystemdesc;

    public EditDeckPage()
    {
        super("Edit Deck", "editdeckpage");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/decks/deckeditpage.xml"));

        
        pLeitnersettings = findChildByID("leitnersettings");
        pVotingsettings = findChildByID("votingsettings");
        pTimingsettings = findChildByID("timingsettings");
        pLeitnersettings.hide(true);
        pVotingsettings.hide(true);
        pTimingsettings.hide(true);
        pStudysystemdesc = (TextBox)findChildByID("studysystemdesc");
        pStudysystemdesc.getBox().hide(true);

        TextField votingfield = (TextField)findChildByID("votingfield");
        votingfield.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            {   
                //TODO Set voting system num stars
            }
        });

        TextField timingfield = (TextField)findChildByID("timelimitfield");
        timingfield.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            {   
                //TODO Set timing system time
            }
        });

        TextField leitnerfield = (TextField)findChildByID("leitnerboxesfield");
        leitnerfield.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            {   
                //TODO Set leitner system num boxes
            }
        });


        //Cancel Button
        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> { PageManager.viewLastPage(); });

        //Apply Button
        Button applyButton = (Button)findChildByID("applybutton");
        applyButton.onClick((RenderGUI gui) -> { applyChanges(); });

        //Titlefield
        pTitleField = (TextField)findChildByID("titlefield");
        pTitleField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete)                {}
            @Override public void input(String input, String complete)  { pNewDeck.setName(complete); }
        });

        //CardOrder dropdown
        pCardOrderDropdown = (Dropdown)findChildByID("cardorderdd");
        pCardOrderDropdown.onSelection((String str) -> {
            if(str.equals(Locale.getCurrentLocale().getString("alphabetical")))
                pNewDeck.setCardOrder(CardOrder.ALPHABETICAL);
            else if(str.equals(Locale.getCurrentLocale().getString("reversealphabetical")))
                pNewDeck.setCardOrder(CardOrder.REVERSED_ALPHABETICAL);
            else if(str.equals(Locale.getCurrentLocale().getString("random")))
                pNewDeck.setCardOrder(CardOrder.RANDOM);
        });
        
        
        //Studysystem dropdown
        pStudySystemDropdown = (Dropdown)findChildByID("studysystemdd");
        pStudySystemDropdown.onSelection((String str) -> {
            pLeitnersettings.hide(true);
            pVotingsettings.hide(true);
            pTimingsettings.hide(true);
            if(str.equals(Locale.getCurrentLocale().getString("leitner")))
            {
                pLeitnersettings.hide(false);
                pStudysystemdesc.setString(Locale.getCurrentLocale().getString("leitnerdesc"));
                StudySystem tmpDeck = pNewDeck;
                pNewDeck = new LeitnerSystem();
                pNewDeck.setCardOrder(tmpDeck.getCardOrder());
            }
            else if(str.equals(Locale.getCurrentLocale().getString("voting")))
            {
                pVotingsettings.hide(false);
                pStudysystemdesc.setString(Locale.getCurrentLocale().getString("votingdesc"));
                StudySystem tmpDeck = pNewDeck;
                pNewDeck = new VoteSystem();
                pNewDeck.setCardOrder(tmpDeck.getCardOrder());
            }
            else if(str.equals(Locale.getCurrentLocale().getString("timing")))
            {
                pTimingsettings.hide(false);
                pStudysystemdesc.setString(Locale.getCurrentLocale().getString("timingdesc"));
                StudySystem tmpDeck = pNewDeck;
                pNewDeck = new TimingSystem();
                pNewDeck.setCardOrder(tmpDeck.getCardOrder());
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
    }

    
    public void editDeck(String uuid) 
    {
        StudySystemController.getInstance().getStudySystemByUUID(uuid, new SingleDataCallback<StudySystem>() {
            @Override public void onSuccess(StudySystem data) {
                editDeck(data, false);
            }

            @Override public void onFailure(String msg) {
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
        bIsNewDeck = newdeck;

        pTitleField.setString(pNewDeck.getName());
        switch(pNewDeck.getType())
        {
            case LEITNER: 
                pStudySystemDropdown.setTitle(Locale.getCurrentLocale().getString("leitner")); 
                pLeitnersettings.hide(false);
                pStudysystemdesc.setString(Locale.getCurrentLocale().getString("leitnerdesc"));
                break;

            case TIMING:  
                pStudySystemDropdown.setTitle(Locale.getCurrentLocale().getString("timing"));  
                pTimingsettings.hide(false);
                pStudysystemdesc.setString(Locale.getCurrentLocale().getString("timingdesc"));
                break;

            case VOTE:    
                pStudySystemDropdown.setTitle(Locale.getCurrentLocale().getString("voting"));  
                pVotingsettings.hide(false);
                pStudysystemdesc.setString(Locale.getCurrentLocale().getString("votingdesc"));
                break;
            default:      Output.error("Unknown Studysystem type!"); break;
        }

        switch(pNewDeck.getCardOrder())
        {
            case ALPHABETICAL:           pCardOrderDropdown.setTitle(Locale.getCurrentLocale().getString("alphabetical")); break;
            case REVERSED_ALPHABETICAL:  pCardOrderDropdown.setTitle(Locale.getCurrentLocale().getString("reversealphabetical"));  break;
            case RANDOM:                 pCardOrderDropdown.setTitle(Locale.getCurrentLocale().getString("random"));  break;
            default:                     Output.error("Unknown CardOrder type!"); break;
        }
    }

    private void applyChanges()
    {
        if(pTitleField.getString().isEmpty())
        {
            NotificationGUI.addNotification(Locale.getCurrentLocale().getString("mandatorytitle"), NotificationType.WARNING, 5);
            return;
        }

        StudySystemController.getInstance().updateStudySystemData(pOldDeck, pNewDeck, bIsNewDeck, new SingleDataCallback<Boolean>() {
            @Override public void onSuccess(Boolean data) 
            {

                if(bIsNewDeck)
                    ((DeckOverviewPage)PageManager.viewPage(PAGES.DECK_OVERVIEW)).loadDecks();
                else
                    ((ViewSingleDeckPage) PageManager.viewPage(PageManager.PAGES.DECK_SINGLEVIEW)).setDeck(pNewDeck);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }
}