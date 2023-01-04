package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.GUI.Extras.AudioGUI;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

import java.io.InputStream;

public class EditAudioCard extends RenderGUI
{
    private Button pUploadButton;

    public EditAudioCard()
    {
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/editaudiocardpage.xml"));

        RenderGUI audioGUIContainer = findChildByID("audio");

        //InputStream stream = EditAudioCard.class.getClassLoader().getResourceAsStream("audios/thud.wav");
        AudioGUI audioGUI = new AudioGUI(new ivec2(0, 0), new ivec2(100, 100), null);
        audioGUI.setSizeInPercent(true, true);
        audioGUIContainer.addGUI(audioGUI);

        pUploadButton = (Button)findChildByID("audioupload");
        pUploadButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                selectAudioFile();
            } 
        });
        
        this.setSizeInPercent(true, true);
        reposition();
    }


    private void selectAudioFile()
    {
        System.out.println("\nOpening file open dialog...");
        System.out.println(tinyfd_openFileDialog("Open File(s)", "", null, null, true));
    }
}