package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;

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
        StudySystemController.getInstance().addStudySystemTypeAndUpdate(pCurrentType, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }
}
