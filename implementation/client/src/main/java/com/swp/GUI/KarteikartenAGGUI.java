package com.swp.GUI;

import com.gumse.gui.GUI;
import com.gumse.gui.Theme;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.*;
import com.swp.DataModel.Settings.Setting;
import com.swp.DataModel.Settings;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.NotificationGUI;

/**
 * Das haupt GUI-Element des KarteikartenAG Projekts
 */
public class KarteikartenAGGUI extends RenderGUI
{
    private Sidebar pSidebar;
    private NotificationGUI pNotifications;
    private ConfirmationGUI pConfirmations;
    private Theme pLightTheme;

    private static KarteikartenAGGUI pInstance;


    private KarteikartenAGGUI()
    {
        PageManager.init(this);
        hideChildren(true); //Manually render

        
        GUI.getDefaultTheme().cornerRadius = new vec4(7,7,7,7);
        pLightTheme = new Theme();
        pLightTheme.backgroundColor   = vec4.div(Color.HEXToRGBA("#FFFFFF"), 255.0f);
        pLightTheme.primaryColor      = vec4.div(Color.HEXToRGBA("#FFFFFF"), 255.0f);
        pLightTheme.primaryColorShade = vec4.div(Color.HEXToRGBA("#CCCCCC"), 255.0f);
        pLightTheme.secondaryColor    = vec4.div(Color.HEXToRGBA("#A6C7E5"), 255.0f);
        pLightTheme.accentColor       = vec4.div(Color.HEXToRGBA("#0F79D9"), 255.0f);
        pLightTheme.accentColorShade1 = vec4.div(Color.HEXToRGBA("#A6C7E5"), 255.0f);
        pLightTheme.textColor         = vec4.div(Color.HEXToRGBA("#121416"), 255.0f);
        pLightTheme.cornerRadius      = new vec4(0,0,0,0);
        pLightTheme.borderThickness   = 1;

        Settings.getInstance().getLanguage().activate();
        if(!Settings.getInstance().getSetting(Setting.DARK_THEME).equals("true")) 
            GUI.setTheme(pLightTheme);
        updateTheme();


        pConfirmations = ConfirmationGUI.getInstance();
        addElement(pConfirmations);
        
        pNotifications = NotificationGUI.getInstance();
        addElement(pNotifications);

        pSidebar = new Sidebar();
        pSidebar.setSize(new ivec2(60, 100));
        pSidebar.setSizeInPercent(false, true);
        addElement(pSidebar);
    }


    @Override
    public void renderextra()
    {
        PageManager.render();
        pSidebar.render();
        pConfirmations.render();
        pNotifications.render();
    }

    @Override
    public void updateextra()
    {
        pConfirmations.update();
        if(pConfirmations.isHidden())
        {
            pNotifications.update();
            pSidebar.update();
            PageManager.update();
        }
    }

    public static KarteikartenAGGUI getInstance()
    {
        if(pInstance == null)
            pInstance = new KarteikartenAGGUI();
        return pInstance;
    }

    public Theme getLightTheme()
    {
        return pLightTheme;
    }

    public Sidebar getSidebar()
    {
        return pSidebar;
    }
}