package com.swp.GUI.Extras;

import com.gumse.gui.HierarchyList.HierarchyList;
import com.gumse.gui.HierarchyList.HierarchyListEntry;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.io.Mouse;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.DataModel.Category;
import com.swp.GUI.Category.ViewSingleCategoryPage;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;

import java.util.ArrayList;
import java.util.List;

/**
 * CategoryList wird dazu verwendet, um
 * eine gegebene liste von Kategorien anzuzeigen.
 * 
 * @author Tom Beuke
 */
public class CategoryList extends RenderGUI
{
    /**
     * Gibt die ausgewählte Kategorie wieder
     */
    public interface CategoryListCallback
    {
        /**
         * Wird ausgeführt, wenn auf eine Kategorie gedrückt wird
         * @param category Die ausgewählte Kategorie
         */
        void run(Category category);
    }

    /**
     * Das Callback interface welches den Status des Auswahlmodus wiedergibt
     */
    public interface CategoryListSelectmodeCallback
    {
        /**
         * Wird beim Aktivieren des Auswahlmodus ausgeführt
         */
        void enterSelectmode();

        /**
         * Wird beim Deaktivieren des Auswahlmodus ausgeführt
         */
        void exitSelectmode();
    }

    private boolean bIsInSelectmode;
    private final CategoryListSelectmodeCallback pSelectmodeCallback;
    private final HierarchyList<Category> pList;

    /**
     * Der Hauptkonstruktor der Klasse CategoryList
     *
     * @param pos                Position des GUIs in Pixeln
     * @param size               Größe des GUIs in Pixeln
     * @param selectmodeCallback Das Auswahlmodus Callbackinterface
     */
    public CategoryList(ivec2 pos, ivec2 size, CategoryListSelectmodeCallback selectmodeCallback)
    {
        this.vPos.set(pos);
        this.vSize.set(size);
        this.pSelectmodeCallback = selectmodeCallback;

        pList = new HierarchyList<>(new ivec2(0, 0), new ivec2(100, 100), "Categories", "Root", false, true, "categorylisttitle");
        pList.setSizeInPercent(true, true);
        pList.onEntryClick((RenderGUI gui) -> {
            HierarchyListEntry<Category> entry = (HierarchyListEntry<Category>)gui;

            if(bIsInSelectmode)
            {
                entry.tick(!entry.isTicked());
                updateSelectmode();
            }
            else
            {
                if(entry.getUserPtr() != null)
                    ((ViewSingleCategoryPage)PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW)).setCategory(entry.getUserPtr());
            }
            pList.selectEntry(null);
        });

        pList.onTick((boolean ticked) -> {
            updateSelectmode();
        });

        addElement(pList);

        resize();
        reposition();
    }

    /**
     * Überprüft, ob die Kategorienliste sich im Auswahlmodus befinden soll
     */
    public void updateSelectmode()
    {
        List<HierarchyListEntry<Category> > foundEntries = pList.getTickedEntries();

        if(foundEntries.size() > 0)
        {
            if(!bIsInSelectmode && pSelectmodeCallback != null)
                pSelectmodeCallback.enterSelectmode();
            bIsInSelectmode = true;

            return;
        }

        if(bIsInSelectmode && pSelectmodeCallback != null)
            pSelectmodeCallback.exitSelectmode();
        bIsInSelectmode = false;
    }


    /**
     * Setzt die Liste zurück
     */
    public void reset()
    {
        pList.reset();
        updateSelectmode();
    }

    /**
     * Fügt eine Kategorie zur Liste hinzu
     *
     * @param category  Die hinzuzufügende Kategorie
     * @param listentry Der Parent Listeneintrag
     */
    public synchronized void addCategory(Category category, HierarchyListEntry<Category> listentry)
    {
        if(listentry == null)
            listentry = pList.getRootEntry();

        final HierarchyListEntry<Category> entry = new HierarchyListEntry<>(category.getName(), pList, category);
        entry.onHover(null, Mouse.GUM_CURSOR_HAND);
        CategoryController.getInstance().getChildrenForCategory(category, new DataCallback<Category>() {
            @Override public void onFailure(String msg) {}
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> categories) 
            {
                for(Category cat : categories)
                    addCategory(cat, entry);
            }
        });

        listentry.addEntry(entry);
        pList.repositionEntries();
        pList.selectEntry(null);
    }

    public List<Category> getSelectedCategories()
    {
        List<Category> retList = new ArrayList<>();
        pList.getTickedEntries().forEach((HierarchyListEntry<Category> entry) -> {
            if(entry.getUserPtr() != null)
                retList.add(entry.getUserPtr());
        });

        return retList;
    }
}
