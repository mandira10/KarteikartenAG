package com.swp.GUI.Category;

import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.Decks.DeckSelectPage;
import com.swp.GUI.Extras.CategoryList;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.CategoryList.CategoryListSelectmodeCallback;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.Extras.ListOrder;
import com.swp.GUI.Extras.ListOrder.Order;
import com.swp.GUI.Extras.MenuOptions;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;
import com.swp.Logic.CategoryLogic;

import java.util.List;

public class CategoryOverviewPage extends Page
{
    private RenderGUI pCanvas;
    private CategoryList pCategoryList;
    private Button pDeleteCategoriesButton;
    private Button pAddToDeckButton;
    private Button pExportButton;
    private boolean bReverseOrder;

    private static final CategoryController categoryController = CategoryController.getInstance();
   
    public CategoryOverviewPage()
    {
        super("Categories", "categoryoverviewpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryoverviewpage.xml"));
        

        MenuOptions menu = (MenuOptions)findChildByID("menu");

        // Tree Button
        Button treeButton = (Button)findChildByID("treeviewbutton");
        treeButton.onClick((RenderGUI gui) -> { ((ViewCategoryTreePage)PageManager.viewPage(PAGES.CATEGORY_TREE)).reset(); });

        // New Button
        Button newButton = (Button)findChildByID("addcategorybutton");
        newButton.onClick((RenderGUI gui) -> { ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).editCategory(new Category(),true); });

        // Delete Button
        pDeleteCategoriesButton = (Button)findChildByID("deletecategoriesbutton");
        pDeleteCategoriesButton.onClick((RenderGUI gui) -> { deleteCategories(pCategoryList.getSelectedCategories()); });
        pDeleteCategoriesButton.hide(true);

        // Add to deck Button
        pAddToDeckButton = (Button)findChildByID("addtodeckbutton");
        pAddToDeckButton.onClick((RenderGUI gui) -> { addToDeck(); });
        pAddToDeckButton.hide(true);

        // Export Button
        pExportButton = (Button)findChildByID("exportbutton");
        pExportButton.onClick((RenderGUI gui) -> { exportCards(); });
        pExportButton.hide(true);

        pCanvas = findChildByID("canvas");
        pCategoryList = new CategoryList(new ivec2(0, 0), new ivec2(100, 100), new CategoryListSelectmodeCallback() {
            @Override public void enterSelectmod() { pExportButton.hide(false); pDeleteCategoriesButton.hide(false); pAddToDeckButton.hide(false); menu.resize(); }
            @Override public void exitSelectmod()  { pExportButton.hide(true);  pDeleteCategoriesButton.hide(true);  pAddToDeckButton.hide(true);  menu.resize(); }
        });
        pCategoryList.setSizeInPercent(true, true);
        pCanvas.addGUI(pCategoryList);

        Searchbar searchbar = (Searchbar)findChildByID("searchbar");
        searchbar.setCallback((String query, int option) -> { loadCategories(query); });


        ListOrder listorder = (ListOrder)findChildByID("listorder");
        listorder.setCallback((Order order, boolean reverse) -> {
            bReverseOrder = reverse;
            loadCategories();
        });

        this.setSizeInPercent(true, true);
        reposition();
    }
    
    public void loadCategories()
    {
        categoryController.getRootCategories(bReverseOrder, new DataCallback<Category>() {
            @Override public void onFailure(String msg) { NotificationGUI.addNotification(msg, NotificationType.ERROR, 5); }
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> categories) 
            {
                pCategoryList.reset();
                for(Category cat : categories)
                    pCategoryList.addCategory(cat, null);
            }
        });
    }
    
    public void loadCategories(String searchterm)
    {

        pCategoryList.reset();

       categoryController.getCategoriesBySearchterm(searchterm, new DataCallback<Category>() {
           @Override
           public void onSuccess(List<Category> data) {
              // pCategoryList.addCategories TODO wie machen wir das mit der Hierarchie?? Oder soll das nur für Roots sein? Dann muss ich das noch umschreiben
           }

           @Override
           public void onFailure(String msg) {
            NotificationGUI.addNotification(msg,NotificationType.ERROR,10);
           }

           @Override
           public void onInfo(String msg) {

           }
       });
    }

    private void deleteCategories(List<Category> categories)
    {
        ConfirmationGUI.openDialog(Locale.getCurrentLocale().getString("confirmdeletecategories").replace("$", String.valueOf(categories.size())), new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                CategoryController.getInstance().deleteCategories(categories, new SingleDataCallback<Boolean>() {
                    @Override public void onSuccess(Boolean data) {

                    }

                    @Override public void onFailure(String msg) {
                        NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
                    }
                });
                ((CategoryOverviewPage)PageManager.viewPage(PAGES.CATEGORY_OVERVIEW)).loadCategories();
            }
        });
    }

    public void addToDeck()
    {
        CategoryController.getInstance().getCardsInCategories(pCategoryList.getSelectedCategories(), new DataCallback<CardOverview>() {
            @Override public void onSuccess(List<CardOverview> cards) 
            {
                ((DeckSelectPage)PageManager.viewPage(PAGES.DECK_SELECTION)).reset(cards);
            }

            @Override public void onFailure(String msg) 
            {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR,5);
            }

            @Override public void onInfo(String msg) 
            {
            }
        });
    }

    private void exportCards()
    {
        CategoryController.getInstance().getCardsInCategories(pCategoryList.getSelectedCategories(), new DataCallback<CardOverview>() {
            @Override
            public void onInfo(String msg) {}
            @Override public void onSuccess(List<CardOverview> cards) {
                ((CardExportPage)PageManager.viewPage(PAGES.CARD_EXPORT)).setCards(cards);
            }

            @Override
            public void onFailure(String msg)
            {
                NotificationGUI.addNotification(msg, NotificationType.ERROR, 5);
            }
        });
    }
}
