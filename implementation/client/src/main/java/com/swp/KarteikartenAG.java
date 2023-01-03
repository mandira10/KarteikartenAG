package com.swp;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;

import com.gumse.basics.Globals;
import com.gumse.gui.GUI;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.Display;
import com.gumse.system.Window;
import com.gumse.system.Window.WindowResizePosCallback;
import com.gumse.tools.Debug;
import com.gumse.tools.FPS;
import com.swp.DataModel.Category;
import com.swp.GUI.KarteikartenAGGUI;

////////////////////////////////////////////////////
//
// TODO-List
// 
// GUIs
//   - Notification GUI    -- done
//   - Confirmation GUI
//   - Rate Card GUI       -- done
//   - Radiobutton GUI     -- done
//   - Tag Textfield GUI   -- done
//   - Audio GUI           -- done
//   - Finalize Decktests
//       - TextCard        -- done
//       - MultiCard       -- done
//       - TrueFalseCard   -- done
//       - ImageTestCard   -- done
//       - ImageDescCard
//       - AudioCard
//   - Finalize Viewpages
//       - TextCard        
//       - MultiCard       
//       - TrueFalseCard   
//       - ImageTestCard   
//       - ImageDescCard
//       - AudioCard
//   - Export Cards Page
//   - Add icons to edit options
//   - Finalize Editpages
//       - TextCard        -- done
//       - MultiCard       -- done
//       - TrueFalseCard   -- done
//       - ImageTestCard   -- done
//       - ImageDescCard
//       - AudioCard       -- done
//   - Fix Scroller class
//   - Category GUIs
//       - Category List
//       - Single Category View
//       - Category Tree
//   - Language Class
////////////////////////////////////////////////////


public class KarteikartenAG 
{
    static GUI pMainGUI;
    public static void main(String[] args)
    {
        Globals.DEBUG_BUILD = true;
        System.setProperty("java.awt.headless", "true"); //for iCrap support

        Debug.init();
        Display.init();
        Window pMainWindow = new Window("KarteikartenAG", new ivec2(800, 600), Window.GUM_WINDOW_RESIZABLE, null);
        pMainWindow.setClearColor(new vec4(0.09f, 0.1f, 0.11f, 1.0f)); // Set the clear color);
        pMainWindow.setVerticalSync(true);
        pMainWindow.setMinimumSize(new ivec2(800, 600));

        String usedCharacters = "";
		Font pFontAwesome = Font.loadFontFromResource("fonts/font-awesome6-free-solid-900.otf", usedCharacters);
        FontManager.getInstance().addFont(pFontAwesome, "FontAwesome");

        //
        // Hollow symbols
        //
        String usedRegularCharacters = "";
		Font pFontAwesomeRegular = Font.loadFontFromResource("fonts/font-awesome6-free-regular-400.otf", usedRegularCharacters);
        FontManager.getInstance().addFont(pFontAwesomeRegular, "FontAwesomeRegular");


        //
        //Init OpenAL Context
        //
        long device = ALC11.alcOpenDevice((ByteBuffer)null);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        long context = ALC10.alcCreateContext(device, (IntBuffer)null);
        ALC11.alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

        //Init Listener
        AL11.alListener3f(AL11.AL_POSITION, 0, 0, 0);
        AL11.alListener3f(AL11.AL_VELOCITY, 0, 0, 0);


        //System.setProperty("java.awt.headless", "false"); //for iCrap support

        pMainGUI = new GUI(pMainWindow);
		pMainWindow.onResized(new WindowResizePosCallback() {
            @Override public void run(ivec2 val) {
                pMainGUI.setSize(val);
            }
        });

        KarteikartenAGGUI pKarteikartenAGGUI = new KarteikartenAGGUI();
        pKarteikartenAGGUI.setSize(new ivec2(100, 100));
        pKarteikartenAGGUI.setSizeInPercent(true, true);
        pMainGUI.addGUI(pKarteikartenAGGUI);

        //Debugging
        TextBox fpsTextBox = null;
        if(Globals.DEBUG_BUILD)
        {
            fpsTextBox = new TextBox("FPS: ", FontManager.getInstance().getDefaultFont(), new ivec2(100, 100), new ivec2(100, 30));
            fpsTextBox.setPositionInPercent(true, true);
            fpsTextBox.setColor(new vec4(0.0f, 0.0f, 0.0f, 0.3f));
            fpsTextBox.setAlignment(TextBox.Alignment.LEFT);
            fpsTextBox.setOrigin(fpsTextBox.getSize());
            pMainGUI.addGUI(fpsTextBox);
        }

        pMainGUI.updateCanvas();

        Category rootCategory = new Category("Root"); //For now


        
        while(pMainWindow.isOpen())
        {
            Display.pollEvents();
            pMainWindow.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            pMainGUI.render();
            pMainGUI.update();
            if(fpsTextBox != null)
            {
                fpsTextBox.setString("FPS: " + (int)FPS.getFPS());   
            }

            //System.out.println("Mouse is: " + (Mouse.isBusy() ? "Busy" : "Available"));

            pMainWindow.finishRender();
            pMainWindow.getMouse().reset();
            pMainWindow.getKeyboard().reset();


            //pMainWindow.update();
            FPS.update();
        }
    }
}