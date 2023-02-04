package com.swp.Controller;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.GUI.Extras.ListOrder;
import com.swp.Logic.CategoryLogic;

import java.util.List;

/**
 * CategoryController Klasse. Wird in der GUI für alle Funktionen zur Kategorie aufgerufen und
 * gibt Ergebnisse an die GUI weiter.
 */
public class CategoryController extends Controller
{
    /**
     * Instanz von CategoryController
     */
    private static CategoryController categoryController;

    /**
     * Hilfsmethode. Wird genutzt, damit nicht mehrere Instanzen vom CategoryController entstehen.
     *
     * @return categoryController Instanz, die benutzt werden kann.
     */
    public static CategoryController getInstance() 
    {
        if (categoryController == null)
            categoryController = new CategoryController();
        return categoryController;
    }

    /**
     * Benutze Logiken, auf die zugegriffen wird.
     */
    private final CategoryLogic categoryLogic = CategoryLogic.getInstance();


    /**
     * Wird verwendet, um Data für eine Kategorie zu aktualisieren. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu aktualisieren
     * @param neu  Ob, die Kategorie neue oder nicht ist zu verstehen
     * @param callback  Callback für die GUI gib bei Fehler die Exception message weiter
     */
    public void updateCategoryData(Category category, boolean neu, boolean nameChange, SingleDataCallback<Boolean> callback) 
    {
        String categoryname = category != null ? category.getName() : "";
        callLogicFuncInThread(
            () -> { categoryLogic.updateCategoryData(category, neu, nameChange); return true; },
            "getcardstoshowempty", "Es wurden keine zugehörigen Karten gefunden", 
            "categoryupdatesaveerror", "Beim Updaten/Speichern der Kategorie "+categoryname+" ist ein Fehler $ aufgetreten",
            callback,"");
    }

    /**
     * Wird verwendet, um Hierarchy der Kategorie zu bearbeiten. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu bearbeiten
     * @param parents   die Liste der Parents Kategorien für diese Kategorie
     * @param children  die Liste der Children Kategorien für diese Kategorie
     * @param callback  Callback für die GUI, gibt bei Fehler die Exception message weiter
     */
    public void editCategoryHierarchy(Category category, List<Category> parents, List<Category> children, SingleDataCallback<String> callback) 
    {
        callLogicFuncInThread(
            () -> { 
                return categoryLogic.editCategoryHierarchy(category, parents, children) 
                    ? Locale.getCurrentLocale().getString("selfreferenceerror") 
                    : "";
            }, 
            "", "", 
            "categoryhierarchyupdateerror", "Beim Updaten der Kategorie ist der Fehler $ aufgetreten", 
            callback,"");
    }

    /**
     * Wird verwendet, um eine Kategorie zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu löschen
     * @param callback Callback für die GUI, gibt bei Fehler die Exception message weiter
     */
    public void deleteCategory(Category category, SingleDataCallback<Boolean> callback) 
    {
        String categoryname = category != null ? category.getName() : "";
        callLogicFuncInThread(
            () -> { categoryLogic.deleteCategory(category); return true; }, 
            "", "", 
            "deletecategoryerror", "Beim Löschen der Kategorie "+categoryname+" ist ein Fehler $ aufgetreten", 
            callback, "");
    }

    /**
     * Wird verwendet, um mehrere Kategorien zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param categories  die Liste der Kategorien zu löschen
     * @param callback  Callback für die GUI, gibt bei Fehler die Exception message.
     *
     */
    public void deleteCategories(List<Category> categories, SingleDataCallback<Boolean> callback)
    {
        String categoriesstr = categories != null ? categories.toString() : "";
        callLogicFuncInThread(
            () -> { categoryLogic.deleteCategories(categories); return true; },
            "", "",
            "deletecategorieserror", "Beim Löschen den Kategorien "+categoriesstr+" ist ein Fehler $ aufgetreten",
            callback, "");
    }


    /**
     * Wird verwendet, um die Children einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     *
     * @param parent  die Parent Kategorie, um die Children zu bekommen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getChildrenForCategory(Category parent, DataCallback<Category> callback) 
    {
        String categoryname = parent != null ? parent.getName() : "";
        callLogicFuncInThread(
            () -> { return categoryLogic.getChildrenForCategory(parent); }, 
            "", "", 
            "catchilderror", "Beim Beim Abrufen der Children für die Kategorie "+categoryname+" ist ein Fehler $ aufgetreten", 
            callback, "");
    }


    /**
     * Wird verwendet, um die Parents einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     *
     * @param child  die Child Kategorie, um die Parents zu bekommen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getParentsForCategory(Category child, DataCallback<Category> callback) 
    {
        String categoryname = child != null ? child.getName() : "";
        callLogicFuncInThread(
            () -> { return categoryLogic.getParentsForCategory(child); }, 
            "", "", 
            "catparenterror", "Beim Beim Abrufen der Parents für die Kategorie "+categoryname+" ist ein Fehler $ aufgetreten", 
            callback, "");
    }

    /**
     * Wird verwendet, um einzelne Kategorieinformationen über ihre UUID abzurufen. Wird an die CategoryLogic weitergegeben.
     *
     * @param uuid  UUID der abzurufenden Kategorie
     * @param callback  Callback für die GUI, gibt bei success Category an GUI weiter, bei Fehler die Exception message.
     */
    public void getCategoryByUUID(String uuid, SingleDataCallback<Category> callback) 
    {
        callLogicFuncInThread(
            () -> { return categoryLogic.getCategoryByUUID(uuid); }, 
            "categorybyuuidempty", "Es wurde keine Kategorie zur UUID "+uuid+" gefunden", 
            "categorybyuuiderror", "Beim Abrufen der Kategorie "+uuid+" ist ein Fehler $ aufgetreten", 
            callback, "UUID");
    }



    /**
     * Wird verwendet, um die Kategorien für eine Kategorie zu ändern. Wird an die CategoryLogic weitergegeben.
     * @param card  die Karte, um zu ändern
     * @param categories  Liste der Kategorien
     * @param callback  Callback für die GUI, gibt bei Fehler die Exception message an die GUI weiter.
     */
    public void setCategoriesToCard(Card card, List<Category> categories, SingleDataCallback<Boolean> callback) 
    {
        String uuid = card != null ? card.getUuid() : "";
        callLogicFuncInThread(
            () -> { categoryLogic.setCardToCategories(card, categories); return true; }, 
            "", "", 
            "setcategoriestocarderror", "Beim Setzen der Kategorien für die Karte mit der UUID "+uuid+" ist ein Fehler $ aufgetreten", 
            callback, "");
    }


    /**
     * Nutzung für Display einzelner Karten im CardGUI
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category  Die Kategorie, zu der die Karten abgerufen werden sollen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsInCategory(Category category, DataCallback<CardOverview> callback) 
    {
        String categoryname = category != null ? category.getName() : "";
        callLogicFuncInThread(
            () -> { return categoryLogic.getCardsInCategory(category); }, 
            "", "", 
            "getcardincategoryerror", "Beim Suchen nach Karten mit Kategorie "+categoryname+" ist ein Fehler $ aufgetreten", 
            callback, "");
    }

    /**
     * Nutzung für Filterfunktion für den User. Category wird als String übergeben und durch die CategoryLogik geprüft
     * ob der String richtig ist und ob es überhaupt eine Kategorie für die Karte gibt.
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category  Die Kategorie, zu der die Karten abgerufen werden sollen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsInCategory(String category, DataCallback<CardOverview> callback) 
    {
        callLogicFuncInThread(
            () -> { return categoryLogic.getCardsInCategory(category); }, 
            "getcardincategoryempty", "", 
            "getcardincategoryerror", "Beim Suchen nach Karten mit Kategorie "+category+" ist ein Fehler $ aufgetreten", 
            callback, "category");
    }

    /**
     * Nutzung für Filterfunktion für den User. Category wird als String übergeben und durch die CategoryLogik geprüft
     * ob der String richtig ist und ob es überhaupt eine Kategorie für die Karte gibt.
     * Gibt eine sortierte Liste von Karten wider.
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category  Die Kategorie, zu der die Karten abgerufen werden sollen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     * @param order Der Parameter nach dem die Karten sortiert werden
     * @param reverseorder Gibt die Sortierreihenfolge an
     */
    public void getCardsInCategory(String category, DataCallback<CardOverview> callback, ListOrder.Order order, boolean reverseorder)
    {
        callLogicFuncInThread(
            () -> { return categoryLogic.getCardsInCategory(category,order,reverseorder); },
            "getcardincategoryempty", "",
            "getcardincategoryerror", "Beim Suchen nach Karten mit Kategorie "+category+" ist ein Fehler $ aufgetreten",
            callback, "category");
    }


    /**
     * Gibt Karten für mehrere Kategorien wider. Wird an die CategoryLogic weitergegeben.
     * @param categories Die Kategorien, zu denen die zugehörigen Karten gesucht werden sollen
     * @param callback Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsInCategories(List<Category> categories, DataCallback<CardOverview> callback)
    {
        callLogicFuncInThread(
            () -> { return categoryLogic.getCardsInCategories(categories); }, 
            "getcardsincategoriesempty", "", 
            "getcardsincategorieserror", "Beim Suchen nach Karten mit Kategorien ist ein Fehler $ aufgetreten", 
            callback, "");
    }


    /**
     * Kann verwendet werden, um einzelne Kategorien zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     *
     * @param card Die Karte, zu der die Kategorien abgerufen werden sollen
     * @param callback Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCategoriesToCard(Card card, DataCallback<Category> callback) 
    {
        String cardname = card != null ? card.toString() : "";
        callLogicFuncInThread(
            () -> { return categoryLogic.getCategoriesByCard(card); }, 
            "", "", 
            "getcategoriestocarderror", "Beim Suchen nach Kategorien für die Karte "+cardname+" ist ein Fehler $ aufgetreten", 
            callback, "");
    }

    /**
     * Lädt alle Categories als Set. 
     * Werden in der CardEditPage als Dropdown angezeigt. 
     * Wird weitergegeben an die CategoryLogic.
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCategories(DataCallback<Category> callback) 
    {
        callLogicFuncInThread(
            categoryLogic::getCategories,
            "getcatoriesempty", "",
            "getcatorieserror", "Beim holen von Kategorien ist ein Fehler $ aufgetreten",
            callback, "");
    }

    /**
     * Lädt alle Categories welche keine Parent-Categories haben als Liste.
     * Wird weitergegeben an die CategoryLogic.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     *
     * @param bReverseOrder sortiert die Rückgabe entsprechend (true/desc und false/asc)
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getRootCategories(boolean bReverseOrder, DataCallback<Category> callback)
    {
        callLogicFuncInThread(
            () -> { return categoryLogic.getRootCategories(bReverseOrder); }, 
            "", "",
            "getrootcategorieserror", "Beim holen der Root-Kategorien ist ein Fehler $ aufgetreten",
            callback, "");
    }

    /**
     * Wird verwendet, um eine Liste von Karten in einer Kategorie zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param callback  wird verwendet, um mögliche Fehler abzufangen.
     * @param category          Die Kategorie, bei der die Karten gelöscht werden sollen
     * @param list                die Liste der zu löschenden Karten
     */
    public void removeCardsFromStudySystem(List<CardOverview> list, Category category, SingleDataCallback<Boolean> callback) 
    {
        String categoryname = category != null ? category.getName() : "";
        callLogicFuncInThread(
            () -> { categoryLogic.removeCardsFromCategory(list, category); return true; }, 
            "", "", 
            "removecardsfromstudysystemerror", "Beim entfernen der Karten aus der Kategorie "+categoryname+" ist ein Fehler $ aufgetreten", 
            callback, "");
    }

    /**
     * Wird verwendet, um Kategorien nach einem bestimmten Suchwort zu durchsuchen.
     * Wird an die CategoryLogic weitergegeben.
     * @param searchterm Der zu durchsuchende Suchterm
     * @param callback wird verwendet, um mögliche Fehler abzufangen
     */

    public void getCategoriesBySearchterm(String searchterm, DataCallback<Category> callback) 
    {
        callLogicFuncInThread(
            () -> { return categoryLogic.getCategoriesBySearchterms(searchterm); }, 
            "getcateoriesbysearchtermsempty", "", 
            "getcateoriesbysearchtermserror", "Beim Suchen nach Karten mit dem Suchbegriff "+searchterm+" ist ein Fehler $ aufgetreten", 
            callback, searchterm);
    }
}