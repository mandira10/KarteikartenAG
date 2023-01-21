package com.swp.GUI.Cards;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.TagList.TagList;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.EditCardPages.*;
import com.swp.GUI.Category.CategorySelectPage;
import com.swp.GUI.PageManager.PAGES;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;

public class EditCardPage extends Page
{
    private EditTextCard pEditTextCardPage;
    private EditTrueFalseCard pEditTrueFalseCardPage;
    private EditMultipleChoiceCard pEditMultipleChoiceCardPage;
    private EditImageTestCard pEditImageTestCardPage;
    private EditAudioCard pEditAudioCardPage;
    private EditImageDescriptionCard pEditImageDescriptionCardPage;

    private Card pNewCard;
    private List<Category> aCategories;
    private RenderGUI pCanvas;
    private TextField pTitlefield, pQuestionField;
    private TagList<Tag> pTagList;
    private TextBox pCategoriesBox;
    private boolean bIsNewCard;

    public EditCardPage()
    {
        super("Edit Card");
        this.vSize = new ivec2(100,100);
        this.pNewCard = null;
        this.aCategories = new ArrayList<>();


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
        applyButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  { applyChanges(); } 
        });

        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  { PageManager.viewLastPage(); } 
        });


        pTagList = (TagList<Tag>)findChildByID("tagslist");
        pCategoriesBox = (TextBox)findChildByID("categorylist");
        pCategoriesBox.setAlignment(Alignment.LEFT);
        pCategoriesBox.setAutoInsertLinebreaks(true);


        Button categoryButton = (Button)findChildByID("editcategoriesbutton");
        categoryButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  
            { 
                ((CategorySelectPage)PageManager.viewPage(PAGES.CATEGORY_SELECTION)).reset(); 
            } 
        });
        

        this.setSizeInPercent(true, true);
        reposition();
    }

    public void editCard(String uuid) {
        CardController.getInstance().getCardByUUID(uuid, new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card data) {
                editCard(data, false);
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

        }); }
    public void editCard(Card card, boolean newcard)
    {
        if(card == null)
            return;

        bIsNewCard = newcard;
        pNewCard = Card.copyCard(card);

        //Set data
        pTitlefield.setString(pNewCard.getTitle());
        pQuestionField.setString(pNewCard.getQuestion());
        CategoryController.getInstance().getCategoriesToCard(pNewCard, new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
                updateCategories(data);
            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

            @Override
            public void onInfo(String msg) {

            }
            
        });

        CardController.getInstance().getTagsToCard(pNewCard, new DataCallback<Tag>() {
            @Override
            public void onSuccess(List<Tag> data) {
                updateTags(data);
            }

            @Override
            public void onFailure(String msg) {
            NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

            @Override
            public void onInfo(String msg) {

            }
        });

        switch(pNewCard.getType())
        {
            case TRUEFALSE:      setPage(pEditTrueFalseCardPage);        break;
            case IMAGETEST:      setPage(pEditImageTestCardPage);        break;
            case IMAGEDESC:      
                setPage(pEditImageDescriptionCardPage); 
                pEditImageDescriptionCardPage.setCard((ImageDescriptionCard)pNewCard); 
                break;
            case MULITPLECHOICE: 
                setPage(pEditMultipleChoiceCardPage);   
                pEditMultipleChoiceCardPage.setCard((MultipleChoiceCard)pNewCard);
                break;
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

    public void updateCategories(List<Category> categories)
    {
        this.aCategories = categories;
        String catString = "";
        for(Category category : categories)
            catString += category.getName() + ", ";
        if(catString.length() > 0)
            catString = catString.substring(0, catString.length() - 2);
        pCategoriesBox.setString(catString);
    }

    public void updateTags(List<Tag> tags)
    {
        for(Tag tag : tags)
            pTagList.addTag(tag.getVal(), tag);
    }

    private void applyChanges()
    {
        CardController.getInstance().updateCardData(pNewCard, bIsNewCard, new SingleDataCallback<Boolean>() {
            @Override public void onSuccess(Boolean data) {}

            @Override
            public void onFailure(String msg) {
            NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });

        CardController.getInstance().setTagsToCard(pNewCard, pTagList.getTagUserptrs(), new SingleDataCallback<Boolean>() {
            @Override public void onSuccess(Boolean data) {}

            @Override
            public void onFailure(String msg) {
            NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });

        CategoryController.getInstance().setCategoriesToCard(pNewCard, aCategories, new SingleDataCallback<Boolean>() {
            @Override public void onSuccess(Boolean data) {}

            @Override
            public void onFailure(String msg) {
            NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }
}