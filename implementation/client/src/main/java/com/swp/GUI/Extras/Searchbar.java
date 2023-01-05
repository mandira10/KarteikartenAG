package com.swp.GUI.Extras;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;

public class Searchbar extends RenderGUI
{
    public interface SearchbarCallback
    {
        public void run();
    }

    private TextField pInputField;
    private Button pSubmitButton;
    private SearchbarCallback pCallback;

    public Searchbar(ivec2 pos, ivec2 size, String hint, SearchbarCallback callback)
    {
        this.sType = "Searchbar";
        this.vPos.set(pos);
        this.vSize.set(size);
        this.pCallback = callback;

        int corderradius = 7; 

        FontManager fonts = FontManager.getInstance();
        pInputField = new TextField("", fonts.getDefaultFont(), new ivec2(0, 0), new ivec2(100, 100));
        pInputField.setSizeInPercent(true, true);
        pInputField.setMargin(new ivec2(-size.y, 0));
        pInputField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) 
            {
                pCallback.run();
            }

            @Override public void input(String input, String complete) 
            {
            }
            
        });
        pInputField.setCornerRadius(new vec4(corderradius, 0, 0, corderradius));
        pInputField.setHint(hint);
        addElement(pInputField);

        pSubmitButton = new Button(new ivec2(100, 0), new ivec2(size.y, size.y), "", fonts.getFont("FontAwesome"));
        pSubmitButton.setOrigin(new ivec2(size.y, 0));
        pSubmitButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                pCallback.run();
            }
        });
        pSubmitButton.setPositionInPercent(true, false);
        pSubmitButton.setCornerRadius(new vec4(0, corderradius, corderradius, 0));
        pSubmitButton.getBox().setTextSize(20);
        addElement(pSubmitButton);

        resize();
        reposition();
    }
}