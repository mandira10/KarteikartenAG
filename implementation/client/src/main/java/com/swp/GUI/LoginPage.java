package com.swp.GUI;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.*;
import com.gumse.tools.Debug;

public class LoginPage extends Page
{
    public LoginPage()
    {
        super("Login");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/loginpage.xml"));

        Button loginButton = (Button)findChildByID("loginbutton");
        if(loginButton != null)
        {
            loginButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    Debug.info("Login Button");
                }
            });
        }

        Button registerButton = (Button)findChildByID("registerbutton");
        if(registerButton != null)
        {
            registerButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    Debug.info("Register Button");
                }
            });
        }


        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }    
}