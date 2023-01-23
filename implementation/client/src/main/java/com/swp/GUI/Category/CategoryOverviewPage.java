package com.swp.GUI.Category;

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
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.GUI.PageManager.PAGES;

import java.util.List;

public class CategoryOverviewPage extends Page
{
    RenderGUI pCanvas;
    CategoryList pCategoryList;
    
    private static final CategoryController categoryController = CategoryController.getInstance();
   
    public CategoryOverviewPage()
    {
        super("Categories", "categoryoverviewpage");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/categories/categoryoverviewpage.xml"));
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button treeButton = (Button)optionsMenu.findChildByID("treeviewbutton");
        treeButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                ((ViewCategoryTreePage)PageManager.viewPage(PAGES.CATEGORY_TREE)).reset();
            }
        });

        Button newButton = (Button)optionsMenu.findChildByID("addcategorybutton");
        newButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui)  
            {
                ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).editCategory(new Category(),true);
            }
        });
        Button deleteCategoriesButton = (Button)findChildByID("deletecategoriesbutton");
        deleteCategoriesButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                deleteCategories(pCategoryList.getSelectedCategories());
            }
        });
        deleteCategoriesButton.hide(true);

        Button addToDeckButton = (Button)findChildByID("addtodeckbutton");
        addToDeckButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                addToDeck();
            }
        });
        addToDeckButton.hide(true);

        pCanvas = findChildByID("canvas");
        pCategoryList = new CategoryList(new ivec2(0, 0), new ivec2(100, 100), new CategoryListSelectmodeCallback() {
            @Override public void enterSelectmod() { deleteCategoriesButton.hide(false); addToDeckButton.hide(false); }
            @Override public void exitSelectmod()  { deleteCategoriesButton.hide(true); addToDeckButton.hide(true); }
        });
        pCategoryList.setSizeInPercent(true, true);
        pCanvas.addGUI(pCategoryList);

        Searchbar searchbar = new Searchbar(new ivec2(20, 100), new ivec2(40, 30), "categorysearch", new String[] {
            "bycontentsearch",
        }, new SearchbarCallback() {
            @Override public void run(String query, int option) 
            {
                loadCategories(query);
            }
        });
        searchbar.setPositionInPercent(false, true);
        searchbar.setSizeInPercent(true, false);
        searchbar.setOrigin(new ivec2(0, 50));
        addGUI(searchbar);

        this.setSizeInPercent(true, true);
        reposition();
    }
    
    public void loadCategories()
    {
        pCategoryList.reset();
        categoryController.getRootCategories(new DataCallback<Category>() {
            @Override public void onFailure(String msg) { NotificationGUI.addNotification(msg, NotificationType.ERROR, 5); }
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> data) 
            {
                for(Category cat : data)
                {
                    pCategoryList.addCategory(cat, null);
                }
            }
        });
    }
    
    public void loadCategories(String searchterm)
    {
        pCategoryList.reset();
        //pCategoryList.addCategories(CategoryController.getCategoriesBySearchterm(searchterm));
    }

    private void deleteCategories(List<Category> categories)
    {
        ConfirmationGUI.openDialog("Are you sure that you want to delete " + categories.size() + " categories?", new ConfirmationCallback() {
            @Override public void onCancel() {}
            @Override public void onConfirm() 
            {  
                CategoryController.getInstance().deleteCategories(categories, new SingleDataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {

                    }

                    @Override
                    public void onFailure(String msg) {
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
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
