package com.swp.GUI.Cards.EditCardPages;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.textures.Texture;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.GUI.Extras.FileDialog;

public class EditImageTestCard extends RenderGUI
{
    private Button pImageButton;
    private TextField pAnswerField;
    private ImageTestCard pCard;

    public EditImageTestCard()
    {
        this.vSize = new ivec2(100,100);

        //pRatingGUI = new RatingGUI(card);
        addGUI(XMLGUI.loadFile("guis/cards/edit/editimagetestcardpage.xml"));

        pImageButton = (Button)findChildByID("imagebox");
        pImageButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                selectImageFile();
            }
        });


        pAnswerField = (TextField)findChildByID("answerfield");
        pAnswerField.setCallback(new TextFieldInputCallback() { 
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            {
                pCard.setAnswer(complete);
            } 
        });

        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }


    private void selectImageFile()
    {
        String filepath = FileDialog.openFile("Select Image File", "Image Files", new String[] {"png", "jpg", "tga", "gif", "hdr"});

        if(filepath != null && !filepath.equals(""))
        {
            Texture loadTex = new Texture();
            if(loadTex.loadFile(filepath, getClass()))
            {
                pImageButton.getBox().setTexture(loadTex);
                float aspect = (float)loadTex.getSize().x / (float)loadTex.getSize().y;
                int size = pImageButton.getSize().y;
                pImageButton.setSize(new ivec2((int)(size * aspect), size));
            }

            pCard.setImage(filepath);
        }
    }

    public void setCard(ImageTestCard card)
    {
        this.pCard = card;
        pAnswerField.setString(pCard.getAnswer());
    }
}