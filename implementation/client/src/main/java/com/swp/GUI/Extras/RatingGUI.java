package com.swp.GUI.Extras;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Debug;

public class RatingGUI extends RenderGUI
{
    public interface RateCallback { void run(int rating); }

    private Box pBackground;
    private int iStarSize;
    private int iActualRating;
    private boolean bReset;
    private RateCallback pCallback;
    private static final int iGapSize = 3;

    public RatingGUI(ivec2 pos, int starSize, int numStars)
    {
        this.vPos.set(pos);
        this.vSize.set(new ivec2((starSize + iGapSize) * numStars - 3, starSize));
        this.iStarSize = starSize;
        this.iActualRating = 0;
        this.bReset = false;
        this.pCallback = null;

        pBackground = new Box(new ivec2(0,0), new ivec2(100, 100));
        pBackground.setSizeInPercent(true, true);
        pBackground.setCornerRadius(new vec4(7));
        pBackground.setColor(new vec4(0));
        addElement(pBackground);

        Font hollowFont = FontManager.getInstance().getFont("FontAwesomeRegular");

        for(int i = 0; i < numStars; i++)
        {
            Text star = new Text("", hollowFont, new ivec2((starSize + iGapSize) * i, 0), 0);
            star.setCharacterHeight(starSize);
            pBackground.addGUI(star);
        }

    }

    @Override
    public void update() 
    {
        if(pBackground.isMouseInside())
        {
            Mouse.setActiveHovering(true);
            Window.CurrentlyBoundWindow.getMouse().setCursor(Mouse.GUM_CURSOR_HAND);

            for(RenderGUI child : pBackground.getChildren())
            {
                Text star = (Text)child;
                star.setColor(new vec4(0.9f, 0.9f, 0.9f, 1.0f));
                star.setFont(FontManager.getInstance().getFont("FontAwesomeRegular"));
            }

            int mouseoffset = Window.CurrentlyBoundWindow.getMouse().getPosition().x - pBackground.getPosition().x;
            int starID = mouseoffset / (iStarSize + iGapSize);

            for(int i = 0; i < starID + 1; i++)
            {
                Text star = (Text)pBackground.getChild(i);
                star.setColor(new vec4(1, 1, 0, 1));
                star.setFont(FontManager.getInstance().getFont("FontAwesome"));
            }

            if(pBackground.isClicked())
            {
                iActualRating = starID + 1;
                if(pCallback != null)
                    pCallback.run(iActualRating);
            }

            bReset = true;
        }
        else if(bReset)
        {
            for(int i = 0; i < pBackground.numChildren(); i++)
            {
                Text star = (Text)pBackground.getChild(i);
                if(i < iActualRating)
                {
                    star.setColor(new vec4(1, 1, 0, 1));
                    star.setFont(FontManager.getInstance().getFont("FontAwesome"));
                }
                else
                {
                    star.setColor(new vec4(0.9f, 0.9f, 0.9f, 1.0f));
                    star.setFont(FontManager.getInstance().getFont("FontAwesomeRegular"));
                }
            }

            bReset = false;
        }
    }

    //
    // Setter
    // 
    public void setRating(int rating)              { this.iActualRating = rating; bReset = true; }
    public void setCallback(RateCallback callback) { this.pCallback = callback; }

    //
    // Getter
    // 
    public int getRating()            { return iActualRating; }
}