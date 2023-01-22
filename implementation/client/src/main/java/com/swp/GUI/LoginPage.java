package com.swp.GUI;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.*;
import com.gumse.tools.Output;
import com.swp.GUI.Extras.RatingGUI;

public class LoginPage extends Page
{
    public LoginPage()
    {
        super("Login", "loginpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/loginpage.xml"));

        Button loginButton = (Button)findChildByID("loginbutton");
        if(loginButton != null)
        {
            loginButton.onClick(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    Output.info("Login Button");
                }
            });
        }

        Button registerButton = (Button)findChildByID("registerbutton");
        if(registerButton != null)
        {
            registerButton.onClick(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    Output.info("Register Button");
                }
            });
        }


        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }    
}