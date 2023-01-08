package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.GumMath;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec2;
import com.gumse.maths.vec4;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;

public class EditImageDescriptionCardAnswerEntry extends RenderGUI
{
    public interface RemoveAnswerEntryCallback
    {
        public void run(EditImageDescriptionCardAnswerEntry entry);
    }

    private TextBox pMovingIndexBox;
    private TextBox pIndexBox;
    private TextField pAnswerField;
    private Button pTrashButton;
    private boolean bSnapped;
    
    public EditImageDescriptionCardAnswerEntry(String str, ivec2 pos, RemoveAnswerEntryCallback callback)
    {
        this.sType = "EditImageDescriptionCardAnswerEntry";
        this.vSize.set(new ivec2(100, 30));


        pMovingIndexBox = new TextBox(String.valueOf(0), FontManager.getInstance().getDefaultFont(), pos, new ivec2(20, 20));
        pMovingIndexBox.setPositionInPercent(true, true);
        pIndexBox       = new TextBox(String.valueOf(0), FontManager.getInstance().getDefaultFont(), new ivec2(0, 0), new ivec2(30, 30));
        addElement(pIndexBox);

        pAnswerField = new TextField(str, FontManager.getInstance().getDefaultFont(), new ivec2(40, 0), new ivec2(100, 30));
        pAnswerField.setSizeInPercent(true, false);
        pAnswerField.setMargin(new ivec2(-80, 0));
        addElement(pAnswerField);

        pTrashButton = new Button(new ivec2(100, 0), new ivec2(30, 30), "", FontManager.getInstance().getFont("FontAwesomeRegular"));
        pTrashButton.getBox().setTextSize(15);
        pTrashButton.getBox().setCornerRadius(new vec4(0, 0, 0, 0));
        pTrashButton.setPositionInPercent(true, false);
        pTrashButton.setOrigin(new ivec2(30, 0));
        EditImageDescriptionCardAnswerEntry parentPtr = this;
        this.pTrashButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() {
                callback.run(parentPtr);
            }
            
        });
        addElement(pTrashButton);
        
        this.setSizeInPercent(true, true);
        reposition();
    }

    @Override
    public void updateextra() 
    {
        Mouse mouse = Window.CurrentlyBoundWindow.getMouse();
        
        if(!Mouse.isBusy() && pMovingIndexBox.isMouseInside())
        {
            Mouse.setActiveHovering(true);
            mouse.setCursor(Mouse.GUM_CURSOR_ALL_SIDES_RESIZE);
            
            if(mouse.hasLeftClickStart())
            {
                Mouse.setBusiness(true);
                bSnapped = true;
                mouse.hide(true);
            }
        }

        if(bSnapped)
        {
            vec2 pos = new vec2(ivec2.sub(ivec2.sub(mouse.getPosition(), ivec2.div(pMovingIndexBox.getSize(), 2.0f)), pMovingIndexBox.getParent().getPosition()));
            pos.x /= (float)pMovingIndexBox.getParent().getSize().x;
            pos.y /= (float)pMovingIndexBox.getParent().getSize().y;
            pos = vec2.mul(pos, 100.0f);

            pos.x = GumMath.clamp(pos.x, 0.0f, 100.0f);
            pos.y = GumMath.clamp(pos.y, 0.0f, 100.0f);

            pMovingIndexBox.setPosition(new ivec2(pos));


            if(mouse.hasLeftRelease())
            {
                bSnapped = false;
                mouse.hide(false);
            }
        }
    }


    //
    // Setter
    //
    void setIndex(int index)
    {
        pMovingIndexBox.setString(String.valueOf(index));
        pIndexBox.setString(String.valueOf(index));
    } 


    //
    // Getter
    //
    public String getAnswerString() { return pAnswerField.getBox().getString(); }
    public TextBox getIndexBox()    { return this.pMovingIndexBox; }
}