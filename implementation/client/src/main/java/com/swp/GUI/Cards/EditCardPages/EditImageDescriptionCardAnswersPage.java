package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.textures.Texture;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;

import java.util.ArrayList;
import java.util.List;

/**
 * Wird verwendet, um die Antworten einer Bildbeschreibungskarte zu bearbeiten
 * 
 * @author Tom Beuke
 */
public class EditImageDescriptionCardAnswersPage extends Page
{
    private final Box pImageBox;
    private final Scroller pContextScroller;
    private Button pAddEntryButton;
    private ImageDescriptionCard pCard;

    /**
     * Der Standardkonstruktor für die Klasse EditImageDescriptionCardAnswersPage
     */
    public EditImageDescriptionCardAnswersPage()
    {
        super("Edit Answers", "editanswerspage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/editimagedesccardanswers.xml"));

        pImageBox = (Box)findChildByID("imagebox");
        pImageBox.invertTexcoordY(true);
        pContextScroller = (Scroller)findChildByID("canvas");
        
        createAddButton();

        Button applyButton = (Button)findChildByID("applybutton");
        applyButton.onClick((RenderGUI gui) -> applyChanges());

        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> PageManager.viewPage(PAGES.CARD_EDIT));

        
        this.setSizeInPercent(true, true);
        reposition();
    }

    @Override
    protected void updateOnSizeChange() 
    {
        pImageBox.setSize(new ivec2(50, pImageBox.getSize().x));
    }

    public void setCard(ImageDescriptionCard card)
    {
        pContextScroller.destroyChildren();
        pImageBox.destroyChildren();
        pCard = card;

        if(card.getImage() != null)
        {
            Texture tex = new Texture();
            tex.loadMemory(card.getImage());
            pImageBox.setTexture(tex);
            pImageBox.setColor(new vec4(1, 1, 1, 1));
            pImageBox.fitWidth((float)tex.getSize().y / (float)tex.getSize().x);
        }

        createAddButton();
        for(ImageDescriptionCardAnswer answer : card.getAnswers())
        {
            addEntry(answer.answertext, new ivec2(answer.xpos, answer.ypos));
        }
    }

    private void reallignEntries()
    {
        int i = 1;
        for(RenderGUI child : pContextScroller.getChildren())
        {
            if(child.getType().equals("EditImageDescriptionCardAnswerEntry"))
            {
                EditImageDescriptionCardAnswerEntry entry = (EditImageDescriptionCardAnswerEntry)child;
                entry.setPosition(new ivec2(0, i * 40));
                entry.setIndex(i++);
            }
        }
        pAddEntryButton.setPosition(new ivec2(100, i * 40));
    }

    private void createAddButton()
    {
        pAddEntryButton = new Button(new ivec2(100, 40), new ivec2(30), "+", FontManager.getInstance().getDefaultFont());
        pAddEntryButton.setPositionInPercent(true, false);
        pAddEntryButton.getBox().setTextSize(28);
        pAddEntryButton.setOrigin(new ivec2(30, 0));
        pAddEntryButton.onClick((RenderGUI gui) -> addEntry("", new ivec2(0, 0)));
        pContextScroller.addGUI(pAddEntryButton);
    }

    /**
     * Fügt einen eintrag zur Antwortenliste hinzu
     *
     * @param answer Der Antworttext
     * @param pos    Die Position der Indexbox
     */
    public void addEntry(String answer, ivec2 pos)
    {
        EditImageDescriptionCardAnswerEntry entry = new EditImageDescriptionCardAnswerEntry(answer, pos, entry1 -> {
            pContextScroller.removeChild(entry1);
            pImageBox.removeChild(entry1.getIndexBox());
            reallignEntries();
        });

        pContextScroller.addGUI(entry);
        pImageBox.addGUI(entry.getIndexBox());
        reallignEntries();
    }

    private void applyChanges()
    {
        List<ImageDescriptionCardAnswer> answers = new ArrayList<>();
        for(RenderGUI child : pContextScroller.getChildren())
        {
            if(child.getType().equals("EditImageDescriptionCardAnswerEntry"))
            {
                EditImageDescriptionCardAnswerEntry entry = (EditImageDescriptionCardAnswerEntry)child;
                answers.add(new ImageDescriptionCardAnswer(entry.getAnswerString(), entry.getIndexBox().getUserDefinedPosition().x, entry.getIndexBox().getUserDefinedPosition().y, pCard));
            }
        }

        pCard.setAnswers(answers);
        PageManager.viewPage(PAGES.CARD_EDIT);
    }
}