package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.GUI.Cards.EditCardPages.EditMultipleChoiceCardAnswerEntry.RemoveAnswerEntryCallback;

public class EditMultipleChoiceCard extends RenderGUI
{
    private Button pAddEntryButton;
    private Scroller pContextScroller;

    public EditMultipleChoiceCard()
    {
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/editmultiplechoicecardpage.xml"));
        pContextScroller = (Scroller)findChildByID("scroller");

        pAddEntryButton = new Button(new ivec2(100, 40), new ivec2(30), "+", FontManager.getInstance().getDefaultFont());
        pAddEntryButton.setPositionInPercent(true, false);
        pAddEntryButton.getBox().setTextSize(28);
        pAddEntryButton.setOrigin(new ivec2(30, 0));
        pAddEntryButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                addEntry("", false);
            }
        });
        pContextScroller.addGUI(pAddEntryButton);
        
        addEntry("TestEntry", false);

        this.setSizeInPercent(true, true);
        reposition();
    }

    private void reallignEntries()
    {
        int yoffset = -40; // First child is pAddEntryButton
        for(RenderGUI child : pContextScroller.getChildren())
        {
            child.setPosition(new ivec2(0, yoffset));
            yoffset += 40;
        }
        pAddEntryButton.setPosition(new ivec2(100, yoffset));
    }

    public void addEntry(String answer, boolean iscorrect)
    {
        EditMultipleChoiceCardAnswerEntry entry = new EditMultipleChoiceCardAnswerEntry(answer, iscorrect, new RemoveAnswerEntryCallback() {
            @Override public void run(EditMultipleChoiceCardAnswerEntry entry) 
            {
                pContextScroller.removeChild(entry);
                reallignEntries();
            }
        });

        pContextScroller.addGUI(entry);
        reallignEntries();
    }
}