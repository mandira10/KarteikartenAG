package com.swp.GUI.Cards;

import java.util.List;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Radiobutton;
import com.gumse.gui.Basics.Speechbubble;
import com.gumse.gui.Basics.Speechbubble.Side;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.swp.Controller.CardController;
import com.swp.DataModel.CardOverview;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.FileDialog;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.Persistence.Exporter;
import com.swp.Persistence.Exporter.ExportFileType;
import com.swp.Controller.SingleDataCallback;

public class CardExportPage extends Page
{
    private CardList pCardList;
    private Speechbubble pBubble;
    private Text pTypeText;
    private Exporter.ExportFileType iFileType;

    public CardExportPage() 
    {
        super("Export Cards", "exportcardspage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/cardexportpage.xml"));

        RenderGUI canvas = findChildByID("canvas");

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), false, new CardListSelectmodeCallback() {
            @Override public void enterSelectmod() {}
            @Override public void exitSelectmod()  {}
        }, null);
        pCardList.setSizeInPercent(true, true);
        canvas.addGUI(pCardList);

        Button exportbutton = (Button)findChildByID("exportbutton");
        exportbutton.onClick((RenderGUI gui) -> { doExport(); });

        Button cancelButton = (Button)findChildByID("cancelbutton");
        cancelButton.onClick((RenderGUI gui) -> { PageManager.viewLastPage(); });

        Button typebutton = (Button)findChildByID("typebutton");
        typebutton.onClick((RenderGUI gui) -> { pBubble.hide(false); });
        typebutton.getBox().getText().setFont(FontManager.getInstance().getFont("FontAwesome"));
        typebutton.getBox().setString("");

        pTypeText = (Text)findChildByID("typetext");

        pBubble = new Speechbubble(new ivec2(15, 0), new ivec2(200, 120), Side.ABOVE);
        pBubble.setColor(vec4.sub(GUI.getTheme().primaryColor, 0.06f));
        pBubble.onClick((RenderGUI gui) -> { /*Prevent clickthrough*/ });
        pBubble.hide(true);
        typebutton.addGUI(pBubble);

        Radiobutton filetypeOptions = new Radiobutton(new ivec2(20, 20), 100, FontManager.getInstance().getDefaultFont(), 20);
        filetypeOptions.addOption("PDF");
        filetypeOptions.addOption("JSON");
        filetypeOptions.addOption("XML");
        
        filetypeOptions.select(0);
        filetypeOptions.singleSelect(true);
        filetypeOptions.setSizeInPercent(true, false);
        filetypeOptions.onSelect((int index, String content) -> {
            pTypeText.setString(content);
            String sym = "";
            switch(index)
            {
                case 0: iFileType = ExportFileType.EXPORT_PDF; sym = ""; break;
                case 1: iFileType = ExportFileType.EXPORT_JSON;           break;
                case 2: iFileType = ExportFileType.EXPORT_XML;            break;
            }
            typebutton.getBox().setString(sym);
        });
        pBubble.addGUI(filetypeOptions);
        pBubble.setSize(new ivec2(120, filetypeOptions.getSize().y + 30));


        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setCards(List<CardOverview> cards)
    {
        pCardList.reset();
        pCardList.addCards(cards, PAGES.CARD_EXPORT);
    }

    private void doExport()
    {
        String filepath = FileDialog.saveFile("Select Destination", "Export Cards", new String[] {"xml", "json", "pdf"});

        if(filepath == null || filepath.isEmpty())
            return;

        CardController.getInstance().exportCards(pCardList.getCards(), filepath, iFileType, new SingleDataCallback<Boolean>() {
            @Override public void onSuccess(Boolean data) 
            {

            }

            @Override public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }
        });
    }
}
