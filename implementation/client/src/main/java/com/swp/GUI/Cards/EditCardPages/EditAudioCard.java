package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.DataModel.Card;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.GUI.Extras.AudioGUI;
import com.swp.GUI.Extras.FileDialog;

/**
 * Wird verwendet, um Audiokarten zu bearbeiten
 * 
 * @author Tom Beuke
 */
public class EditAudioCard extends EditCardGUI
{
    private final Button pUploadButton;
    private AudioCard pCard;
    private final AudioGUI pAudioGUI;
    private final TextField pAnswerField;

    /**
     * Der Standardkonstruktor für die Klasse EditAudioCard
     */
    public EditAudioCard()
    {
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/editaudiocardpage.xml"));

        RenderGUI audioGUIContainer = findChildByID("audio");
        pAnswerField = (TextField)findChildByID("answerfield");
        pAnswerField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            {
                pCard.setAnswer(complete);
            }
        });

        
        pAudioGUI = new AudioGUI(new ivec2(0, 0), new ivec2(100, 100));
        pAudioGUI.setSizeInPercent(true, true);
        audioGUIContainer.addGUI(pAudioGUI);

        pUploadButton = (Button)findChildByID("audioupload");
        pUploadButton.onClick((RenderGUI gui) -> { selectAudioFile(); });
        
        this.setSizeInPercent(true, true);
        reposition();
    }


    private void selectAudioFile()
    {
        String filepath = FileDialog.openFile("Select Audio File", "Audio Files", new String[] {"wav"});

        if(filepath != null && !filepath.equals(""))
        {
            if(pCard.loadAudioFile(filepath))
            {
                pAudioGUI.loadAudio(pCard.getAudio());
                pAudioGUI.stop();
            }
        }
    }

    @Override 
    public void setCard(Card card) 
    {
        if(card.getType() != CardType.AUDIO)
        {
            Output.error("Wrong card type given!");
            return;
        }
        
        pCard = (AudioCard)card;
        if(pCard.getAudio() != null)
            pAudioGUI.loadAudio(pCard.getAudio());
        pAudioGUI.stop();

        pAnswerField.setString(pCard.getAnswer());
    }

    @Override
    public boolean checkMandatory() 
    {
        return true;
    }
}