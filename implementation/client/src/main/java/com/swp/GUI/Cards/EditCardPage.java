package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
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
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.EditCardPages.EditAudioCardPage;
import com.swp.GUI.Cards.EditCardPages.EditImageTestCardPage;
import com.swp.GUI.Cards.EditCardPages.EditMultipleChoiceCardPage;
import com.swp.GUI.Cards.EditCardPages.EditTextCardPage;
import com.swp.GUI.Cards.EditCardPages.EditTrueFalseCardPage;
import com.swp.GUI.Extras.ListTextField;

public class EditCardPage extends Page
{
    private EditTextCardPage pEditTextCardPage;
    private EditTrueFalseCardPage pEditTrueFalseCardPage;
    private EditMultipleChoiceCardPage pEditMultipleChoiceCardPage;
    private EditImageTestCardPage pEditImageTestCardPage;
    private EditAudioCardPage pEditAudioCardPage;

    private Dropdown pDropdown;
    private ListTextField pListTextField;
    private Card pOldCard, pNewCard;
    private RenderGUI pCanvas;
    private TextField pTitlefield;

    public EditCardPage()
    {
        super("Edit Card");
        this.vSize = new ivec2(100,100);
        pOldCard = null;
        pNewCard = null;
        pListTextField = new ListTextField();
        //oDropdown = new Dropdown(null, null, null, null, 0)


        addGUI(XMLGUI.loadFile("guis/cards/cardeditpage.xml"));

        pCanvas = findChildByID("canvas");
        
        pEditTextCardPage = new EditTextCardPage();
        pCanvas.addGUI(pEditTextCardPage);

        pEditTrueFalseCardPage = new EditTrueFalseCardPage();
        pCanvas.addGUI(pEditTrueFalseCardPage);

        pEditMultipleChoiceCardPage = new EditMultipleChoiceCardPage();
        pCanvas.addGUI(pEditMultipleChoiceCardPage);

        pEditImageTestCardPage = new EditImageTestCardPage();
        pCanvas.addGUI(pEditImageTestCardPage);

        pEditAudioCardPage = new EditAudioCardPage();
        pCanvas.addGUI(pEditAudioCardPage);

        pTitlefield = (TextField)findChildByID("titlefield");
<<<<<<< HEAD
        pTitlefield.setCallback(new TextFieldInputCallback() { 
            @Override
            public void enter(String complete) 
            {
                pNewCard.setSTitle(complete); 
                
            }

            @Override
            public void input(String input, String complete) 
            {
            } 
        });
=======
        pTitlefield.setReturnCallback(new TextFieldFinishedInputCallback() { @Override public void run(String str) { pNewCard.setTitle(str); } });
>>>>>>> f1acf7301c620cd11eca8d45ea8785dc15c03675

        Button applyButton = (Button)findChildByID("applybutton");
        if(applyButton != null)
            applyButton.setCallbackFunction(new ButtonCallback() { @Override public void run() { applyChanges(); } });

        Button cancelButton = (Button)findChildByID("cancelbutton");
        if(cancelButton != null)
            cancelButton.setCallbackFunction(new ButtonCallback() { @Override public void run() { PageManager.viewLastPage(); } });


        TagList tagList = (TagList)findChildByID("tagslist");
        tagList.addTag("Tag1");
        tagList.addTag("Testing");
        tagList.addTag("AnotherTag");


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

        switch(pNewCard.getType())
        {
            case TRUEFALSE:      setPage(pEditTrueFalseCardPage); break;
            case IMAGETEST:      setPage(pEditImageTestCardPage); break;
            case IMAGEDESC:      setPage(pEditTextCardPage); break;
            case MULITPLECHOICE: setPage(pEditMultipleChoiceCardPage); break;
            case TEXT:           setPage(pEditTextCardPage); break;
            case AUDIO:          setPage(pEditAudioCardPage); break;
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