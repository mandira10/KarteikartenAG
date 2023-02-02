package com.swp.GUI;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextField.TextFieldInputCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.*;
import com.swp.DataModel.User;

public class LoginPage extends Page
{
    public LoginPage()
    {
        super("Login", "loginpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/loginpage.xml"));
        TextField usernamefield = (TextField)findChildByID("usernamefield");
        TextField passwordfield = (TextField)findChildByID("passwordfield");
        TextFieldInputCallback callback = new TextFieldInputCallback() {
            @Override public void enter(String complete) {
                login(usernamefield.getString(), passwordfield.getString());
            }
            @Override public void input(String input, String complete) {}  
        };

        usernamefield.setCallback(callback);
        passwordfield.setCallback(callback);

        Button loginButton = (Button)findChildByID("loginbutton");
        loginButton.onClick((RenderGUI gui) -> {
            login(usernamefield.getString(), passwordfield.getString());
        });

        Button demoButton = (Button)findChildByID("demobutton");
        demoButton.onClick((RenderGUI gui) -> {
            demo();
        });


        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }    

    private void login(String username, String password)
    {
        User.loginUser(new User(username, password));
    } 

    private void demo()
    {
        User.demoUser();
    }
}