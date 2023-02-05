package com.swp.GUI.Extras;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Radiobutton;
import com.gumse.gui.Basics.Speechbubble;
import com.gumse.gui.Basics.Speechbubble.Side;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.GUI;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI.XMLGUICreator;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.filesystem.XML.XMLNode;

/**
 * Searchbar wird von den Overviewpages 
 * verwendet um nach Karten/Kategorien/Decks 
 * zu suchen.
 * 
 * @author Tom Beuke
 */
public class Searchbar extends RenderGUI
{
    /**
     * Wird ausgeführt, wenn eine Suche getätigt wurde abgegeben wurde
     */
    public interface SearchbarCallback
    {
        /**
         * @param query  Der Suchbegriff
         * @param option Die Suchoptionen
         */
        void run(String query, int option);
    }

    private final TextField pInputField;
    private final Button pSubmitButton;
    private final Radiobutton pSearchOptions;
    private SearchbarCallback pCallback;
    private Speechbubble pBubble;
    private int iCurrentSearchOption;

    /**
     * Der Hauptkonstruktor der Klasse Searchbar
     *
     * @param pos             Position des GUIs in Pixeln
     * @param size            Größe des GUIs in Pixeln
     * @param localeid        Die Locale ID der Suchleiste für andere Sprachen
     * @param optionlocaleids Die Locale IDs der Suchoptionen für andere Sprachen
     */
    public Searchbar(ivec2 pos, ivec2 size, String localeid, String[] optionlocaleids)
    {
        this.sType = "Searchbar";
        this.vPos.set(pos);
        this.vSize.set(size);
        this.iCurrentSearchOption = 0;

        FontManager fonts = FontManager.getInstance();
        pInputField = new TextField("", fonts.getDefaultFont(), new ivec2(0, 0), new ivec2(100, 100));
        pInputField.setSizeInPercent(true, true);
        pInputField.setMargin(new ivec2(-size.y * 3, 0));
        pInputField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) 
            {
                pCallback.run(complete, iCurrentSearchOption);
            }

            @Override public void input(String input, String complete) 
            {
            }
        });
        pInputField.setCornerRadius(new vec4(GUI.getTheme().cornerRadius.x, 0, 0, GUI.getTheme().cornerRadius.w));
        pInputField.setLocaleID(localeid);
        addElement(pInputField);


        Button pOptionsButton = new Button(new ivec2(100, 0), new ivec2(size.y, size.y), "", fonts.getFont("FontAwesome"));
        pOptionsButton.setOrigin(new ivec2(size.y * 3, 0));
        pOptionsButton.setPositionInPercent(true, false);
        pOptionsButton.getBox().setTextSize(20);
        pOptionsButton.setCornerRadius(new vec4(0,0,0,0));
        pOptionsButton.onClick((RenderGUI gui) -> pBubble.show());
        addElement(pOptionsButton);

        pBubble = new Speechbubble(new ivec2(size.y / 2, 0), new ivec2(100, 120), Side.ABOVE);
        pBubble.setColor(vec4.sub(GUI.getTheme().primaryColor, 0.06f));
        pBubble.onClick((RenderGUI gui) -> { /*Prevent clickthrough*/ });
        pBubble.hide(true);
        pOptionsButton.addGUI(pBubble);

        pSearchOptions = new Radiobutton(new ivec2(20, 20), 150, fonts.getDefaultFont(), 20);
        pSearchOptions.singleSelect(true);
        pSearchOptions.setSizeInPercent(true, false);
        pBubble.addGUI(pSearchOptions);
        for(String locale : optionlocaleids)
            pSearchOptions.addOption("", locale, "");
        pSearchOptions.select(iCurrentSearchOption);
        pSearchOptions.onSelect((int index, String content) -> iCurrentSearchOption = index);
        pBubble.setSize(new ivec2(200, pSearchOptions.getSize().y + 30));

        pSubmitButton = new Button(new ivec2(100, 0), new ivec2(size.y * 2, size.y), "", fonts.getFont("FontAwesome"));
        pSubmitButton.setOrigin(new ivec2(size.y * 2, 0));
        pSubmitButton.setPositionInPercent(true, false);
        pSubmitButton.setCornerRadius(new vec4(0, GUI.getTheme().cornerRadius.y, GUI.getTheme().cornerRadius.z, 0));
        pSubmitButton.getBox().setTextSize(20);
        pSubmitButton.onClick((RenderGUI gui) -> pCallback.run(pInputField.getString(), iCurrentSearchOption));
        addElement(pSubmitButton);

        resize();
        reposition();
    }

    public void setCallback(SearchbarCallback callback)
    {
        this.pCallback = callback;
    }

    @Override
    protected void updateOnThemeChange() 
    {
        pInputField.setCornerRadius(new vec4(GUI.getTheme().cornerRadius.x, 0, 0, GUI.getTheme().cornerRadius.w));
        pSubmitButton.setCornerRadius(new vec4(0, GUI.getTheme().cornerRadius.y, GUI.getTheme().cornerRadius.z, 0));
        pBubble.setColor(vec4.sub(GUI.getTheme().primaryColor, 0.06f));
        pBubble.setSize(new ivec2(200, pSearchOptions.getSize().y + 30));
    }

    /**
     * Erstellt ein Searchbar GUI anhand einer XML Node
     * @return gibt das erstellte Searchbar-Objekt wieder
     */
    public static XMLGUICreator createFromXMLNode() 
    {
        return (XMLNode node) -> { 
			String localeid = node.getAttribute("locale-id");
			String[] optionids = node.getAttribute("option-ids").split(",");

			return new Searchbar(new ivec2(0,0), new ivec2(30,30), localeid, optionids);
        };
    }
}