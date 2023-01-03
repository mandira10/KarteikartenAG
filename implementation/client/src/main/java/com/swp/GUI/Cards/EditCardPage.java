package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.TagList.TagList;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.EditCardPages.*;

public class EditCardPage extends Page
{
    private EditTextCard pEditTextCardPage;
    private EditTrueFalseCard pEditTrueFalseCardPage;
    private EditMultipleChoiceCard pEditMultipleChoiceCardPage;
    private EditImageTestCard pEditImageTestCardPage;
    private EditAudioCard pEditAudioCardPage;
    private EditImageDescriptionCard pEditImageDescriptionCardPage;

    private Card pOldCard, pNewCard;
    private RenderGUI pCanvas;
    private TextField pTitlefield, pQuestionField;

    public EditCardPage()
    {
        super("Edit Card");
        this.vSize = new ivec2(100,100);
        pOldCard = null;
        pNewCard = null;
        //oDropdown = new Dropdown(null, null, null, null, 0)


        addGUI(XMLGUI.loadFile("guis/cards/cardeditpage.xml"));

        pCanvas = findChildByID("canvas");
        
        pEditTextCardPage = new EditTextCard();
        pCanvas.addGUI(pEditTextCardPage);

        pEditTrueFalseCardPage = new EditTrueFalseCard();
        pCanvas.addGUI(pEditTrueFalseCardPage);

        pEditMultipleChoiceCardPage = new EditMultipleChoiceCard();
        pCanvas.addGUI(pEditMultipleChoiceCardPage);

        pEditImageTestCardPage = new EditImageTestCard();
        pCanvas.addGUI(pEditImageTestCardPage);

        pEditImageDescriptionCardPage = new EditImageDescriptionCard();
        pCanvas.addGUI(pEditImageDescriptionCardPage);

        pEditAudioCardPage = new EditAudioCard();
        pCanvas.addGUI(pEditAudioCardPage);

        pTitlefield = (TextField)findChildByID("titlefield");
        pTitlefield.setCallback(new TextFieldInputCallback() { 
            @Override
            public void enter(String complete) 
            {
                pNewCard.setTitle(complete); 
            }

            @Override
            public void input(String input, String complete) 
            {
            } 
        });

        pQuestionField = (TextField)findChildByID("questionfield");
        pQuestionField.setCallback(new TextFieldInputCallback() { 
            @Override
            public void enter(String complete) 
            {
                pNewCard.setQuestion(complete); 
            }

            @Override
            public void input(String input, String complete) 
            {
            } 
        });

        Button applyButton = (Button)findChildByID("applybutton");
        if(applyButton != null)
            applyButton.setCallbackFunction(new ButtonCallback() { @Override public void run() { applyChanges(); } });

        Button cancelButton = (Button)findChildByID("cancelbutton");
        if(cancelButton != null)
            cancelButton.setCallbackFunction(new ButtonCallback() { @Override public void run() { PageManager.viewLastPage(); } });


        TagList tagList = (TagList)findChildByID("tagslist");
        tagList.addTag("ket");
        tagList.addTag("orange");
        tagList.addTag("important");


        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void editCard(String uuid) { editCard(CardController.getCardByUUID(uuid)); }
    public void editCard(Card card)
    {
        if(card == null)
            return;
        
        pOldCard = card;
        pNewCard = Card.copyCard(card); //TODO: lieber keine Kopie sondern Hibernate update Methode verwenden?

        pTitlefield.setString(pNewCard.getTitle());
        pQuestionField.setString(pNewCard.getQuestion());

        switch(pNewCard.getType())
        {
            case TRUEFALSE:      setPage(pEditTrueFalseCardPage);        break;
            case IMAGETEST:      setPage(pEditImageTestCardPage);        break;
            case IMAGEDESC:      
                setPage(pEditImageDescriptionCardPage); 
                pEditImageDescriptionCardPage.setCard((ImageDescriptionCard)pNewCard); 
                break;
            case MULITPLECHOICE: setPage(pEditMultipleChoiceCardPage);   break;
            case TEXT:           setPage(pEditTextCardPage);             break;
            case AUDIO:          setPage(pEditAudioCardPage);            break;
        }
    }

    private void setPage(RenderGUI page)
    {
        for(RenderGUI child : pCanvas.getChildren())
            child.hide(true);
        page.hide(false);
    }

    private void deleteCard()
    {
        CardController.deleteCard(pOldCard);
    }

    private void applyChanges()
    {
        Debug.info("Applying changes");
      //  CardController.updateCardData(pOldCard, pNewCard);
    }
}