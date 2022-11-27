package com.swp.GUI;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.*;

public class LoginPage extends RenderGUI
{
    public LoginPage()
    {
        this.vSize = new ivec2(100,100);

        FontManager fonts = FontManager.getInstance();
        float cornerradius = 7.0f;

        TextField usernameField = new TextField("Username", fonts.getDefaultFont(), new ivec2(50, 100), new ivec2(400, 60));
        usernameField.setCornerRadius(new vec4(cornerradius));
        addElement(usernameField);

        TextField passwordField = new TextField("Password", fonts.getDefaultFont(), new ivec2(50, 180), new ivec2(400, 60));
        passwordField.setCornerRadius(new vec4(cornerradius));
        addElement(passwordField);

        Text rememberText = new Text("Remember me", fonts.getDefaultFont(), new ivec2(50, 250), 0);
        rememberText.setCharacterHeight(20);
		rememberText.setColor(new vec4(0.9f, 0.9f, 0.9f, 1.0f));
        addElement(rememberText);

        Switch rememberSwitch = new Switch(new ivec2(430, 250), new ivec2(20, 20), 12);
        addElement(rememberSwitch);


        Button registerButton = new Button(new ivec2(50, 400), new ivec2(190, 60), "Register", fonts.getDefaultFont());
        registerButton.setCornerRadius(new vec4(cornerradius));
        registerButton.setColor(new vec4(0.2f, 0.2f, 0.4f, 1.0f));
        addElement(registerButton);

        Button loginButton = new Button(new ivec2(260, 400), new ivec2(190, 60), "Login", fonts.getDefaultFont());
        loginButton.setCornerRadius(new vec4(cornerradius));
        addElement(loginButton);

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }    
}