package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.DataModel.Card;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.GUI.Extras.AudioGUI;
import com.swp.GUI.Extras.FileDialog;

import java.io.FileInputStream;

public class EditAudioCard extends EditCardGUI
{
    private Button pUploadButton;
    private AudioCard pCard;
    private AudioGUI pAudioGUI;

    public EditAudioCard()
    {
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/editaudiocardpage.xml"));

        RenderGUI audioGUIContainer = findChildByID("audio");

        //InputStream stream = EditAudioCard.class.getClassLoader().getResourceAsStream("audios/thud.wav");
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
            pAudioGUI.loadAudio((FileInputStream)null);

            pCard.setAudio(null);
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
        pAudioGUI.loadAudio(pCard.getAudio());
    }

    @Override
    public boolean checkMandatory() 
    {
        return true;
    }
}