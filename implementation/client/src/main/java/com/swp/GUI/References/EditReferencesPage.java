package com.swp.GUI.References;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.Card;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;

public class EditReferencesPage extends Page
{
    private Scroller pContextScroller;
    private Button pAddEntryButton;

    public EditReferencesPage()
    {
        super("Edit References", "editreferencespage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/references/editreferences.xml"));

        pContextScroller = (Scroller)findChildByID("canvas");
        pContextScroller.getMainChildContainer().shouldUpdateFromFirstToLast(true);
        
        createAddButton();

        Button applyButton = (Button)findChildByID("applybutton");
        applyButton.onClick((RenderGUI gui) -> { applyChanges(); });

        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> { PageManager.viewPage(PAGES.CARD_EDIT); });

        
        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setCard(Card card)
    {
        pContextScroller.destroyChildren();
        createAddButton();

        String references = card.getReferences();

        //for(ImageDescriptionCardAnswer answer : )
        {
            //addEntry(answer.answertext, new ivec2(answer.xpos, answer.ypos));
        }
    }

    private void reallignEntries()
    {
        int i = 0;
        for(RenderGUI child : pContextScroller.getChildren())
        {
            if(child.getType().equals("EditReferencesEntry"))
            {
                EditReferencesEntry entry = (EditReferencesEntry)child;
                entry.setPosition(new ivec2(0, i++ * 80));
            }
        }
        pAddEntryButton.setPosition(new ivec2(100, i * 80));
    }

    private void createAddButton()
    {
        pAddEntryButton = new Button(new ivec2(100, 40), new ivec2(30), "+", FontManager.getInstance().getDefaultFont());
        pAddEntryButton.setPositionInPercent(true, false);
        pAddEntryButton.getBox().setTextSize(28);
        pAddEntryButton.setOrigin(new ivec2(30, 0));
        pAddEntryButton.onClick((RenderGUI gui) -> { addEntry(""); });
        pContextScroller.addGUI(pAddEntryButton);
    }

    public void addEntry(String reference)
    {
        final EditReferencesEntry nentry = new EditReferencesEntry(reference, (EditReferencesEntry entry) -> {
            pContextScroller.removeChild(entry);
            reallignEntries();
        }, (RenderGUI gui) -> {
            closeAll(gui);
        });

        pContextScroller.addGUI(nentry);
        reallignEntries();
    }

    private void closeAll(RenderGUI except)
    {
        for(RenderGUI child : pContextScroller.getChildren())
        {
            if(child.getType().equals("EditReferencesEntry") && child != except)
            {
                EditReferencesEntry entry = (EditReferencesEntry)child;
                entry.closeDropdown();
            }
        }
    }

    private void applyChanges()
    {
        
    }
}