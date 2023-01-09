package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;

public class CreateCardPage extends Page
{
    public CreateCardPage()
    {
        super("Create Card");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/cardcreatepage.xml"));

        Dropdown typeDropdown = (Dropdown)findChildByID("typedropdown");

        Button submitButton = (Button)findChildByID("submitbutton");
        submitButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                Debug.info(typeDropdown.getTitle());
                switch(typeDropdown.getTitle())
                {
                    case "Audio":             ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new AudioCard(), true);            break;
                    case "Text":              ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new TextCard(), true);             break;
                    case "Image Description": ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new ImageDescriptionCard(), true); break;
                    case "Image Test":        ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new ImageTestCard(), true);        break;
                    case "Multiplechoice":    ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new MultipleChoiceCard(), true);   break;
                    case "True/False":        ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new TrueFalseCard(), true);        break;
                    default:                  NotificationGUI.addNotification("Please specify a cardtype", NotificationType.INFO, 5);     break;
                }
            }
        });


        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  
            {
                PageManager.viewPage(PAGES.CARD_OVERVIEW);
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
    }
}