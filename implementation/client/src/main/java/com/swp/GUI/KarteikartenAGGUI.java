package com.swp.GUI;

import com.gumse.gui.GUI;
import com.gumse.gui.Theme;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.*;
import com.gumse.tools.Output;
import com.swp.DataModel.Settings.Setting;
import com.swp.Controller.CategoryController;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Category;
import com.swp.DataModel.Settings;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Category.ViewCategoryTreePage;
import com.swp.GUI.Decks.DeckOverviewPage;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.NotificationGUI;

public class KarteikartenAGGUI extends RenderGUI
{
    private Sidebar pSidebar;
    private NotificationGUI pNotifications;
    private ConfirmationGUI pConfirmations;
    private Theme pLightTheme;

    private static KarteikartenAGGUI pInstance;


    private KarteikartenAGGUI()
    {
        PageManager.init(this);
        hideChildren(true); //Manually render

        
        GUI.getDefaultTheme().cornerRadius = new vec4(7,7,7,7);
        pLightTheme = new Theme();
        pLightTheme.backgroundColor   = vec4.div(Color.HEXToRGBA("#FFFFFF"), 255.0f);
        pLightTheme.primaryColor      = vec4.div(Color.HEXToRGBA("#FFFFFF"), 255.0f);
        pLightTheme.primaryColorShade = vec4.div(Color.HEXToRGBA("#CCCCCC"), 255.0f);
        pLightTheme.secondaryColor    = vec4.div(Color.HEXToRGBA("#A6C7E5"), 255.0f);
        pLightTheme.accentColor       = vec4.div(Color.HEXToRGBA("#0F79D9"), 255.0f);
        pLightTheme.accentColorShade1 = vec4.div(Color.HEXToRGBA("#A6C7E5"), 255.0f);
        pLightTheme.textColor         = vec4.div(Color.HEXToRGBA("#121416"), 255.0f);
        pLightTheme.cornerRadius      = new vec4(0,0,0,0);
        pLightTheme.borderThickness   = 1;


        if(!Settings.getInstance().getSetting(Setting.DARK_THEME).equals("true")) 
            GUI.setTheme(pLightTheme);
        updateTheme();

        /*ImageDescriptionCardAnswer[] answers = new ImageDescriptionCardAnswer[] {
            new ImageDescriptionCardAnswer("Orangenblatt", 75, 5),
            new ImageDescriptionCardAnswer("Orange",       61, 26),
            new ImageDescriptionCardAnswer("Nase",         40, 67),
            new ImageDescriptionCardAnswer("Hand",         82, 58),
            new ImageDescriptionCardAnswer("Fuß",          62, 89),
        };
        ImageDescriptionCard card = new ImageDescriptionCard("What is orange ket?", answers, "Importance of kets", "textures/orange-ket.png", true);

        //EditCardPage page = (EditCardPage)PageManager.getPage(PAGES.CARD_EDIT);
        //page.editCard(card);
        //PageManager.viewPage(PAGES.CARD_EDIT);

        //ViewSingleCardPage page = (ViewSingleCardPage)PageManager.getPage(PAGES.CARD_SINGLEVIEW);
        //page.setCard(card);
        */
        ((DeckOverviewPage)PageManager.viewPage(PAGES.DECK_OVERVIEW)).loadDecks();

        PageManager.viewPage(PAGES.CATEGORY_OVERVIEW);


        pConfirmations = ConfirmationGUI.getInstance();
        addElement(pConfirmations);
        
        pNotifications = NotificationGUI.getInstance();
        addElement(pNotifications);


        //NotificationGUI.addNotification("Info Message with a little more text test test test test test test test", NotificationType.INFO, 5);
        //NotificationGUI.addNotification("Warning Message", NotificationType.WARNING, 10);
        //NotificationGUI.addNotification("Error Message", NotificationType.ERROR, 5);
        //NotificationGUI.addNotification("Critical Error Message", NotificationType.CRITICAL, 5);
        //NotificationGUI.addNotification("Debug Message", NotificationType.DEBUG, 3);
        //NotificationGUI.addNotification("Some Connection Message/Error", NotificationType.CONNECTION, 3);

        pSidebar = new Sidebar();
        pSidebar.setSize(new ivec2(60, 100));
        pSidebar.setSizeInPercent(false, true);
        addElement(pSidebar);
    }


    @Override
    public void renderextra()
    {
        PageManager.render();
        pSidebar.render();
        pConfirmations.render();
        pNotifications.render();
    }

    @Override
    public void updateextra()
    {
        pConfirmations.update();
        if(pConfirmations.isHidden())
        {
            pNotifications.update();
            pSidebar.update();
            PageManager.update();
        }
    }

    public static KarteikartenAGGUI getInstance()
    {
        if(pInstance == null)
            pInstance = new KarteikartenAGGUI();
        return pInstance;
    }

    public Theme getLightTheme()
    {
        return pLightTheme;
    }
}