package com.swp.GUI.Settings;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Dropdown.DropdownSelectionCallback;
import com.gumse.gui.Basics.Switch.OnSwitchTicked;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.DataModel.Settings;
import com.swp.DataModel.Language.Language;
import com.swp.DataModel.Settings.Setting;
import com.swp.GUI.Page;


public class SettingsPage extends Page 
{
    public SettingsPage() 
    {
        super("Settings");
        this.vSize = new ivec2(100,100);
        
        addGUI(XMLGUI.loadFile("guis/settings/settingsoverviewpage.xml"));

        Box subsettingBox = (Box)findChildByID("subsettingview");
        RenderGUI generalSettings = XMLGUI.loadFile("guis/settings/generalsettings.xml");
        //RenderGUI languageSettings = XMLGUI.loadFile("guis/settings/languagesettings.xml");
        RenderGUI serverSettings = XMLGUI.loadFile("guis/settings/serversettings.xml");

        subsettingBox.addGUI(generalSettings);
        Button generalSettingsButton = (Button)findChildByID("generalbutton");
        generalSettingsButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                generalSettings.hide(false);
                //languageSettings.hide(true);
                serverSettings.hide(true);
            }
        });

        Settings settings = Settings.getInstance();

        Switch themeSwitch = (Switch)generalSettings.findChildByID("themeswitch");
        themeSwitch.tick(settings.getSetting(Setting.DARK_THEME) == "true");
        themeSwitch.onTick(new OnSwitchTicked() {
            @Override public void run(boolean ticked) 
            {
                settings.setSetting(Setting.DARK_THEME, ticked ? "true" : "false");
            }
        });

        Dropdown languageDropdown = (Dropdown)generalSettings.findChildByID("languagedropdown");
        Language lang = settings.getLanguage();
        languageDropdown.setTitle(lang.getName());

        languageDropdown.onSelection(new DropdownSelectionCallback() {
            @Override public void run(String str) 
            {
                switch(str)
                {
                    case "German":  settings.setSetting(Setting.LANGUAGE, "de");
                    case "English": settings.setSetting(Setting.LANGUAGE, "en");
                }
            }
        });


        subsettingBox.addGUI(serverSettings);
        serverSettings.hide(true);
        Button serverSettingsButton = (Button)findChildByID("serverbutton");
        serverSettingsButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                generalSettings.hide(true);
                //languageSettings.hide(true);
                serverSettings.hide(false);
            }
        });

        TextField serveraddressField = (TextField)serverSettings.findChildByID("serveraddressfield");
        serveraddressField.setString(settings.getSetting(Setting.SERVER_ADDRESS));
        serveraddressField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) 
            {
                settings.setSetting(Setting.SERVER_ADDRESS, complete);
            }

            @Override public void input(String input, String complete) 
            {
            }
        });

        TextField serverportField = (TextField)serverSettings.findChildByID("serverportfield");
        serverportField.setString(settings.getSetting(Setting.SERVER_PORT));
        serverportField.setCallback(new TextFieldInputCallback() {
            @Override public void enter(String complete) 
            {
                settings.setSetting(Setting.SERVER_PORT, complete);
            }

            @Override public void input(String input, String complete) 
            {
            }
        });

        this.setSizeInPercent(true, true);
        reposition();
    }
}
