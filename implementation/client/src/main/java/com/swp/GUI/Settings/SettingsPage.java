package com.swp.GUI.Settings;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.swp.DataModel.Settings;
import com.swp.DataModel.Language.Language;
import com.swp.DataModel.Settings.Setting;
import com.swp.GUI.KarteikartenAGGUI;
import com.swp.GUI.Page;


public class SettingsPage extends Page 
{
    Box pSubsettingBox;

    public SettingsPage() 
    {
        super("Settings", "settingspage");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/settings/settingsoverviewpage.xml"));
        Settings settings = Settings.getInstance();

        pSubsettingBox = (Box)findChildByID("subsettingview");
        RenderGUI generalSettings = XMLGUI.loadFile("guis/settings/generalsettings.xml");
        RenderGUI serverSettings  = XMLGUI.loadFile("guis/settings/serversettings.xml");

        pSubsettingBox.addGUI(generalSettings);
        pSubsettingBox.setColor(vec4.sub(GUI.getTheme().backgroundColor, new vec4(0.2f, 0.2f, 0.2f, 0.0f)));
        Button generalSettingsButton = (Button)findChildByID("generalbutton");
        generalSettingsButton.onClick((RenderGUI gui) -> {
            generalSettings.hide(false);
            serverSettings.hide(true);
        });

        Switch themeSwitch = (Switch)generalSettings.findChildByID("themeswitch");
        themeSwitch.tick(settings.getSetting(Setting.DARK_THEME).equals("true"));
        themeSwitch.onTick((boolean ticked) -> {
            settings.setSetting(Setting.DARK_THEME, ticked ? "true" : "false");
            if(ticked) { GUI.setTheme(GUI.getDefaultTheme()); }
            else       { GUI.setTheme(KarteikartenAGGUI.getInstance().getLightTheme()); }
            KarteikartenAGGUI.getInstance().updateTheme();
        });

        Dropdown languageDropdown = (Dropdown)generalSettings.findChildByID("languagedropdown");
        Language lang = settings.getLanguage();
        languageDropdown.setTitle(lang.getLocale().getLanguage());

        languageDropdown.onSelection((str) -> {
            if     (str.equals("German"))  settings.setSetting(Setting.LANGUAGE, "de");
            else if(str.equals("English")) settings.setSetting(Setting.LANGUAGE, "en");

            settings.getLanguage().activate();
            KarteikartenAGGUI.getInstance().updateTheme();
        });


        pSubsettingBox.addGUI(serverSettings);
        serverSettings.hide(true);
        Button serverSettingsButton = (Button)findChildByID("serverbutton");
        serverSettingsButton.onClick((RenderGUI gui) -> {
            generalSettings.hide(true);
            serverSettings.hide(false);
        });

        TextField serveraddressField = (TextField)serverSettings.findChildByID("serveraddressfield");
        serveraddressField.setString(settings.getSetting(Setting.SERVER_ADDRESS));
        serveraddressField.setCallback(new TextFieldInputCallback() {
            @Override public void input(String input, String complete) {}
            @Override public void enter(String complete) 
            {
                settings.setSetting(Setting.SERVER_ADDRESS, complete);
            }
        });

        TextField serverportField = (TextField)serverSettings.findChildByID("serverportfield");
        serverportField.setString(settings.getSetting(Setting.SERVER_PORT));
        serverportField.setCallback(new TextFieldInputCallback() {
            @Override public void input(String input, String complete) {}
            @Override public void enter(String complete) 
            {
                settings.setSetting(Setting.SERVER_PORT, complete);
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
    }

    @Override
    protected void updateOnThemeChange() 
    {
        pSubsettingBox.setColor(vec4.sub(GUI.getTheme().backgroundColor, new vec4(0.2f, 0.2f, 0.2f, 0.0f)));
    }
}