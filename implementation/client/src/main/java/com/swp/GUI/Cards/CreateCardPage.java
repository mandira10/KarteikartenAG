package com.swp.GUI.Cards;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
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

/**
 * Die Seite welche einem das Erstellen
 * einer neuen Karte ermöglicht mit
 * einer auswahl an Kartentypen
 * 
 * @author Tom Beuke
 */
public class CreateCardPage extends Page
{
    private final Dropdown pTypeDropdown;

    /**
     * Der Standardkonstruktor der Klasse CreateCardPage
     */
    public CreateCardPage()
    {
        super("Create Card", "createcardpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/cardcreatepage.xml"));

        pTypeDropdown = (Dropdown)findChildByID("typedropdown");

        Button submitButton = (Button)findChildByID("submitbutton");
        submitButton.onClick((RenderGUI gui) -> {
            String title = pTypeDropdown.getTitle();
            if     (title.equals(Locale.getCurrentLocale().getString("audiocard")))          { ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new AudioCard(), true); }
            else if(title.equals(Locale.getCurrentLocale().getString("textcard")))           { ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new TextCard(), true); }
            else if(title.equals(Locale.getCurrentLocale().getString("imagedesccard")))      { ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new ImageDescriptionCard(), true); }
            else if(title.equals(Locale.getCurrentLocale().getString("imagetestcard")))      { ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new ImageTestCard(), true); }
            else if(title.equals(Locale.getCurrentLocale().getString("multiplechoicecard"))) { ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new MultipleChoiceCard(), true); }
            else if(title.equals(Locale.getCurrentLocale().getString("truefalsecard")))      { ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).editCard(new TrueFalseCard(), true); }
            else                                                                             { NotificationGUI.addNotification(Locale.getCurrentLocale().getString("specifycardtype"), NotificationType.INFO, 5); }
        });


        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> {
            PageManager.viewPage(PAGES.CARD_OVERVIEW);
        });

        this.setSizeInPercent(true, true);
        reposition();
    }

    /**
     * Setzt den Titel des auswahl Dropdowns zurück
     */
    public void reset()
    {
        pTypeDropdown.setTitle(Locale.getCurrentLocale().getString("chosecardtype"));
    }
}