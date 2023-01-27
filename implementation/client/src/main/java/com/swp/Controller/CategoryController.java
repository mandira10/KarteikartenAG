package com.swp.Controller;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.Logic.CategoryLogic;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CategoryController {
    private static CategoryController categoryController;
    CategoryLogic categoryLogic = CategoryLogic.getInstance();

    private CategoryController() {
    }

    public static CategoryController getInstance() {
        if (categoryController == null)
            categoryController = new CategoryController();
        return categoryController;
    }

    /**
     * Wird verwendet, um Data für eine Kategorie zu aktualisieren. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu aktualisieren
     * @param neu  Ob, die Kategorie neue oder nicht ist zu verstehen
     * @param singleDataCallback  Callback für die GUI gib bei Fehler die Exception message weiter
     */
    public void updateCategoryData(Category category, boolean neu, boolean nameChange, SingleDataCallback<Boolean> singleDataCallback) {
       //  new Thread(() -> {
            try {
                categoryLogic.updateCategoryData(category, neu, nameChange);
                singleDataCallback.onSuccess(true);
            }
            catch (IllegalStateException ex) {
                singleDataCallback.onFailure(ex.getMessage());
                log.error("Kategorie nicht gefunden");
            }
            catch (Exception ex) {
                singleDataCallback.onFailure(String.format(Locale.getCurrentLocale().getString("categoryupdatesaveerror")));
                log.error("Beim Updaten/Speichern der Kategorie {} ist ein Fehler {} aufgetreten",category.getUuid(),ex.getMessage());

            }
         //}).start();
    }

    /**
     * Wird verwendet, um Hierarchy der Kategorie zu bearbeiten. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu bearbeiten
     * @param parents   die Liste der Parents Kategorien für diese Kategorie
     * @param children  die Liste der Children Kategorien für diese Kategorie
     * @param singleDataCallback  Callback für die GUI, gibt bei Fehler die Exception message weiter
     */
    public void editCategoryHierarchy(Category category, List<Category> parents, List<Category> children, SingleDataCallback<Boolean> singleDataCallback) {
       //  new Thread(() -> {
            try {
                categoryLogic.editCategoryHierarchy(category, parents, children);
                singleDataCallback.onSuccess(true);
            } catch (Exception ex) {
                log.error("Beim Updaten der Kategorie ist der Fehler {} aufgetreten", ex.getMessage());
                singleDataCallback.onFailure(Locale.getCurrentLocale().getString("categoryhierarchyupdateerror"));
            }
         //}).start();
    }

    /**
     * Wird verwendet, um eine Kategorie zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu löschen
     * @param singleDataCallback Callback für die GUI, gibt bei Fehler die Exception message weiter
     */
    public void deleteCategory(Category category, SingleDataCallback<Boolean> singleDataCallback) {
       //  new Thread(() -> {
            try {
                categoryLogic.deleteCategory(category);
                singleDataCallback.onSuccess(true);
            } catch (IllegalStateException ex) { //übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                singleDataCallback.onFailure(ex.getMessage());
            }
            catch (Exception ex) {
                singleDataCallback.onFailure(Locale.getCurrentLocale().getString("deletecategoryerror"));
                log.error("Beim Löschen der Kategorie {} ist ein Fehler {} aufgetreten", category, ex);
            }
         //}).start();
    }

    /**
     * Wird verwendet, um mehrere Kategorien zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param categories  die Liste der Kategorien zu löschen
     * @param singleDataCallback  Callback für die GUI, gibt bei Fehler die Exception message.
     *
     */
    public void deleteCategories(List<Category> categories, SingleDataCallback<Boolean> singleDataCallback){
       //  new Thread(() -> {
            try {
                categoryLogic.deleteCategories(categories);
                singleDataCallback.onSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.onFailure(Locale.getCurrentLocale().getString("deletecategorieserror"));
                log.error("Beim Löschen der Kategorien {} ist ein Fehler {} aufgetreten", categories, ex);
            }
         //}).start();
    }


    /**
     * Wird verwendet, um die Children einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     *
     * @param parent  die Parent Kategorie, um die Children zu bekommen
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getChildrenForCategory(Category parent, DataCallback<Category> dataCallback) {
       //  new Thread(() -> {
            try {
                List<Category> categoryChildren = categoryLogic.getChildrenForCategory(parent);
                if (categoryChildren.isEmpty())
                    log.trace(Locale.getCurrentLocale().getString("catchildrenempty"));

                dataCallback.onSuccess(categoryChildren);
            } catch (Exception ex) {
                dataCallback.onFailure(Locale.getCurrentLocale().getString("catchilderror"));
                log.error("Beim Beim Abrufen der Children für die Kategorie {} ist ein Fehler {} aufgetreten", parent, ex);
            }
         //}).start();
    }


    /**
     * Wird verwendet, um die Parents einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     *
     * @param child  die Child Kategorie, um die Parents zu bekommen
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getParentsForCategory(Category child, DataCallback<Category> dataCallback) {
       //  new Thread(() -> {
            try {
                List<Category> categoryParents = categoryLogic.getParentsForCategory(child);
                if (categoryParents.isEmpty())
                    log.trace(Locale.getCurrentLocale().getString("catchildrenempty"));

                dataCallback.onSuccess(categoryParents);
            } catch (Exception ex) {
                dataCallback.onFailure(Locale.getCurrentLocale().getString("catchilderror"));
                log.error("Beim Beim Abrufen der Children für die Kategorie {} ist ein Fehler {} aufgetreten", child, ex);
            }
         //}).start();
    }

    /**
     * Wird verwendet, um einzelne Kategorieinformationen über ihre UUID abzurufen. Wird an die CategoryLogic weitergegeben.
     *
     * @param uuid  UUID der abzurufenden Kategorie
     * @param singleDataCallback  Callback für die GUI, gibt bei success Category an GUI weiter, bei Fehler die Exception message.
     */
    public void getCategoryByUUID(String uuid, SingleDataCallback<Category> singleDataCallback) {
       //  new Thread(() -> {
            try {
                singleDataCallback.onSuccess(categoryLogic.getCategoryByUUID(uuid));
            } catch (IllegalArgumentException ex) {//übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                singleDataCallback.onFailure(ex.getMessage());
            }
            catch(NoResultException ex){
                singleDataCallback.onFailure(Locale.getCurrentLocale().getString("categorybyuuidempty"));
                log.error("Es wurde keine Kategorie zur UUID {} gefunden",uuid);
            }
            catch(Exception ex){
                singleDataCallback.onFailure(Locale.getCurrentLocale().getString("categorybyuuiderror"));
                log.error("Beim Abrufen der Kategorie ist ein Fehler {} aufgetreten",ex.getMessage());
            }
         //}).start();
    }



    /**
     * Wird verwendet, um die Kategorien für eine Kategorie zu ändern. Wird an die CategoryLogic weitergegeben.
     * @param card  die Karte, um zu ändern
     * @param categories  Liste der Kategorien
     * @param singleDataCallback  Callback für die GUI, gibt bei Fehler die Exception message an die GUI weiter.
     */
    public void setCategoriesToCard(Card card, List<Category> categories, SingleDataCallback<Boolean> singleDataCallback) {
       //  new Thread(() -> {
            try {
                categoryLogic.setCardToCategories(card, categories);
                singleDataCallback.onSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.onFailure(Locale.getCurrentLocale().getString("setcategoriestocarderror"));
                log.error("Beim Setzen der Kategorien für die Karte mit der UUID {} ist ein Fehler {} aufgetreten",card.getUuid(), ex.getMessage());
            }
         //}).start();
    }

//OVERVIEW

    /**
     * Nutzung für Display einzelner Karten im CardGUI
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category  Die Kategorie, zu der die Karten abgerufen werden sollen
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsInCategory(Category category, DataCallback<CardOverview> dataCallback) {
       //  new Thread(() -> {
            try {
                List<CardOverview> cards = categoryLogic.getCardsInCategory(category);

                if (cards.isEmpty())
                    log.info(Locale.getCurrentLocale().getString("getcardincategoryempty"));

                dataCallback.onSuccess(cards);

            } catch (IllegalStateException ex) { //übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                dataCallback.onFailure(ex.getMessage());
            }
            catch (final Exception ex) {
                log.error("Beim Suchen nach Karten mit Kategorie {} ist ein Fehler {} aufgetreten", category
                        , ex);
                dataCallback.onFailure(Locale.getCurrentLocale().getString("getcardincategoryerror"));
            }
         //}).start();
    }

    /**
     * Nutzung für Filterfunktion für den User. Category wird als String übergeben und durch die CategoryLogik geprüft
     * ob der String richtig ist und ob es überhaupt eine Kategorie für die Karte gibt.
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category  Die Kategorie, zu der die Karten abgerufen werden sollen
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsInCategory(String category, DataCallback<CardOverview> dataCallback) {
       //  new Thread(() -> {
            try {
                List<CardOverview> cards = categoryLogic.getCardsInCategory(category);

                if (cards.isEmpty())
                    dataCallback.onInfo(Locale.getCurrentLocale().getString("getcardincategoryempty"));

                dataCallback.onSuccess(cards);

            } catch (IllegalStateException ex) { //übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                dataCallback.onFailure(ex.getMessage());
            }  catch (final Exception ex) {
                log.error("Beim Suchen nach Karten mit der Kategorie {} ist ein Fehler {} aufgetreten", category
                        , ex);
                dataCallback.onFailure(Locale.getCurrentLocale().getString("getcardincategoryerror"));
            }
         //}).start();
    }


    /**
     * Gibt Karten für mehrere Kategorien wider. Wird an die CategoryLogic weitergegeben.
     * @param categories Die Kategorien, zu denen die zugehörigen Karten gesucht werden sollen
     * @param dataCallback Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsInCategories(List<Category> categories, DataCallback<CardOverview> dataCallback)
    {
       // new Thread(() -> {
        try {
            List<CardOverview> cards = categoryLogic.getCardsInCategories(categories);

            if (cards.isEmpty())
                dataCallback.onInfo(Locale.getCurrentLocale().getString("getcardsincategoryempty"));

            dataCallback.onSuccess(cards);

        } catch (IllegalStateException ex) { //null
            log.error("Der übergebene Wert war leer");
            dataCallback.onFailure(ex.getMessage());
        }  catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit Kategorien ist ein Fehler {} aufgetreten", ex);
            dataCallback.onFailure(Locale.getCurrentLocale().getString("getcardsincategoryerror"));
        }
     //}).start();
    }


    /**
     * Kann verwendet werden, um einzelne Kategorien zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     *
     * @param card Die Karte, zu der die Kategorien abgerufen werden sollen
     * @param dataCallback Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCategoriesToCard(Card card, DataCallback<Category> dataCallback) {
       //  new Thread(() -> {
            try {
                List<Category> categoriesForCard = categoryLogic.getCategoriesByCard(card);

                if (categoriesForCard.isEmpty())
                    log.info(Locale.getCurrentLocale().getString("getcategoriestocardempty"));

                dataCallback.onSuccess(categoriesForCard);

            } catch (final Exception ex) {
                log.error("Beim Suchen nach Kategorien für die Karte {} ist ein Fehler {} aufgetreten", card, ex);
                dataCallback.onFailure(Locale.getCurrentLocale().getString("getcategoriestocarderror"));
            }
         //}).start();
    }

    /**
     * Lädt alle Categories als Set. 
     * Werden in der CardEditPage als Dropdown angezeigt. 
     * Wird weitergegeben an die CategoryLogic.
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCategories(DataCallback<Category> dataCallback) {
       //  new Thread(() -> {
            try {
                List<Category> categories = categoryLogic.getCategories();

                if (categories.isEmpty())
                    log.info(Locale.getCurrentLocale().getString("getcatoriesempty"));

                dataCallback.onSuccess(categories);
            } catch (Exception ex) {
                log.error("Beim Suchen nach Kategorien  ist ein Fehler {} aufgetreten", ex);
                dataCallback.onFailure(Locale.getCurrentLocale().getString("getcatorieserror"));
            }
         //}).start();
    }

    /**
     * Lädt alle Categories welche keine Parent-Categories haben als Liste. 
     * Wird weitergegeben an die CategoryLogic.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     *
     */
    public void getRootCategories(DataCallback<Category> dataCallback)
    {
        // new Thread(() -> {
        try {
            dataCallback.onSuccess(categoryLogic.getRootCategories());
        } catch (Exception ex) {
            log.error("Beim Suchen nach Root-Kategorien  ist ein Fehler {} aufgetreten", ex);
            dataCallback.onFailure(Locale.getCurrentLocale().getString("getrootcategorieserror"));
        }
     //}).start();
    }

}
