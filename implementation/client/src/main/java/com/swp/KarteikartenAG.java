package com.swp;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.GUI.KarteikartenAGGUI;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CategoryRepository;
import com.swp.Persistence.DeckRepository;
import com.swp.Persistence.SingleDataCallback;

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
//   - Deck editpage                -- done
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
//   - Add Single Cards to Deck     -- done
//   - Add Cards to Deck by category
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
//   - Theming                      -- done
//   - Language Class               -- done
//   - Language enum
//   - Settings file                -- done
////////////////////////////////////////////////////

@Slf4j
public class KarteikartenAG 
{
    private static GUI pMainGUI;
    private static final ivec2 iWindowSize = new ivec2(1000, 800);


    public static void main(String[] args)
    {
        Globals.DEBUG_BUILD = true;
        System.setProperty("java.awt.headless", "true");

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
		pMainWindow.onResized(new WindowResizePosCallback() {
            @Override public void run(ivec2 val) {
                pMainGUI.setSize(val);
            }
        });

        KarteikartenAGGUI pKarteikartenAGGUI = KarteikartenAGGUI.getInstance();
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

        pMainGUI.setSize(iWindowSize);
        importTestData();

        
        while(pMainWindow.isOpen())
        {
            Display.pollEvents();
            pMainWindow.setClearColor(GUI.getTheme().backgroundColor);
            pMainWindow.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            pMainGUI.render();
            pMainGUI.update();
            if(fpsTextBox != null)
                fpsTextBox.setString("FPS: " + (int)FPS.getFPS());

            pMainWindow.finishRender();
            pMainWindow.getMouse().reset();
            pMainWindow.getKeyboard().reset();
            FPS.update();
        }
    }


    private static void importTestData() 
    {
        Category randomCategory   = new Category("Random");
        Category schuleCategory   = new Category("Schule");
        Category technikCategory  = new Category("Technik");
        Category spanischCategory = new Category("Spanisch");
        Category erdkundeCategory = new Category("Erdkunde");

        //schuleCategory.addChild(spanischCategory); //Crashes
        //schuleCategory.addChild(erdkundeCategory); //Crashes

        for(Category category : new Category[] {randomCategory, schuleCategory, technikCategory, spanischCategory, erdkundeCategory})
        {
            CategoryController.updateCategoryData(category, true, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });
        }


        List<Card> randomL = new ArrayList<>();
        //randomL.add(new ImageDescriptionCard("Some Image Description Question", new ImageDescriptionCardAnswer[]{}, "ImageDescriptionTitle", "textures/orange-ket.png", false));
        randomL.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", "textures/orange-ket.png", "ImageTestCardTitle", false, true));

        for (Card c : randomL) 
        {
            CardController.updateCardData(c, true, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });
            CardController.setTagsToCard(c, new HashSet<Tag>() {
                {
                    add(new Tag("toll"));
                    add(new Tag("cool"));
                }
            }, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });

            CategoryController.setCategoriesToCard(c, new HashSet<Category>() {
                { add(randomCategory); }
            });
        }


        List<Card> erdkundeL = new ArrayList<>();
        //Karten 2: Erdkunde Fragen MC
        erdkundeL.add(new MultipleChoiceCard("Welches der folgenden ist das flächenmäßig kleinste Bundesland?", new String[]{"Thüringen", "Sachsen", "Saarland"}, new int[]{2}, "Bundesländer Größe", false));
        erdkundeL.add(new MultipleChoiceCard("Welcher See liegt nicht in Bayern?", new String[]{"Ammersee", "Schweriner See", "Starnberger See"}, new int[]{0}, "Sees in Bayern", false));
        erdkundeL.add(new TextCard("Über welche Länge erstreckt sich das Uralgebirge?", "2.400 Kilometer", "Strecke des Uralgebirges", false));
        erdkundeL.add(new MultipleChoiceCard("Welche dieser Städte liegt am nördlichsten?", new String[]{"Adelaide", "Perth", "Melbourne", "Brisbane"}, new int[]{3}, "Nördlichste Stadt Australiens", false));
        //erdkundeL.add(new TrueFalseCard("Die USA hat insgesamt 50 Bundesstaaten", true, "USA Bundesstaaten", false));

        for (Card c : erdkundeL) 
        {
            CardController.updateCardData(c, true, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });
            CardController.setTagsToCard(c, new HashSet<Tag>() {
                {
                    add(new Tag("Deutschland"));
                    add(new Tag("Australien"));
                    add(new Tag("Länder"));
                }
            }, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });
            CategoryController.setCategoriesToCard(c, new HashSet<Category>() {
                {
                    add(erdkundeCategory);
                    add(schuleCategory);
                }
            });
        }


        List<Card> spanischL = new ArrayList<>();
        //Karten 2: Spanisch
        spanischL.add(new AudioCard(null, "Comer", "Comer", "Essen", false, true));
        spanischL.add(new AudioCard(null, "Trinken", "Trinken", "Beber", false, true));
        spanischL.add(new AudioCard(null, "Taufen", "Taufen", "bautizar", false, true));

        for (Card c : spanischL) 
        {
            CardController.updateCardData(c, true, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });
            CardController.setTagsToCard(c, new HashSet<Tag>() {
                {
                    add(new Tag("Essen"));
                    add(new Tag("Anderes"));
                }
            }, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });

            CategoryController.setCategoriesToCard(c, new HashSet<Category>() {
                {
                    add(spanischCategory);
                    add(schuleCategory);
                }
            });
        }


        List<Card> technikL = new ArrayList<>();
        //Karten 3: Technik Quiz
        technikL.add(new MultipleChoiceCard("Mag sein, dass das iPhone 2007 die Ära der Smartphones erst so richtig begründet hat. Aber das IBM Simon (hier in der Mitte) war tatsächlich das allererste Smartphone mit Touch Display. Wann kam es auf den Markt??", new String[]{"1989", "1994", "2001"}, new int[]{1}, "Erstes Smartphone", false));
        technikL.add(new MultipleChoiceCard("Google Earth ist weltweit bekannt. Doch eine Berliner Agentur schuf bereits Anfang der Neunziger ein ähnliches Produkt, wie hieß der digitale Globus der Berliner??", new String[]{"Terravision", "Globetrotter", "Digiglobe"}, new int[]{0}, "Das erste Google Earth", false));
        //technikL.add(new TrueFalseCard("Logitech produzierte die erste echte Computer-Maus", false, "Produzent erster Computer-Maus", false));
        //technikL.add(new TrueFalseCard("http steht für Hypertext Transfer Protocol", false, "", false));

        for (Card c : technikL) 
        {
            CardController.updateCardData(c, true, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });
            CardController.setTagsToCard(c, new HashSet<Tag>() {
                {
                    add(new Tag("Google Earth"));
                    add(new Tag("Technikmarken"));
                }
            }, new SingleDataCallback<Boolean>() {
                @Override public void onSuccess(Boolean data) {}
                @Override public void onFailure(String msg) {}
            });

            CategoryController.setCategoriesToCard(c, new HashSet<Category>() {
                {
                    add(technikCategory);
                    add(schuleCategory);
                }
            });
        }

    }
}