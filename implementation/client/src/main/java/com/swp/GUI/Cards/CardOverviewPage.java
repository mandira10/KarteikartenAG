package com.swp.GUI.Cards;

import java.util.List;
import java.util.stream.Collectors;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.CardList;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.CardList.CardListSelectmodeCallback;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.GUI.PageManager.PAGES;
import com.swp.Persistence.DataCallback;

public class CardOverviewPage extends Page 
{
    private Searchbar pSearchbar;
    private CardList pCardList;

    public CardOverviewPage()
    {
        super("Card Overview");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/cardoverviewpage.xml"));

        RenderGUI optionsMenu = findChildByID("menu");

        Button addCardButton = (Button)findChildByID("addcardbutton");
        addCardButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                PageManager.viewPage(PAGES.CARD_CREATE);
            }
        });

        Button deleteCardButton = (Button)findChildByID("deletecardbutton");
        deleteCardButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                deleteCards();
            }
        });
        deleteCardButton.hide(true);


        RenderGUI canvas = findChildByID("canvas");

        pCardList = new CardList(new ivec2(0, 0), new ivec2(100, 100), new CardListSelectmodeCallback() {
            @Override public void enterSelectmod() { deleteCardButton.hide(false); }
            @Override public void exitSelectmod()  { deleteCardButton.hide(true); }
        });
        pCardList.setSizeInPercent(true, true);
        canvas.addGUI(pCardList);

        pSearchbar = new Searchbar(new ivec2(20, 100), new ivec2(40, 30), "Search Card", new SearchbarCallback() {
            @Override public void run(String query) 
            {
                Debug.info(query);
                if(query.equals(""))
                    loadCards(0, 30);
                else
                    loadCards(query);
            }
        });
        pSearchbar.setPositionInPercent(false, true);
        pSearchbar.setSizeInPercent(true, false);
        pSearchbar.setOrigin(new ivec2(0, 50));
        addGUI(pSearchbar);

        this.setSizeInPercent(true, true);
        reposition();
    }

    public void loadCards(int from, int to)
    {
        if(from == 0)
            pCardList.reset();
        CardController.getCardsToShow(from, to, new DataCallback<Card>() {
            @Override public void onSuccess(List<Card> data) 
            {
                pCardList.addCards(data.stream().collect(Collectors.toSet()));
            }

            @Override public void onFailure(String msg) 
            {
                NotificationGUI.addNotification(msg, NotificationType.ERROR, 5);     
            }
            
        });
    }

    public void loadCards(String str)
    {
        pCardList.reset();
        pCardList.addCards(CardController.getCardsBySearchterms(str));
    }

    public void deleteCards()
    {
        int numCards = pCardList.getSelection().size();
        ConfirmationGUI.openDialog("Are you sure that you want to delete " + String.valueOf(numCards) + " cards?", new ConfirmationCallback() {
            @Override public void onConfirm() 
            {  
                CardController.deleteCards(pCardList.getSelection());
            }
            @Override public void onCancel() 
            {
            }
        });
    }

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
