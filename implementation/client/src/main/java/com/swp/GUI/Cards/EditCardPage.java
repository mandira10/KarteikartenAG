package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Locale;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.TagList.TagList;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.GUI.Cards.EditCardPages.*;
import com.swp.GUI.Category.CategorySelectPage;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.References.EditReferencesPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Seite welche es einem ermöglicht, Karten zu bearbeiten
 * 
 * @author Tom Beuke
 */
public class EditCardPage extends Page
{
    private EditCardGUI pEditCard;

    private Card pNewCard;
    private List<Category> aCategories;
    private final RenderGUI pCanvas;
    private final TextField pTitlefield;
    private final TextField pQuestionField;
    private final TagList<Tag> pTagList;
    private final TextBox pCategoriesBox;
    private boolean bIsNewCard;

    /**
     * Der Standardkonstruktor der Klasse EditCardPage
     */
    public EditCardPage()
    {
        super("Edit Card", "editcardpage");
        this.vSize = new ivec2(100,100);
        this.pNewCard = null;
        this.aCategories = new ArrayList<>();
        
        addGUI(XMLGUI.loadFile("guis/cards/cardeditpage.xml"));

        pCanvas = findChildByID("canvas");

        pTitlefield = (TextField)findChildByID("titlefield");
        pTitlefield.setCallback(new TextFieldInputCallback() { 
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) {
                pNewCard.setTitle(complete); 
            } 
        });

        pQuestionField = (TextField)findChildByID("questionfield");
        pQuestionField.setCallback(new TextFieldInputCallback() { 
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) {
                pNewCard.setQuestion(complete); 
            } 
        });

        Button applyButton = (Button)findChildByID("applybutton");
        applyButton.onClick((RenderGUI gui) -> { applyChanges(); });

        Button referencesButton = (Button)findChildByID("referencesbutton");
        referencesButton.onClick((RenderGUI gui) -> { ((EditReferencesPage)PageManager.viewPage(PAGES.REFERENCES_EDIT_PAGE)).setCard(pNewCard); });

        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> { PageManager.viewLastPage(); });


        RenderGUI taglistcontainer = findChildByID("tagslist");
        pTagList = new TagList<>(new ivec2(0, 0), new ivec2(100, 30), FontManager.getInstance().getDefaultFont(), (String name) -> {
            return new Tag(name);
        });
        pTagList.setSizeInPercent(true, false);
        pTagList.setLocaleID("cardtaglist");
        taglistcontainer.addGUI(pTagList);

        pCategoriesBox = (TextBox)findChildByID("categorylist");
        pCategoriesBox.setAlignment(Alignment.LEFT);
        pCategoriesBox.setAutoInsertLinebreaks(true);


        Button categoryButton = (Button)findChildByID("editcategoriesbutton");
        categoryButton.onClick((RenderGUI gui) -> { 
            ((CategorySelectPage)PageManager.viewPage(PAGES.CATEGORY_SELECTION)).reset(false, (categories -> {
                ((EditCardPage)PageManager.viewPage(PAGES.CARD_EDIT)).updateCategories(categories);
            }), aCategories);
        });
        

        this.setSizeInPercent(true, true);
        reposition();
    }

    /**
     * Bearbeitet eine Karte anhand ihrer UUID
     *
     * @param uuid Die UUID einer Karte
     */
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

        }); 
    }

    /**
     * Übergibt eine zu bearbeitende Karte
     *
     * @param card    Eine Karte
     * @param newcard Ist die übergebene Karte neu?
     */
    public void editCard(Card card, boolean newcard)
    {
        if(card == null)
            return;

        bIsNewCard = newcard;
        pNewCard = card;

        //Set data
        pTitlefield.setString(pNewCard.getTitle());
        pQuestionField.setString(pNewCard.getQuestion());
        
        pCategoriesBox.setString("");
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

        pTagList.reset();
        CardController.getInstance().getTagsToCard(pNewCard, new DataCallback<Tag>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Tag> data) {
                updateTags(data);
            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });

        pCanvas.destroyChildren();
        switch(pNewCard.getType())
        {
            case TRUEFALSE:      pEditCard = new EditTrueFalseCard();        break;
            case IMAGETEST:      pEditCard = new EditImageTestCard();        break;
            case IMAGEDESC:      pEditCard = new EditImageDescriptionCard(); break;
            case MULITPLECHOICE: pEditCard = new EditMultipleChoiceCard();   break;
            case TEXT:           pEditCard = new EditTextCard();             break;
            case AUDIO:          pEditCard = new EditAudioCard();            break;
        }
        pEditCard.setCard(card);
        pCanvas.addGUI(pEditCard);
        pCanvas.updateTheme();
        resize();
    }

    /**
     * Aktualisiert die Kategorie-Einträge der zu bearbeitenden Karte
     *
     * @param categories Die kategorien
     */
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

    /**
     * Aktualisiert die Tag-Einträge der zu bearbeitenden Karte
     *
     * @param tags Die tags
     */
    public void updateTags(List<Tag> tags)
    {
        for(Tag tag : tags)
            pTagList.addTag(tag.getVal(), tag);
    }

    private void applyChanges()
    {
        if(pQuestionField.getString().isBlank())
        {
            NotificationGUI.addNotification(Locale.getCurrentLocale().getString("mandatoryquestion"), NotificationType.WARNING, 5);
            return;
        }

        if(!pEditCard.checkMandatory())
            return;
        
        CardController.getInstance().updateCardData(pNewCard, bIsNewCard, new SingleDataCallback<Boolean>() {
            @Override public void onSuccess(Boolean data) 
            {
                CardController.getInstance().setTagsToCard(pNewCard, pTagList.getTagUserptrs(), new SingleDataCallback<Boolean>() {
                    @Override public void onSuccess(Boolean data) 
                    {
                        CategoryController.getInstance().setCategoriesToCard(pNewCard, aCategories, new SingleDataCallback<Boolean>() {
                            @Override public void onSuccess(Boolean data)
                            {
                                ((ViewSingleCardPage)PageManager.viewPage(PAGES.CARD_SINGLEVIEW)).setCard(pNewCard, PAGES.CARD_EDIT);
                            }
                
                            @Override public void onFailure(String msg) 
                            {
                                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                            }
                        });
                    }
        
                    @Override public void onFailure(String msg) 
                    {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });
            }

            @Override public void onFailure(String msg) 
            {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }
}