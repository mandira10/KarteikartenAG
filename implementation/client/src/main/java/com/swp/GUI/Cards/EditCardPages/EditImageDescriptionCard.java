package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.textures.Texture;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.FileDialog;
import com.swp.GUI.PageManager.PAGES;

public class EditImageDescriptionCard extends RenderGUI
{
    private Button pImageButton, pAnswersButton;
    private ImageDescriptionCard pCard;

    public EditImageDescriptionCard()
    {
        this.vSize = new ivec2(100,100);
        addGUI(XMLGUI.loadFile("guis/cards/edit/editimagedesccardpage.xml"));

        pImageButton = (Button)findChildByID("imagebox");

        pImageButton.getBox().getBox().invertTexcoordY(true);
        pImageButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                selectImageFile();
            }
        });

        pAnswersButton = (Button)findChildByID("answersbutton");
        pAnswersButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                EditImageDescriptionCardAnswersPage page = (EditImageDescriptionCardAnswersPage)PageManager.getPage(PAGES.CARD_IMAGE_ANSWERS);
                page.setCard(pCard);
                PageManager.viewPage(PAGES.CARD_IMAGE_ANSWERS);
            }
        });

        
        this.setSizeInPercent(true, true);
        reposition();
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

            pCard.setImage("TODO");
        }
    }


    public void setCard(ImageDescriptionCard card)
    {
        this.pCard = card;
        if(!card.getImage().equals(""))
        {
            Texture tex = new Texture();
            tex.load(card.getImage(), getClass());
            pImageButton.getBox().setTexture(tex);
            //pImageButton.getBox().setColor(new vec4(1, 1, 1, 1));
        }
    }
}
