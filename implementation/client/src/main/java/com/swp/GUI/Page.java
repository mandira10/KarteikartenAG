package com.swp.GUI;

import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Locale;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;

/**
 * Die Seitenklasse von welcher alle anderen Seiten erben
 * 
 * @author Tom Beuke
 */
public class Page extends RenderGUI
{
    private final Text pNameText;
    private final String sLocaleID;

    /**
     * Der Hauptkonstruktor der Klasse Page
     *
     * @param name     Name der Seite
     * @param localeid Locale ID für den namen in anderen Sprachen
     */
    public Page(String name, String localeid)
    {
        pNameText = new Text(name, FontManager.getInstance().getDefaultFont(), new ivec2(100, 3), 0);
        pNameText.setCharacterHeight(50);
        pNameText.setPositionInPercent(true, false);
        pNameText.setOrigin(new ivec2(pNameText.getSize().x + 10, 0));
        pNameText.setColor(new vec4(0.15f, 0.15f, 0.15f, 1.0f));
        pNameText.setLocaleID(localeid);
        sLocaleID = localeid;
        addElement(pNameText);
    }

    @Override
    public void renderextra()
    {
        renderchildren();
        
        pNameText.render();
    }

    @Override
    protected void updateOnThemeChange() 
    {
        pNameText.setString(Locale.getCurrentLocale().getString(sLocaleID));
        pNameText.setOrigin(new ivec2(pNameText.getSize().x + 10, 0));
    }
}
