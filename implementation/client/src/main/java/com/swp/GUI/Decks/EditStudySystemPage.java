package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.swp.Controller.DeckController;
import com.swp.DataModel.StudySystemType;

public class EditStudySystemPage extends RenderGUI
{
    private Button pApplyButton;
    private StudySystemType pCurrentType;

    public EditStudySystemPage()
    {
        DeckController.getStudySystemTypes();
    }

    private void deleteStudySystem()
    {
        //DeckController.deleteDeck(pOldDeck);
    }

    private void applyChanges()
    {
        DeckController.addStudySystemTypeAndUpdate(pCurrentType);
    }
}
