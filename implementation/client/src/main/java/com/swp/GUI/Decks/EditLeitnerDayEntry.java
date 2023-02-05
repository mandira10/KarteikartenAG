package com.swp.GUI.Decks;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.tools.Toolbox;

/**
 * Ein Eintrag für die EditDeckPage
 * 
 * @author Tom Beuke
 */
public class EditLeitnerDayEntry extends RenderGUI
{
    /**
     * Wird ausgeführt, wenn der Eintrag gelöscht oder geändert wird
     */
    public interface EntryCallback
    {
        /**
         * Wird ausgeführt, wenn der Eintrag gelöscht wird
         * @param entry Der gelöschte eintrag
         */
        void onRemove(EditLeitnerDayEntry entry);

        /**
         * Wird ausgeführt, wenn der Eintrag geändert wird
         * @param entry Der geänderte eintrag
         */
        void onChange(EditLeitnerDayEntry entry);
    }

    private final TextField pDayField;
    private final Text pDayNumber;

    /**
     * Der Hauptkonstruktor der Klasse EditLeitnerDayEntry
     * @param days     Die Tage bis zum erneuten Lernen
     * @param index    Der Index des Eintrags
     * @param callback Die Callbackfunktion
     */
    public EditLeitnerDayEntry(int days, int index, EntryCallback callback)
    {
        this.vSize.set(new ivec2(100, 30));
        this.setType("EditLeitnerDayEntry");

        EditLeitnerDayEntry thisentry = this;

        pDayNumber = new Text(Locale.getCurrentLocale().getString("box") + " " + index, FontManager.getInstance().getDefaultFont(), new ivec2(0, 3), 0);
        pDayNumber.setCharacterHeight(25);
        addElement(pDayNumber);

        pDayField = new TextField(String.valueOf(days), FontManager.getInstance().getDefaultFont(), new ivec2(100, 0), new ivec2(100, 30));
        pDayField.setSizeInPercent(true, false);
        pDayField.setMargin(new ivec2(-140, 0));
        pDayField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            { 
                callback.onChange(thisentry); 
            } 
        });
        addElement(pDayField);

        Button pTrashButton = new Button(new ivec2(100, 0), new ivec2(30, 30), "", FontManager.getInstance().getFont("FontAwesomeRegular"));
        pTrashButton.getBox().setTextSize(15);
        pTrashButton.getBox().setCornerRadius(new vec4(0, 0, 0, 0));
        pTrashButton.setPositionInPercent(true, false);
        pTrashButton.setOrigin(new ivec2(30, 0));
        
        pTrashButton.onClick((RenderGUI gui) -> callback.onRemove(thisentry));
        addElement(pTrashButton);
        
        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setDayNumber(int num)
    {
        pDayNumber.setString(Locale.getCurrentLocale().getString("box") + " " + num);
    }

    //
    // Getter
    //
    public int getDays() { return Toolbox.StringToInt(pDayField.getBox().getString()); }
}