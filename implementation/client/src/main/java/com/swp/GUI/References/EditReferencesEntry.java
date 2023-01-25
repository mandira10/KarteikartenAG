package com.swp.GUI.References;


import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.tools.Output;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardSelectPage;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Category.CategorySelectPage;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;

public class EditReferencesEntry extends RenderGUI
{
    public interface RemoveReferenceEntryCallback
    {
        public void run(EditReferencesEntry entry);
    }

    private enum TYPE 
    {
        CARD,
        CATEGORY,
        FILE,
        WEB
    }

    private TextField pDescriptionField;
    private TextField pReferenceField;
    private TextBox pReferenceBox;
    private Button pTrashButton;
    private Dropdown pTypeDropdown;
    private String sDestination;
    private TYPE iType;
    
    public EditReferencesEntry(String str, RemoveReferenceEntryCallback callback, GUICallback onclick)
    {
        this.sType = "EditReferencesEntry";
        this.vSize.set(new ivec2(100, 30));

        String type = "";
        sDestination = "";
        String description = "";
        if(!str.isEmpty())
        {
            String[] args = str.split(";");
            if(args.length < 3)
                return;
            
            type = args[0].replaceAll("\\s", "");
            sDestination = args[1];
            description = args[2];
        }

        switch(type)
        {
            case "crd": iType = TYPE.CARD;     break;
            case "ctg": iType = TYPE.CATEGORY; break;
            case "fil": iType = TYPE.FILE;     break;
            case "htm": iType = TYPE.WEB;      break;
        }

        EditReferencesEntry thisentry = this;
        pTypeDropdown = new Dropdown("Type", FontManager.getInstance().getDefaultFont(), new ivec2(0, 0), new ivec2(100, 30), 25);
        pTypeDropdown.onClick((RenderGUI gui) -> { onclick.run(thisentry); pTypeDropdown.Switch(); });
        pTypeDropdown.addEntry("Card",     (String name) -> { createTextBox(true, "");  iType = TYPE.CARD;     }, iType == TYPE.CARD);
        pTypeDropdown.addEntry("Category", (String name) -> { createTextBox(false, ""); iType = TYPE.CATEGORY; }, iType == TYPE.CATEGORY);
        pTypeDropdown.addEntry("File",     (String name) -> { createTextField("");      iType = TYPE.FILE;     }, iType == TYPE.FILE);
        pTypeDropdown.addEntry("Website",  (String name) -> { createTextField("");      iType = TYPE.WEB;      }, iType == TYPE.WEB);
        addElement(pTypeDropdown);

        pDescriptionField = new TextField(description, FontManager.getInstance().getDefaultFont(), new ivec2(110, 40), new ivec2(100, 30));
        pDescriptionField.setHint("Description");
        pDescriptionField.setSizeInPercent(true, false);
        pDescriptionField.setMargin(new ivec2(-150, 0));
        addElement(pDescriptionField);

        pTrashButton = new Button(new ivec2(100, 0), new ivec2(30, 30), "", FontManager.getInstance().getFont("FontAwesomeRegular"));
        pTrashButton.getBox().setTextSize(15);
        pTrashButton.getBox().setCornerRadius(new vec4(0, 0, 0, 0));
        pTrashButton.setPositionInPercent(true, false);
        pTrashButton.setOrigin(new ivec2(30, 0));
        this.pTrashButton.onClick((RenderGUI gui) -> { callback.run(thisentry); });
        addElement(pTrashButton);

        if(iType == TYPE.CARD)
        {
            createTextBox(true, "");
            CardController.getInstance().getCardByUUID(sDestination, new SingleDataCallback<Card>() {
                @Override public void onSuccess(Card card) {
                    createTextBox(true, card.getTitle());
                }

                @Override public void onFailure(String msg) {
                    NotificationGUI.addNotification(msg, NotificationType.WARNING, 5);
                }
            });
        }
        else if(iType == TYPE.CATEGORY)
        {
            createTextBox(false, "");
            CategoryController.getInstance().getCategoryByUUID(sDestination, new SingleDataCallback<Category>() {
                @Override public void onSuccess(Category category) {
                    createTextBox(false, category.getName());
                }

                @Override public void onFailure(String msg) {
                    NotificationGUI.addNotification(msg, NotificationType.WARNING, 5);
                }
            });
        }
        else
        {
            createTextField(sDestination);
        }

        this.setSizeInPercent(true, true);
        reposition();
    }

    private void createTextField(String str)
    {
        removeChild(pReferenceBox);
        removeChild(pReferenceField);

        pReferenceBox = null;

        pReferenceField = new TextField(str, FontManager.getInstance().getDefaultFont(), new ivec2(110, 0), new ivec2(100, 30));
        pReferenceField.setSizeInPercent(true, false);
        pReferenceField.setMargin(new ivec2(-150, 0));
        addGUI(pReferenceField);
    }

    private void createTextBox(boolean cardcategory, String str)
    {
        removeChild(pReferenceBox);
        removeChild(pReferenceField);

        pReferenceField = null;

        pReferenceBox = new TextBox(str, FontManager.getInstance().getDefaultFont(), new ivec2(110, 0), new ivec2(100, 30));
        pReferenceBox.setSizeInPercent(true, false);
        pReferenceBox.setMargin(new ivec2(-150, 0));
        pReferenceBox.setAlignment(Alignment.LEFT);
        pReferenceBox.onClick((RenderGUI gui) -> {
            
            if(cardcategory) //Open card select gui
            {
                ((CardSelectPage)PageManager.viewPage(PAGES.CARD_SELECT)).reset((card -> {
                    PageManager.viewPage(PAGES.REFERENCES_EDIT_PAGE);
                    pReferenceBox.setString(card.getTitelToShow());
                    sDestination = card.getUUUID();
                    Output.info(sDestination);
                }));
            } 
            else //Open category select gui      
            { 
                ((CategorySelectPage)PageManager.viewPage(PAGES.CATEGORY_SELECTION)).reset(true, (categories -> {
                    PageManager.viewPage(PAGES.REFERENCES_EDIT_PAGE);
                    if(categories.size() > 0)
                    {
                        pReferenceBox.setString(categories.get(0).getName());
                        sDestination = categories.get(0).getUuid();
                        Output.info(sDestination);
                    }
                }), null);
            }
        });
        addGUI(pReferenceBox);
    }

    public void closeDropdown()
    {
        pTypeDropdown.close();
    }

    @Override
    public String toString()
    {
        String retStr = "";
        String destination = "";
        String description = pDescriptionField.getString();
        
        if(pReferenceField != null) { destination = pReferenceField.getString(); }
        else                        { destination = sDestination;   }
        switch(iType)
        {
            case CARD:     retStr = "crd;" + destination + ";" + description; break;
            case CATEGORY: retStr = "ctg;" + destination + ";" + description; break;
            case FILE:     retStr = "fil;" + destination + ";" + description; break;
            case WEB:      retStr = "htm;" + destination + ";" + description; break;
        }

        return retStr;
    }

    //
    // Getter
    //
    public String getReferenceString() 
    { 
        if(pReferenceBox != null)
            return pReferenceBox.getString();
        else if(pReferenceField != null)
            return pReferenceField.getBox().getString(); 
        return "";
    }
}