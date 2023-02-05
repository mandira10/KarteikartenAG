package com.swp.GUI.Decks;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;

/**
 * Die Seite welche einem das Erstellen
 * eines neuen Decks ermöglicht
 * 
 * @author Tom Beuke
 */
public class CreateDeckPage extends Page
{
    /**
     * Der Standardkonstruktor der Klasse CreateDeckPage
     */
    public CreateDeckPage()
    {
        super("Create Deck", "createdeckpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckcreatepage.xml"));

        Dropdown typeDropdown = (Dropdown)findChildByID("typedropdown");

        Button submitButton = (Button)findChildByID("submitbutton");
        submitButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                String str = typeDropdown.getTitle();

                if(str.equals(Locale.getCurrentLocale().getString("leitner")))
                    ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(new LeitnerSystem(), true);
                else if(str.equals(Locale.getCurrentLocale().getString("voting")))
                    ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(new VoteSystem(), true);   
                else if(str.equals(Locale.getCurrentLocale().getString("timing")))
                    ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(new TimingSystem(), true); 
                else
                    NotificationGUI.addNotification("Please specify a decktype", NotificationType.INFO, 5);

            }
        });


        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  
            {
                PageManager.viewPage(PAGES.DECK_OVERVIEW);
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
    }
}