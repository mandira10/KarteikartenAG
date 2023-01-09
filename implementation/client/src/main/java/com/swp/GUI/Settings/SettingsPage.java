package com.swp.GUI.Settings;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
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

        String currentLanguage = "TODO";
        Dropdown languageDropdown = (Dropdown)findChildByID("languagedropdown");
        languageDropdown.setTitle(currentLanguage);



        /*subsettingBox.addGUI(languageSettings);
        languageSettings.hide(true);
        Button languageSettingsButton = (Button)findChildByID("languagebutton");
        languageSettingsButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                generalSettings.hide(true);
                languageSettings.hide(false);
                serverSettings.hide(true);
            }
        });*/


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




        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }
}
