package com.swp;

import static org.lwjgl.opengl.GL11.*;

import com.gumse.basics.Globals;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.Display;
import com.gumse.system.Window;
import com.gumse.tools.FPS;
import com.swp.GUI.KarteikartenAGGUI;

public class KarteikartenAG 
{
    public static void main(String[] args)
    {
        Globals.DEBUG_BUILD = true;
        System.setProperty("java.awt.headless", "true"); //for iCrap support

        Display.init();
        Window pMainWindow = new Window("Example App", new ivec2(500, 500), Window.GUM_WINDOW_RESIZABLE, null);
        pMainWindow.setClearColor(new vec4(0.09f, 0.1f, 0.11f, 1.0f)); // Set the clear color);


        KarteikartenAGGUI pMainGUI = new KarteikartenAGGUI();
        
        while(pMainWindow.isOpen())
        {
            Display.pollEvents();
            pMainWindow.clear(GL_COLOR_BUFFER_BIT);

            //System.out.println("Mouse is: " + (Mouse.isBusy() ? "Busy" : "Available"));

            pMainWindow.finishRender();
            pMainWindow.getMouse().reset();
            pMainWindow.getKeyboard().reset();

            //pMainWindow.update();
            FPS.update();
        }
    }
}