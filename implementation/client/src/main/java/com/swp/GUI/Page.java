package com.swp.GUI;

import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;

public class Page extends RenderGUI
{
    public Page(String name)
    {
        Text nameText = new Text(name, FontManager.getInstance().getDefaultFont(), new ivec2(100, 3), 0);
        nameText.setCharacterHeight(50);
        nameText.setPositionInPercent(true, false);
        nameText.setOrigin(new ivec2(nameText.getSize().x + 10, 0));
        nameText.setColor(new vec4(0.15f, 0.15f, 0.15f, 1.0f));
        addElement(nameText);
    }
}
