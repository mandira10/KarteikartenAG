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
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardSelectPage;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Category.CategorySelectPage;

public class EditReferencesEntry extends RenderGUI
{
    public interface RemoveReferenceEntryCallback
    {
        public void run(EditReferencesEntry entry);
    }

    private TextField pDescriptionField;
    private TextField pReferenceField;
    private TextBox pReferenceBox;
    private Button pTrashButton;
    private Dropdown pTypeDropdown;
    private String sDestination;
    
    public EditReferencesEntry(String str, RemoveReferenceEntryCallback callback, GUICallback onclick)
    {
        this.sType = "EditReferencesEntry";
        this.vSize.set(new ivec2(100, 30));

        EditReferencesEntry thisentry = this;
        pTypeDropdown = new Dropdown("Type", FontManager.getInstance().getDefaultFont(), new ivec2(0, 0), new ivec2(100, 30), 25);
        pTypeDropdown.onClick((RenderGUI gui) -> { onclick.run(thisentry); pTypeDropdown.Switch(); });
        pTypeDropdown.addEntry("Card",     (String name) -> { createTextBox(true);  }, false);
        pTypeDropdown.addEntry("Category", (String name) -> { createTextBox(false); }, false);
        pTypeDropdown.addEntry("File",     (String name) -> { createTextField();    }, false);
        pTypeDropdown.addEntry("Website",  (String name) -> { createTextField();    }, false);
        addElement(pTypeDropdown);

        pDescriptionField = new TextField("", FontManager.getInstance().getDefaultFont(), new ivec2(110, 40), new ivec2(100, 30));
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
        
        this.setSizeInPercent(true, true);
        reposition();
    }

    private void createTextField()
    {
        removeChild(pReferenceBox);
        removeChild(pReferenceField);

        pReferenceField = new TextField("", FontManager.getInstance().getDefaultFont(), new ivec2(110, 0), new ivec2(100, 30));
        pReferenceField.setSizeInPercent(true, false);
        pReferenceField.setMargin(new ivec2(-150, 0));
        addGUI(pReferenceField);
    }

    private void createTextBox(boolean cardcategory)
    {
        removeChild(pReferenceBox);
        removeChild(pReferenceField);

        pReferenceBox = new TextBox("", FontManager.getInstance().getDefaultFont(), new ivec2(110, 0), new ivec2(100, 30));
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