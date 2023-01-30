package com.swp.GUI;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.*;
import com.swp.DataModel.Settings;
import com.swp.DataModel.Settings.Setting;

public class LoginPage extends Page
{
    public LoginPage()
    {
        super("Login", "loginpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/loginpage.xml"));
        TextField usernamefield = (TextField)findChildByID("usernamefield");
        TextField passwordfield = (TextField)findChildByID("passwordfield");

        Button loginButton = (Button)findChildByID("loginbutton");
        loginButton.onClick((RenderGUI gui) -> {
            //Do login request and do:
            //Settings.getInstance().setSetting(Setting.USER_NAME, usernamefield.getString());
            //Settings.getInstance().setSetting(Setting.USER_PASSWD, passwordfield.getString());
            //on success
        });


        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }    
}