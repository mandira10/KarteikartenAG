package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;

/**
 * Ein Eintrag für das EditMultipleChoiceCard GUI
 * 
 * @author Tom Beuke
 */
public class EditMultipleChoiceCardAnswerEntry extends RenderGUI
{
    /**
     * Wird ausgeführt, wenn der Eintrag gelöscht oder geändert wird
     */
    public interface AnswerEntryCallback
    {
        /**
         * Wird ausgeführt, wenn der Eintrag gelöscht wird
         * @param entry Der gelöschte eintrag
         */
        void onRemove(EditMultipleChoiceCardAnswerEntry entry);

        /**
         * Wird ausgeführt, wenn der Eintrag geändert wird
         * @param entry Der geänderte eintrag
         */
        void onChange(EditMultipleChoiceCardAnswerEntry entry);
    }

    private final TextField pAnswerField;
    private final Switch pSwitch;

    /**
     * Der Hauptkonstruktor der Klasse EditMultipleChoiceCardAnswerEntry
     * @param str      Der name des Eintrags
     * @param correct  Ob der Eintrag als Korrekt makiert werden soll
     * @param callback Die Callbackfunktion
     */
    public EditMultipleChoiceCardAnswerEntry(String str, boolean correct, AnswerEntryCallback callback)
    {
        this.vSize.set(new ivec2(100, 30));
        this.setType("EditMultipleChoiceCardAnswerEntry");

        EditMultipleChoiceCardAnswerEntry thisentry = this;

        pSwitch = new Switch(new ivec2(5, 5), new ivec2(20), 0);
        pSwitch.tick(correct);
        pSwitch.onTick((boolean ticked) -> callback.onChange(thisentry));
        addElement(pSwitch);

        pAnswerField = new TextField(str, FontManager.getInstance().getDefaultFont(), new ivec2(40, 0), new ivec2(100, 30));
        pAnswerField.setSizeInPercent(true, false);
        pAnswerField.setMargin(new ivec2(-80, 0));
        pAnswerField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) {}
            @Override public void input(String input, String complete) 
            { 
                callback.onChange(thisentry); 
            } 
        });
        addElement(pAnswerField);

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

    //
    // Getter
    //
    public boolean isCorrect()      { return pSwitch.isTicked(); }
    public String getAnswerString() { return pAnswerField.getBox().getString(); }
}