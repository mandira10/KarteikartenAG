package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.textures.Texture;
import com.gumse.tools.Output;
import com.swp.DataModel.Card;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.FileDialog;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.PageManager.PAGES;

public class EditImageDescriptionCard extends EditCardGUI
{
    private Button pImageButton, pAnswersButton;
    private ImageDescriptionCard pCard;

    public EditImageDescriptionCard()
    {
        this.vSize = new ivec2(100,100);
        addGUI(XMLGUI.loadFile("guis/cards/edit/editimagedesccardpage.xml"));

        pImageButton = (Button)findChildByID("imagebox");
        pImageButton.getBox().getBox().invertTexcoordY(true);
        pImageButton.onClick((RenderGUI gui) -> { selectImageFile(); });

        pAnswersButton = (Button)findChildByID("answersbutton");
        pAnswersButton.onClick((RenderGUI gui) -> {
            EditImageDescriptionCardAnswersPage page = (EditImageDescriptionCardAnswersPage)PageManager.getPage(PAGES.CARD_IMAGE_ANSWERS);
            page.setCard(pCard);
            PageManager.viewPage(PAGES.CARD_IMAGE_ANSWERS);
        });

        
        this.setSizeInPercent(true, true);
        reposition();
    }


    private void selectImageFile()
    {
        String filepath = FileDialog.openFile("Select Image File", "Image Files", new String[] {"png", "jpg", "tga", "gif", "hdr"});

        if(filepath != null && !filepath.equals(""))
        {
            if(pCard.loadImageFile(filepath))
            {
                Texture loadTex = new Texture();
                loadTex.loadMemory(pCard.getImage());

                pImageButton.getBox().setTexture(loadTex);
                float aspect = (float)loadTex.getSize().x / (float)loadTex.getSize().y;
                int size = pImageButton.getSize().y;
                pImageButton.setSize(new ivec2((int)(size * aspect), size));
            }
        }
    }

    @Override 
    public void setCard(Card card) 
    {
        if(card.getType() != CardType.IMAGEDESC)
        {
            Output.error("Wrong card type given!");
            return;
        }
        
        this.pCard = (ImageDescriptionCard)card;
        if(pCard.getImage() != null)
        {
            Texture tex = new Texture();
            tex.loadMemory(pCard.getImage());
            pImageButton.getBox().setTexture(tex);
        }
    }

    @Override
    public boolean checkMandatory() 
    {
        if(pCard.getAnswers().size() == 0)
        {
            NotificationGUI.addNotification(Locale.getCurrentLocale().getString("mandatoryanswer"), NotificationType.WARNING, 5);
            return false;
        }
        return true;
    }
}
