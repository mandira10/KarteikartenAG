package com.swp.GUI.Decks;

import java.util.ArrayList;
import java.util.List;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.GumMath;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import com.swp.DataModel.StudySystem.StudySystem.CardOrder;
import com.swp.DataModel.StudySystem.StudySystem.StudySystemType;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Decks.EditLeitnerDayEntry.EntryCallback;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;

/**
 * Die Seite welche es einem ermöglicht, Decks zu bearbeiten
 */
public class EditDeckPage extends Page
{
    private final TextField pTitleField;
    private final TextField pTimingField;
    private final TextField pVotingField;
    private final Scroller pLeitnersettings;
    private final RenderGUI pVotingsettings;
    private final RenderGUI pTimingsettings;
    private final TextBox pStudysystemdesc;
    private final Dropdown pStudySystemDropdown;
    private final Dropdown pCardOrderDropdown;
    private StudySystem pNewDeck;
    private boolean bIsNewDeck;
    private boolean bChangedBoxes;
    private StudySystem pOldDeck;
    private Button pAddEntryButton;

    /**
     * Der Standardkonstruktor der Klasse EditDeckPage
     */
    public EditDeckPage()
    {
        super("Edit Deck", "editdeckpage");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/decks/deckeditpage.xml"));

        findChildByID("scroller");
        
        pLeitnersettings = (Scroller)findChildByID("leitnersettings");
        pVotingsettings = findChildByID("votingsettings");
        pTimingsettings = findChildByID("timingsettings");
        pLeitnersettings.hide(true);
        pVotingsettings.hide(true);
        pTimingsettings.hide(true);
        pStudysystemdesc = (TextBox)findChildByID("studysystemdesc");
        pStudysystemdesc.getBox().hide(true);

        pVotingField = (TextField)findChildByID("votingfield");
        pVotingField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            {   
                int numStars = GumMath.clamp(Toolbox.StringToInt(complete), 1, 10);
                ((VoteSystem)pNewDeck).setStars(numStars);
            }
        });

        pTimingField = (TextField)findChildByID("timelimitfield");
        pTimingField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            {
                ((TimingSystem)pNewDeck).setTimeLimit(Toolbox.StringToFloat(complete));
            }
        });


        //Cancel Button
        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> PageManager.viewLastPage());

        //Apply Button
        Button applyButton = (Button)findChildByID("applybutton");
        applyButton.onClick((RenderGUI gui) -> applyChanges());

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
            if(!bIsNewDeck)
            {
                ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("deckoverridewarning"), new ConfirmationCallback() {
                    @Override public void onCancel() {}
                    @Override public void onConfirm() {
                        selectSystem(StudySystemType.NONE, str);
                    }
                });
            }
            else
            {
                selectSystem(StudySystemType.NONE, str);
            }
        });

        createAddButton();

        this.setSizeInPercent(true, true);
        reposition();
    }


    private void reallignEntries()
    {
        int yoffset = -40; // First child is pAddEntryButton
        int i = 0;
        for(RenderGUI child : pLeitnersettings.getChildren())
        {
            child.setPosition(new ivec2(0, yoffset));
            if(child.getType().equals("EditLeitnerDayEntry"))
                ((EditLeitnerDayEntry)child).setDayNumber(++i);
            yoffset += 40;
        }
        pAddEntryButton.setPosition(new ivec2(100, yoffset));
    }

    /**
     * Fügt einen Eintrag zu den Leitner Box feldern hinzu
     *
     * @param days Anzahl der tage nachdem neu gelernt werden muss
     */
    public void addEntry(int days)
    {
        EditLeitnerDayEntry entry = new EditLeitnerDayEntry(days, pLeitnersettings.numChildren(), new EntryCallback() {
            @Override public void onRemove(EditLeitnerDayEntry entry) 
            {
                if(!bIsNewDeck)
                {
                    ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("deckoverridewarning"), new ConfirmationCallback() {
                        @Override public void onCancel() {}
                        @Override public void onConfirm() {
                            pLeitnersettings.removeChild(entry);
                            reallignEntries();
                            saveLeitnerboxes();
                        }
                    });
                }
                else
                {
                    pLeitnersettings.removeChild(entry);
                    reallignEntries();
                    saveLeitnerboxes();
                }
            }

            @Override public void onChange(EditLeitnerDayEntry entry) 
            {
                saveLeitnerboxes();
            }
        });

        pLeitnersettings.addGUI(entry);
        reallignEntries();
    }

    private void createAddButton()
    {
        pAddEntryButton = new Button(new ivec2(100, 40), new ivec2(30), "+", FontManager.getInstance().getDefaultFont());
        pAddEntryButton.setPositionInPercent(true, false);
        pAddEntryButton.getBox().setTextSize(28);
        pAddEntryButton.setOrigin(new ivec2(30, 0));
        pAddEntryButton.onClick((RenderGUI gui) -> {
            if(!bIsNewDeck)
            {
                ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("deckoverridewarning"), new ConfirmationCallback() {
                    @Override public void onCancel() {}
                    @Override public void onConfirm() {
                        addEntry(5);
                    }
                });
            }
            else
            {
                addEntry(5);
            }
        });
        pLeitnersettings.addGUI(pAddEntryButton);
    }

    private void saveLeitnerboxes()
    {
        LeitnerSystem system = (LeitnerSystem)pNewDeck;
        List<StudySystemBox> boxes = new ArrayList<>();
        for(RenderGUI child : pLeitnersettings.getChildren())
        {
            if(child.getType().equals("EditLeitnerDayEntry"))
            {
                EditLeitnerDayEntry entry = (EditLeitnerDayEntry)child;
                boxes.add(new StudySystemBox(system, entry.getDays(), boxes.size()));
            }
        }
        bChangedBoxes = true;
        system.setBoxes(boxes);
    }



    /**
     * Bearbeitet ein Deck anhand seiner UUID
     *
     * @param uuid Die UUID eines Decks
     */
    public void editDeck(String uuid) 
    {
        StudySystemController.getInstance().getStudySystemByUUID(uuid, new SingleDataCallback<>() {
            @Override public void onSuccess(StudySystem data) {
                editDeck(data, false);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        }); 
    }


    /**
     * Übergibt ein zu bearbeitendes Deck
     *
     * @param deck    Ein Deck
     * @param newdeck Ist das übergebene Deck neu?
     */
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
        selectSystem(pNewDeck.getType(), "");

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
        if(pTitleField.getString().isBlank())
        {
            NotificationGUI.addNotification(Locale.getCurrentLocale().getString("mandatorytitle"), NotificationType.WARNING, 5);
            return;
        }

        StudySystemController.getInstance().updateStudySystemData(pOldDeck, pNewDeck, bIsNewDeck, bChangedBoxes, new SingleDataCallback<Boolean>() {
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
        bChangedBoxes = false;}

    void selectSystem(StudySystem.StudySystemType type, String name)
    {
        pLeitnersettings.hide(true);
        pVotingsettings.hide(true);
        pTimingsettings.hide(true);
        if(name.equals(Locale.getCurrentLocale().getString("leitner")) || type == StudySystemType.LEITNER)
        {
            pLeitnersettings.hide(false);
            pStudysystemdesc.setString(Locale.getCurrentLocale().getString("leitnerdesc"));
            if(type == StudySystemType.NONE)
            {
                if(!bIsNewDeck)
                    NotificationGUI.addNotification(Locale.getCurrentLocale().getString("studysyschangewarning"),NotificationType.WARNING,5);
                StudySystem tmpDeck = pNewDeck;
                pNewDeck = new LeitnerSystem();
                pNewDeck.setCardOrder(tmpDeck.getCardOrder());
                pNewDeck.setName(tmpDeck.getName());
            }
            pLeitnersettings.destroyChildren();
            createAddButton();
            for(int i = 0; i < (pNewDeck).getBoxes().size(); i++)
                addEntry(pNewDeck.getBoxes().get(i).getDaysToLearnAgain());
            reallignEntries();

            pStudySystemDropdown.setTitle(Locale.getCurrentLocale().getString("leitner"));
        }
        else if(name.equals(Locale.getCurrentLocale().getString("voting")) || type == StudySystemType.VOTE)
        {
            pVotingsettings.hide(false);
            pStudysystemdesc.setString(Locale.getCurrentLocale().getString("votingdesc"));
            if(type == StudySystemType.NONE)
            {
                if(!bIsNewDeck)
                    NotificationGUI.addNotification(Locale.getCurrentLocale().getString("studysyschangewarning"),NotificationType.WARNING,5);
                StudySystem tmpDeck = pNewDeck;
                pNewDeck = new VoteSystem();
                pNewDeck.setCardOrder(tmpDeck.getCardOrder());
                pNewDeck.setName(tmpDeck.getName());
            }
            pVotingField.setString(String.valueOf(((VoteSystem)pNewDeck).getStars()));
            pStudySystemDropdown.setTitle(Locale.getCurrentLocale().getString("voting"));
        }
        else if(name.equals(Locale.getCurrentLocale().getString("timing")) || type == StudySystemType.TIMING)
        {
            pTimingsettings.hide(false);
            pStudysystemdesc.setString(Locale.getCurrentLocale().getString("timingdesc"));
            if(type == StudySystemType.NONE)
            {
                if(!bIsNewDeck)
                    NotificationGUI.addNotification(Locale.getCurrentLocale().getString("studysyschangewarning"),NotificationType.WARNING,5);
                StudySystem tmpDeck = pNewDeck;
                pNewDeck = new TimingSystem();
                pNewDeck.setCardOrder(tmpDeck.getCardOrder());
                pNewDeck.setName(tmpDeck.getName());
            }
            pTimingField.setString(String.valueOf(((TimingSystem)pNewDeck).getTimeLimit()));
            pStudySystemDropdown.setTitle(Locale.getCurrentLocale().getString("timing"));
        }
    }
}