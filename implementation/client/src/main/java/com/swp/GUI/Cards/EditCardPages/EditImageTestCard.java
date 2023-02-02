package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.textures.Texture;
import com.gumse.tools.Output;
import com.swp.DataModel.Card;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.GUI.Extras.FileDialog;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;

public class EditImageTestCard extends EditCardGUI
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
        if(card.getType() != CardType.IMAGETEST)
        {
            Output.error("Wrong card type given!");
            return;
        }
        
        this.pCard = (ImageTestCard)card;
        if(pCard.getImage() != null)
        {
            Texture loadTex = new Texture();
            loadTex.loadMemory(pCard.getImage());
            pImageButton.getBox().setTexture(loadTex);
        }
        pAnswerField.setString(pCard.getAnswer());
    }

    @Override
    public boolean checkMandatory() 
    {
        if(pAnswerField.getString().isEmpty())
        {
            NotificationGUI.addNotification(Locale.getCurrentLocale().getString("mandatoryanswer"), NotificationType.WARNING, 5);
            return false;
        }
        return true;
    }
}