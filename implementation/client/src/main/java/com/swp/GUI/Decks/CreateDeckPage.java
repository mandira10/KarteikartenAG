package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;

public class CreateDeckPage extends Page
{
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
                Output.info(typeDropdown.getTitle());
                switch(typeDropdown.getTitle())
                {
                    case "Leitner": ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(new LeitnerSystem(), true); break;
                    case "Timing":  ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(new TimingSystem(), true);  break;
                    case "Voting":  ((EditDeckPage)PageManager.viewPage(PAGES.DECK_EDIT)).editDeck(new VoteSystem(), true);    break;
                    default:        NotificationGUI.addNotification("Please specify a decktype", NotificationType.INFO, 5);    break;
                }
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