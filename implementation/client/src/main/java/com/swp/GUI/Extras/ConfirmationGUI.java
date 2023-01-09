package com.swp.GUI.Extras;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;

public class ConfirmationGUI extends Box
{
    public interface ConfirmationCallback
    {
        public void onConfirm();
        public void onCancel();
    }

    private static ConfirmationGUI pInstance;
    private ConfirmationCallback pCallback;
    private TextBox pDialogTextBox;

    private ConfirmationGUI()
    {
        super(new ivec2(0,0), new ivec2(100, 100));
        setColor(new vec4(0, 0, 0, 0.8f));

        Box dialogBox = new Box(new ivec2(50, 50), new ivec2(500, 250));
        dialogBox.setCornerRadius(new vec4(7));
        dialogBox.setPositionInPercent(true, true);
        dialogBox.setOrigin(new ivec2(250, 125));
        dialogBox.setColor(GUI.getTheme().primaryColor);
        addElement(dialogBox);

        Font defaultFont = FontManager.getInstance().getDefaultFont();

        pDialogTextBox = new TextBox("", defaultFont, new ivec2(5, 30), new ivec2(90, 50));
        pDialogTextBox.setPositionInPercent(true, false);
        pDialogTextBox.setSizeInPercent(true, false);
        pDialogTextBox.setAutoInsertLinebreaks(true);
        pDialogTextBox.setTextSize(35);
        pDialogTextBox.getBox().hide(true);
        pDialogTextBox.setAlignment(Alignment.CENTER);
        dialogBox.addGUI(pDialogTextBox);

        Button confirmButton = new Button(new ivec2(100, 100), new ivec2(120, 40), "Confirm", defaultFont);
        confirmButton.setPositionInPercent(true, true);
        confirmButton.setOrigin(new ivec2(140, 60));
        confirmButton.setColor(GUI.getTheme().accentColor);
        confirmButton.setCornerRadius(new vec4(10));
        confirmButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                pCallback.onConfirm();
                pInstance.hide(true);
            }
        });
        dialogBox.addGUI(confirmButton);

        Button cancelButton = new Button(new ivec2(100, 100), new ivec2(120, 40), "Cancel", defaultFont);
        cancelButton.setPositionInPercent(true, true);
        cancelButton.setOrigin(new ivec2(280, 60));
        cancelButton.setColor(GUI.getTheme().primaryColor);
        cancelButton.setCornerRadius(new vec4(10));
        cancelButton.getBox().getBox().setBorderThickness(3);
        cancelButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                pCallback.onCancel();
                pInstance.hide(true);
            }
        });
        dialogBox.addGUI(cancelButton);

        hide(true);
        setSizeInPercent(true, true);
        reposition();
    }

    public static ConfirmationGUI getInstance()
    {
        if(pInstance == null)
            pInstance = new ConfirmationGUI();

        return pInstance;
    }

    public void showConfirmationDialog(String str, ConfirmationCallback callback)
    {
        this.pCallback = callback;
        this.pDialogTextBox.setString(str);
        this.pDialogTextBox.setSize(new ivec2(90, pDialogTextBox.getText().getSize().y));
        hide(false);
    }


    public static void openDialog(String str, ConfirmationCallback callback)
    {
        getInstance().showConfirmationDialog(str, callback);
    }
}