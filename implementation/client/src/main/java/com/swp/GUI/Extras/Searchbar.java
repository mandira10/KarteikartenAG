package com.swp.GUI.Extras;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Radiobutton;
import com.gumse.gui.Basics.Speechbubble;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Radiobutton.OnSelectCallback;
import com.gumse.gui.Basics.Speechbubble.Side;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;

public class Searchbar extends RenderGUI
{
    public interface SearchbarCallback
    {
        public void run(String query, int option);
    }

    private TextField pInputField;
    private Button pSubmitButton;
    private Button pOptionsButton;
    private SearchbarCallback pCallback;
    private Speechbubble pBubble;
    private int iCurrentSearchOption;

    public Searchbar(ivec2 pos, ivec2 size, String hint, String[] options, SearchbarCallback callback)
    {
        this.sType = "Searchbar";
        this.vPos.set(pos);
        this.vSize.set(size);
        this.pCallback = callback;
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
        pInputField.setHint(hint);
        addElement(pInputField);

        
        pOptionsButton = new Button(new ivec2(100, 0), new ivec2(size.y, size.y), "", fonts.getFont("FontAwesome"));
        pOptionsButton.setOrigin(new ivec2(size.y * 3, 0));
        pOptionsButton.setPositionInPercent(true, false);
        pOptionsButton.getBox().setTextSize(20);
        pOptionsButton.setCornerRadius(new vec4(0,0,0,0));
        pOptionsButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                pBubble.show();
            }
        });
        addElement(pOptionsButton);

        pBubble = new Speechbubble(new ivec2(size.y / 2, 0), new ivec2(200, 120), Side.ABOVE);
        pBubble.setColor(vec4.sub(GUI.getTheme().primaryColor, 0.06f));
        pBubble.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                // Prevent clickthrough
            }
        });
        pBubble.hide(true);
        pOptionsButton.addGUI(pBubble);

        Radiobutton searchOptions = new Radiobutton(new ivec2(20, 20), 20, 100, fonts.getDefaultFont(), options);
        searchOptions.select(iCurrentSearchOption);
        searchOptions.singleSelect(true);
        searchOptions.setSizeInPercent(true, false);
        searchOptions.onSelect(new OnSelectCallback() {
            @Override public void run(int index, String content) 
            {
                iCurrentSearchOption = index;
            }
        });
        pBubble.addGUI(searchOptions);
        pBubble.setSize(new ivec2(200, searchOptions.getSize().y + 30));

        pSubmitButton = new Button(new ivec2(100, 0), new ivec2(size.y * 2, size.y), "", fonts.getFont("FontAwesome"));
        pSubmitButton.setOrigin(new ivec2(size.y * 2, 0));
        pSubmitButton.setPositionInPercent(true, false);
        pSubmitButton.setCornerRadius(new vec4(0, GUI.getTheme().cornerRadius.y, GUI.getTheme().cornerRadius.z, 0));
        pSubmitButton.getBox().setTextSize(20);
        pSubmitButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                pCallback.run(pInputField.getString(), iCurrentSearchOption);
            }
        });
        addElement(pSubmitButton);

        resize();
        reposition();
    }

    @Override
    protected void updateOnThemeChange() 
    {
        pInputField.setCornerRadius(new vec4(GUI.getTheme().cornerRadius.x, 0, 0, GUI.getTheme().cornerRadius.w));
        pSubmitButton.setCornerRadius(new vec4(0, GUI.getTheme().cornerRadius.y, GUI.getTheme().cornerRadius.z, 0));
        pBubble.setColor(vec4.sub(GUI.getTheme().primaryColor, 0.06f));
    }
}