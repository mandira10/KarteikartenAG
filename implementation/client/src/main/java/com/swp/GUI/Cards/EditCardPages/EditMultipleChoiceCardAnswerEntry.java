package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;

public class EditMultipleChoiceCardAnswerEntry extends RenderGUI
{
    public interface RemoveAnswerEntryCallback
    {
        public void run(EditMultipleChoiceCardAnswerEntry entry);
    }

    private TextField pAnswerField;
    private Button pTrashButton;
    private Switch pSwitch;
    
    public EditMultipleChoiceCardAnswerEntry(String str, boolean correct, RemoveAnswerEntryCallback callback)
    {
        this.vSize.set(new ivec2(100, 30));

        pSwitch = new Switch(new ivec2(5, 5), new ivec2(20), 0);
        pSwitch.tick(correct);
        addElement(pSwitch);

        pAnswerField = new TextField(str, FontManager.getInstance().getDefaultFont(), new ivec2(40, 0), new ivec2(100, 30));
        pAnswerField.setSizeInPercent(true, false);
        pAnswerField.setMargin(new ivec2(-80, 0));
        addElement(pAnswerField);

        pTrashButton = new Button(new ivec2(100, 0), new ivec2(30, 30), "", FontManager.getInstance().getFont("FontAwesomeRegular"));
        pTrashButton.getBox().setTextSize(15);
        pTrashButton.getBox().setCornerRadius(new vec4(0, 0, 0, 0));
        pTrashButton.setPositionInPercent(true, false);
        pTrashButton.setOrigin(new ivec2(30, 0));
        EditMultipleChoiceCardAnswerEntry parentPtr = this;
        this.pTrashButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)
            {
                callback.run(parentPtr);
            }
            
        });
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