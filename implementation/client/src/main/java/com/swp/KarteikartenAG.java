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
import com.gumse.tools.Output;
import com.gumse.tools.Output.OutputCallback;
import com.gumse.tools.FPS;
import com.swp.DataModel.Category;
import com.swp.GUI.KarteikartenAGGUI;

import lombok.extern.slf4j.Slf4j;

////////////////////////////////////////////////////
//
// TODO-List
// 
// GUIs
//   - Create GUI inside thread
//   - Card List columns            -- done
//   - card references list (links) 
//   - Show tags/categories in view -- done
//   - Edit card new card switch    -- done
//   - Notification GUI             -- done
//   - Confirmation GUI             -- done
//   - Rate Card GUI                -- done
//   - Radiobutton GUI              -- done
//   - Tag Textfield GUI            -- done
//   - Audio GUI                    -- done
//   - Card creation                -- done
//   - Edit card category           -- done
//   - Finalize Decktests           -- done
//       - TextCard                 -- done
//       - MultiCard                -- done
//       - TrueFalseCard            -- done
//       - ImageTestCard            -- done
//       - ImageDescCard            -- done
//       - AudioCard                -- done
//   - Deck editpage
//   - Show deck progress           -- done
//   - Final testpage               -- done
//   - Test rating                  -- done
//   - Studysystem editpage         -- done
//   - Studysystem dropdown         -- done
//   - Finalize Viewpages
//       - TextCard                 -- done
//       - MultiCard                -- done
//       - TrueFalseCard            -- done
//       - ImageTestCard            -- done
//       - ImageDescCard            -- done
//       - AudioCard
//   - Export Cards Page
//   - Add icons to edit options
//   - Finalize Editpages           -- done
//       - TextCard                 -- done
//       - MultiCard                -- done
//       - TrueFalseCard            -- done
//       - ImageTestCard            -- done
//       - ImageDescCard            -- done
//       - AudioCard                -- done
//   - Fix Scroller class           -- done
//   - Searchbar                    -- done
//   - Searchbar filter             -- done
//   - Category GUIs
//       - Category List            -- done
//       - Single Category View     -- done
//       - Category Tree
//   - Language Class
////////////////////////////////////////////////////

@Slf4j
public class KarteikartenAG 
{
    private static GUI pMainGUI;
    private static final ivec2 iWindowSize = new ivec2(1000, 800);


    public static void main(String[] args)
    {
        Globals.DEBUG_BUILD = true;
        System.setProperty("java.awt.headless", "true"); //for iCrap support

        Output.init();
        Output.setCallback(new OutputCallback() {
            @Override public void onInfo(String msg) 
            {
                log.info(msg);
            }

            @Override public void onWarn(String msg) 
            {
                log.warn(msg);
            }

            @Override public void onError(String msg) 
            { 
                log.error(msg);
            }

            @Override public void onFatal(String msg, int ret) 
            {
                log.error("Fatal: " + msg);
                System.exit(ret);
            }

            @Override public void onDebug(String msg) 
            {
                log.debug(msg);
            }
        });
        Display.init();
        Window pMainWindow = new Window("KarteikartenAG", iWindowSize, Window.GUM_WINDOW_RESIZABLE, null);
        pMainWindow.setVerticalSync(true);
        pMainWindow.setMinimumSize(iWindowSize);

        String usedCharacters = "";
		Font pFontAwesome = Font.loadFontFromResource("fonts/font-awesome6-free-solid-900.otf", usedCharacters);
        FontManager.getInstance().addFont(pFontAwesome, "FontAwesome");


        //
        // Hollow symbols
        //
        String usedRegularCharacters = "";
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

        pMainGUI = new GUI(pMainWindow);
        pMainWindow.setClearColor(GUI.getTheme().backgroundColor); // Set the clear color);
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