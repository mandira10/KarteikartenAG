package com.swp;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.swp.DataModel.Settings;
import com.swp.DataModel.User;
import com.swp.DataModel.Settings.Setting;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;

import com.gumse.basics.Globals;
import com.gumse.gui.GUI;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Display;
import com.gumse.system.Window;
import com.gumse.system.Window.WindowResizePosCallback;
import com.gumse.tools.Output;
import com.gumse.tools.Output.OutputCallback;
import com.gumse.tools.FPS;
import com.swp.GUI.KarteikartenAGGUI;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.ListOrder;
import com.swp.GUI.Extras.MenuOptions;
import com.swp.GUI.Extras.RatingGUI;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.PageManager.PAGES;
import com.swp.Persistence.PersistenceManager;
import com.swp.Controller.ControllerThreadPool;

import lombok.extern.slf4j.Slf4j;

////////////////////////////////////////////////////
//
// TODO-List
// 
// GUIs
//   - Card List columns            -- done
//   - card references list (links) -- done
//   - card references edit page    -- done
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
//   - Show deck progress           -- done
//   - Final testpage               -- done
//   - Test rating                  -- done
//   - Studysystem editpage         -- done
//   - Studysystem dropdown         -- done
//   - Finalize Viewpages           -- done
//       - TextCard                 -- done
//       - MultiCard                -- done
//       - TrueFalseCard            -- done
//       - ImageTestCard            -- done
//       - ImageDescCard            -- done
//       - AudioCard                -- done
//   - Export Cards Page            -- done
//   - Add Single Cards to Deck     -- done
//   - Add Cards to Deck by category-- done
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
//   - Category GUIs                -- done
//       - Category List            -- done
//       - Single Category View     -- done
//       - Category Tree            -- done
//   - Fix Hierarchy List           -- done
//   - Theming                      -- done
//   - Language Class               -- done
//   - Language files               -- done
//   - Settings file                -- done
//   - Threedots optionsmenu        -- done
//   - Auto resizing menubar        -- done
//   - Custom XML types             -- done
//   - Check for mandatory fields   -- done
//   - Change cardlist order        -- done
//   - Fix deck tests               -- done
//       - get correct answer       -- done
//       - Timer auto cancel        -- done
//   - Loginpage                    -- done
//   - Settings logout              -- done
//   - German language              -- done
//   - Deck edit page               -- done
//       - Time field               -- done
//       - Vote field               -- done
//       - Leitner fields           -- done
//   - Fix notification gui         -- done
//   - Auto add file extensions     -- done
//   - viewpage callback            -- done
//       - stop audio               -- done
//   - fix imagedesc answerentries  -- done
//   - fix imagedesc answercheck
//   - Fix browser/fileexplorer
//   - javadoc
//   - fix aspectratio
//       - Tests
//       - Imagedescanswers
//       - Viewpage

// Missing:
// - get categories by name
// - get cards/categories in different order

////////////////////////////////////////////////////

@Slf4j
public class KarteikartenAG 
{
    private static GUI pMainGUI;
    private static final ivec2 iWindowSize = new ivec2(1000, 800);
    private static final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    public static void main(String[] args)
    {
        Globals.DEBUG_BUILD = true;
        System.setProperty("java.awt.headless", "true");

        PersistenceManager.init();
        Output.init();
        Output.setCallback(new OutputCallback() {
            @Override public void onInfo(String msg)  { log.info(msg); }
            @Override public void onWarn(String msg)  { log.warn(msg); }
            @Override public void onError(String msg) { log.error(msg); }
            @Override public void onDebug(String msg) { log.debug(msg); }
            @Override public void onFatal(String msg, int ret) {
                log.error("Fatal: " + msg);
                System.exit(ret);
            }
        });
        Display.init();
        Window pMainWindow = new Window("KarteikartenAG", iWindowSize, Window.GUM_WINDOW_RESIZABLE, null);
        pMainWindow.setVerticalSync(true);
        pMainWindow.setMinimumSize(iWindowSize);

        String usedCharacters = "";
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
		pMainWindow.onResized(new WindowResizePosCallback() {
            @Override public void run(ivec2 val) {
                pMainGUI.setSize(val);
            }
        });

        XMLGUI.addGUIType("menu", MenuOptions.createFromXMLNode());
        XMLGUI.addGUIType("searchbar", Searchbar.createFromXMLNode());
        XMLGUI.addGUIType("list-order", ListOrder.createFromXMLNode());
        XMLGUI.addGUIType("rating", RatingGUI.createFromXMLNode());
        
        Settings.getInstance().getLanguage().activate();

        //Check for saved login
        User.loginUser(new User(Settings.getInstance().getSetting(Setting.USER_NAME), Settings.getInstance().getSetting(Setting.USER_PASSWD)));
        
        KarteikartenAGGUI pKarteikartenAGGUI = KarteikartenAGGUI.getInstance();
        pKarteikartenAGGUI.setSize(new ivec2(100, 100));
        pKarteikartenAGGUI.setSizeInPercent(true, true);
        pMainGUI.addGUI(pKarteikartenAGGUI);
        

        //Debugging
        //TextBox fpsTextBox = null;
        //if(Globals.DEBUG_BUILD)
        //{
        //    fpsTextBox = new TextBox("FPS: ", FontManager.getInstance().getDefaultFont(), new ivec2(0, 0), new ivec2(100, 30));
        //    //fpsTextBox.setPositionInPercent(true, true);
        //    fpsTextBox.setColor(new vec4(0.0f, 0.0f, 0.0f, 0.3f));
        //    fpsTextBox.setAlignment(TextBox.Alignment.LEFT);
        //    //fpsTextBox.setOrigin(fpsTextBox.getSize());
        //    pMainGUI.addGUI(fpsTextBox);
        //}

        pMainGUI.setSize(iWindowSize);

        if(User.isLoggedIn())
            PageManager.viewPage(PAGES.DECK_OVERVIEW);
        else
            PageManager.viewPage(PAGES.LOGIN);

        
        while(pMainWindow.isOpen())
        {
            Display.pollEvents();
            pMainWindow.setClearColor(GUI.getTheme().backgroundColor);
            pMainWindow.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            pMainGUI.render();
            pMainGUI.update();
            threadPool.runQueue();
            //if(fpsTextBox != null)
            //    fpsTextBox.setString("FPS: " + (int)FPS.getFPS());

            pMainWindow.finishRender();
            pMainWindow.getMouse().reset();
            pMainWindow.getKeyboard().reset();
            FPS.update();
        }
        threadPool.runQueue();
        PersistenceManager.close();
        System.exit(0); // Hack
    }
}