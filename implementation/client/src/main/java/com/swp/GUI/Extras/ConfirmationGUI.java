package com.swp.GUI.Extras;

import com.gumse.gui.GUI;
import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;

/**
 * ConfirmationGUI wird dazu verwendet um 
 * einen Bestätigungsdialog zu öffnen.
 * 
 * @author Tom Beuke
 */
public class ConfirmationGUI extends Box
{
    /**
     * Die Callbackmethoden, welche die auswahl des Users zurückgibt
     */
    public interface ConfirmationCallback
    {
        /**
         * Wird bei der auswahl 'Confirm' ausgeführt
         */
        void onConfirm();
        /**
         * Wird bei der auswahl 'Cancel' ausgeführt
         */
        void onCancel();
    }

    private static ConfirmationGUI pInstance;
    private ConfirmationCallback pCallback;
    private final TextBox pDialogTextBox;
    private final Button pCancelButton;
    private final Button pConfirmButton;

    private ConfirmationGUI()
    {
        super(new ivec2(0,0), new ivec2(100, 100));
        setColor(new vec4(0, 0, 0, 0.8f));

        Box dialogBox = new Box(new ivec2(50, 50), new ivec2(500, 250));
        dialogBox.setCornerRadius(new vec4(7));
        dialogBox.setPositionInPercent(true, true);
        dialogBox.setOrigin(new ivec2(250, 125));
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

        pConfirmButton = new Button(new ivec2(100, 100), new ivec2(140, 40), Locale.getCurrentLocale().getString("confirmbutton"), defaultFont);
        pConfirmButton.setLocaleID("confirmbutton");
        pConfirmButton.setPositionInPercent(true, true);
        pConfirmButton.setOrigin(new ivec2(160, 60));
        pConfirmButton.setColor(GUI.getTheme().accentColor);
        pConfirmButton.onClick((RenderGUI gui) -> {
            pCallback.onConfirm();
            pInstance.hide(true);
        });
        dialogBox.addGUI(pConfirmButton);

        pCancelButton = new Button(new ivec2(100, 100), new ivec2(140, 40), Locale.getCurrentLocale().getString("cancelbutton"), defaultFont);
        pCancelButton.setLocaleID("cancelbutton");
        pCancelButton.setPositionInPercent(true, true);
        pCancelButton.setOrigin(new ivec2(320, 60));
        pCancelButton.setColor(GUI.getTheme().primaryColor);
        pCancelButton.getBox().getBox().setBorderThickness(3);
        pCancelButton.onClick((RenderGUI gui) -> {
            pCallback.onCancel();
            pInstance.hide(true);
        });
        dialogBox.addGUI(pCancelButton);

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

    /**
     * Zeigt einen neuen Fragedialog
     *
     * @param str      Der Dialogtext
     * @param callback Die Rückgabefunktion
     */
    public void showConfirmationDialog(String str, ConfirmationCallback callback)
    {
        this.pCallback = callback;
        this.pDialogTextBox.setString(str);
        this.pDialogTextBox.setSize(new ivec2(90, pDialogTextBox.getText().getSize().y));
        hide(false);
    }


    /**
     * Öffnet einen neuen Fragedialog
     *
     * @param str      Der Dialogtext
     * @param callback Die Rückgabefunktion
     */
    public static void openDialog(String str, ConfirmationCallback callback)
    {
        getInstance().showConfirmationDialog(str, callback);
    }

    @Override
    protected void updateOnThemeChange() {
        pCancelButton.setColor(GUI.getTheme().primaryColor);
        pConfirmButton.setColor(GUI.getTheme().accentColor);
    }
}