package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.swp.Controller.DeckController;
import com.swp.DataModel.StudySystem.StudySystem;

public class EditStudySystemPage extends RenderGUI
{
    private Button pApplyButton;
    private StudySystem.StudySystemType pCurrentType;

    public EditStudySystemPage()
    {
        StudySystem.StudySystemType.values();
    }

    private void deleteStudySystem()
    {
        //DeckController.deleteDeck(pOldDeck);
    }

    private void applyChanges()
    {
        DeckController.getInstance().addStudySystemTypeAndUpdate(pCurrentType);
    }
}
