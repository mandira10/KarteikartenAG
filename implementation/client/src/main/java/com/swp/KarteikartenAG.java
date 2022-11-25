package com.swp;

import static org.lwjgl.opengl.GL11.*;

import com.gumse.basics.Globals;
import com.gumse.gui.GUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.Display;
import com.gumse.system.Window;
import com.gumse.system.Window.WindowResizePosCallback;
import com.gumse.tools.FPS;
import com.swp.GUI.KarteikartenAGGUI;
import com.swp.GUI.PageManager;

public class KarteikartenAG 
{
    static GUI pMainGUI;
    public static void main(String[] args)
    {
        Globals.DEBUG_BUILD = true;
        System.setProperty("java.awt.headless", "true"); //for iCrap support

        Display.init();
        Window pMainWindow = new Window("Example App", new ivec2(500, 500), Window.GUM_WINDOW_RESIZABLE, null);
        pMainWindow.setClearColor(new vec4(0.09f, 0.1f, 0.11f, 1.0f)); // Set the clear color);

        pMainGUI = new GUI(pMainWindow);
		pMainWindow.onResized(new WindowResizePosCallback() {
            @Override public void run(ivec2 val) {
                pMainGUI.setSize(val);
            }
        });

        PageManager.init();

        KarteikartenAGGUI pKarteikartenAGGUI = new KarteikartenAGGUI();
        pKarteikartenAGGUI.setSize(new ivec2(100, 100));
        pKarteikartenAGGUI.setSizeInPercent(true, true);
        pMainGUI.addGUI(pKarteikartenAGGUI);
        
        while(pMainWindow.isOpen())
        {
            Display.pollEvents();
            pMainWindow.clear(GL_COLOR_BUFFER_BIT);
            pMainGUI.render();
            pMainGUI.update();

            //System.out.println("Mouse is: " + (Mouse.isBusy() ? "Busy" : "Available"));

            pMainWindow.finishRender();
            pMainWindow.getMouse().reset();
            pMainWindow.getKeyboard().reset();

            //pMainWindow.update();
            FPS.update();
        }
    }
}