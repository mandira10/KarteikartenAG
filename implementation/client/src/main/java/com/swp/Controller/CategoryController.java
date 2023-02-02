package com.swp.Controller;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.Logic.CategoryLogic;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * CategoryController Klasse. Wird in der GUI für alle Funktionen zur Kategorie aufgerufen und
 * gibt Ergebnisse an die GUI weiter.
 */
@Slf4j
public class CategoryController {
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
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();


    /**
     * Wird verwendet, um Data für eine Kategorie zu aktualisieren. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu aktualisieren
     * @param neu  Ob, die Kategorie neue oder nicht ist zu verstehen
     * @param singleDataCallback  Callback für die GUI gib bei Fehler die Exception message weiter
     */
    public void updateCategoryData(Category category, boolean neu, boolean nameChange, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                categoryLogic.updateCategoryData(category, neu, nameChange);
                singleDataCallback.callSuccess(true);
            }
            catch (IllegalStateException ex) {
                singleDataCallback.callFailure(ex.getMessage());
                log.error("Kategorie nicht gefunden");
            }
            catch (Exception ex) {
                singleDataCallback.callFailure(String.format(Locale.getCurrentLocale().getString("categoryupdatesaveerror")));
                log.error("Beim Updaten/Speichern der Kategorie {} ist ein Fehler {} aufgetreten",category.getUuid(),ex.getMessage());

            }
        });
    }

    /**
     * Wird verwendet, um Hierarchy der Kategorie zu bearbeiten. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu bearbeiten
     * @param parents   die Liste der Parents Kategorien für diese Kategorie
     * @param children  die Liste der Children Kategorien für diese Kategorie
     * @param singleDataCallback  Callback für die GUI, gibt bei Fehler die Exception message weiter
     */
    public void editCategoryHierarchy(Category category, List<Category> parents, List<Category> children, SingleDataCallback<String> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                if(categoryLogic.editCategoryHierarchy(category, parents, children) && singleDataCallback != null) //Selfreference?
                        singleDataCallback.callSuccess(Locale.getCurrentLocale().getString("selfreferenceerror"));
                else if(singleDataCallback != null)
                    singleDataCallback.callSuccess("");
            }
            catch(IllegalArgumentException ex){
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(ex.getMessage());
            }
            catch (Exception ex) {
                log.error("Beim Updaten der Kategorie ist der Fehler {} aufgetreten", ex.getMessage());
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("categoryhierarchyupdateerror"));
            }
        });
    }

    /**
     * Wird verwendet, um eine Kategorie zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param category  die Kategorie zu löschen
     * @param singleDataCallback Callback für die GUI, gibt bei Fehler die Exception message weiter
     */
    public void deleteCategory(Category category, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                categoryLogic.deleteCategory(category);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (IllegalStateException ex) { //übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(ex.getMessage());
            }
            catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("deletecategoryerror"));
                log.error("Beim Löschen der Kategorie {} ist ein Fehler {} aufgetreten", category, ex);
            }
        });
    }

    /**
     * Wird verwendet, um mehrere Kategorien zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param categories  die Liste der Kategorien zu löschen
     * @param singleDataCallback  Callback für die GUI, gibt bei Fehler die Exception message.
     *
     */
    public void deleteCategories(List<Category> categories, SingleDataCallback<Boolean> singleDataCallback){
        threadPool.exec(() -> {
            try {
                categoryLogic.deleteCategories(categories);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("deletecategorieserror"));
                log.error("Beim Löschen der Kategorien {} ist ein Fehler {} aufgetreten", categories, ex);
            }
        });
    }


    /**
     * Wird verwendet, um die Children einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     *
     * @param parent  die Parent Kategorie, um die Children zu bekommen
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getChildrenForCategory(Category parent, DataCallback<Category> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<Category> categoryChildren = categoryLogic.getChildrenForCategory(parent);
                if (categoryChildren.isEmpty())
                    log.trace(Locale.getCurrentLocale().getString("catchildrenempty"));

                if(dataCallback != null)
                    dataCallback.callSuccess(categoryChildren);
            } catch (Exception ex) {
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("catchilderror"));
                log.error("Beim Beim Abrufen der Children für die Kategorie {} ist ein Fehler {} aufgetreten", parent, ex);
            }
        });
    }


    /**
     * Wird verwendet, um die Parents einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     *
     * @param child  die Child Kategorie, um die Parents zu bekommen
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getParentsForCategory(Category child, DataCallback<Category> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<Category> categoryParents = categoryLogic.getParentsForCategory(child);
                if (categoryParents.isEmpty())
                    log.trace(Locale.getCurrentLocale().getString("catchildrenempty"));

                if(dataCallback != null)
                    dataCallback.callSuccess(categoryParents);
            } catch (Exception ex) {
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("catchilderror"));
                log.error("Beim Beim Abrufen der Children für die Kategorie {} ist ein Fehler {} aufgetreten", child, ex);
            }
        });
    }

    /**
     * Wird verwendet, um einzelne Kategorieinformationen über ihre UUID abzurufen. Wird an die CategoryLogic weitergegeben.
     *
     * @param uuid  UUID der abzurufenden Kategorie
     * @param singleDataCallback  Callback für die GUI, gibt bei success Category an GUI weiter, bei Fehler die Exception message.
     */
    public void getCategoryByUUID(String uuid, SingleDataCallback<Category> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(categoryLogic.getCategoryByUUID(uuid));
            } catch (IllegalArgumentException ex) {//übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(ex.getMessage());
            }
            catch(NoResultException ex){
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("categorybyuuidempty"));
                log.error("Es wurde keine Kategorie zur UUID {} gefunden",uuid);
            }
            catch(Exception ex){
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("categorybyuuiderror"));
                log.error("Beim Abrufen der Kategorie ist ein Fehler {} aufgetreten",ex.getMessage());
            }
        });
    }



    /**
     * Wird verwendet, um die Kategorien für eine Kategorie zu ändern. Wird an die CategoryLogic weitergegeben.
     * @param card  die Karte, um zu ändern
     * @param categories  Liste der Kategorien
     * @param singleDataCallback  Callback für die GUI, gibt bei Fehler die Exception message an die GUI weiter.
     */
    public void setCategoriesToCard(Card card, List<Category> categories, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                categoryLogic.setCardToCategories(card, categories);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("setcategoriestocarderror"));
                log.error("Beim Setzen der Kategorien für die Karte mit der UUID {} ist ein Fehler {} aufgetreten",card.getUuid(), ex.getMessage());
            }
        });
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
        threadPool.exec(() -> {
            try {
                List<CardOverview> cards = categoryLogic.getCardsInCategory(category);

                if (cards.isEmpty())
                    log.info(Locale.getCurrentLocale().getString("getcardincategoryempty"));

                if(dataCallback != null)
                    dataCallback.callSuccess(cards);

            } catch (IllegalStateException ex) { //übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                if(dataCallback != null)
                    dataCallback.callFailure(ex.getMessage());
            }
            catch (final Exception ex) {
                log.error("Beim Suchen nach Karten mit Kategorie {} ist ein Fehler {} aufgetreten", category, ex);
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getcardincategoryerror"));
            }
        });
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
        threadPool.exec(() -> {
            try {
                List<CardOverview> cards = categoryLogic.getCardsInCategory(category);

                if (cards.isEmpty() && dataCallback != null)
                    dataCallback.callInfo(Locale.getCurrentLocale().getString("getcardincategoryempty"));

                if(dataCallback != null)
                    dataCallback.callSuccess(cards);

            } catch (IllegalStateException ex) { //übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                if(dataCallback != null)
                    dataCallback.callFailure(ex.getMessage());
            }  catch (final Exception ex) {
                log.error("Beim Suchen nach Karten mit der Kategorie {} ist ein Fehler {} aufgetreten", category, ex);
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getcardincategoryerror"));
            }
        });
    }


    /**
     * Gibt Karten für mehrere Kategorien wider. Wird an die CategoryLogic weitergegeben.
     * @param categories Die Kategorien, zu denen die zugehörigen Karten gesucht werden sollen
     * @param dataCallback Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsInCategories(List<Category> categories, DataCallback<CardOverview> dataCallback)
    {
        threadPool.exec(() -> {
            try 
            {
                List<CardOverview> cards = categoryLogic.getCardsInCategories(categories);

                if (cards.isEmpty() && dataCallback != null)
                    dataCallback.callInfo(Locale.getCurrentLocale().getString("getcardsincategoriesempty"));

                if(dataCallback != null)
                    dataCallback.callSuccess(cards);

            } catch (IllegalStateException ex) { //null
                log.error("Der übergebene Wert war leer");
                if(dataCallback != null)
                    dataCallback.callFailure(ex.getMessage());
            }  catch (final Exception ex) {
                // wann wird von categoryLogic.getCardsInCategories() andere Exceptions als `IllegalStateException` geworfen?
                log.error("Beim Suchen nach Karten mit Kategorien ist ein Fehler {} aufgetreten", ex);
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getcardsincategorieserror"));
            }
        });
    }


    /**
     * Kann verwendet werden, um einzelne Kategorien zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     *
     * @param card Die Karte, zu der die Kategorien abgerufen werden sollen
     * @param dataCallback Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCategoriesToCard(Card card, DataCallback<Category> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<Category> categoriesForCard = categoryLogic.getCategoriesByCard(card);

                if (categoriesForCard.isEmpty())
                    log.info(Locale.getCurrentLocale().getString("getcategoriestocardempty"));

                if(dataCallback != null)
                    dataCallback.callSuccess(categoriesForCard);

            } catch (final Exception ex) {
                log.error("Beim Suchen nach Kategorien für die Karte {} ist ein Fehler {} aufgetreten", card, ex);
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getcategoriestocarderror"));
            }
        });
    }

    /**
     * Lädt alle Categories als Set. 
     * Werden in der CardEditPage als Dropdown angezeigt. 
     * Wird weitergegeben an die CategoryLogic.
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCategories(DataCallback<Category> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<Category> categories = categoryLogic.getCategories();

                if (categories.isEmpty())
                    log.info(Locale.getCurrentLocale().getString("getcatoriesempty"));

                if(dataCallback != null)
                    dataCallback.callSuccess(categories);
            } catch (Exception ex) {
                log.error("Beim Suchen nach Kategorien  ist ein Fehler {} aufgetreten", ex);
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getcatorieserror"));
            }
        });
    }

    /**
     * Lädt alle Categories welche keine Parent-Categories haben als Liste.
     * Wird weitergegeben an die CategoryLogic.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     *
     * @param bReverseOrder
     * @param dataCallback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getRootCategories(boolean bReverseOrder, DataCallback<Category> dataCallback)
    {
        threadPool.exec(() -> {
            try 
            { 
                if(dataCallback != null)
                    dataCallback.callSuccess(categoryLogic.getRootCategories(bReverseOrder));
            }
            catch (Exception ex) 
            {
                log.error("Beim Suchen nach Root-Kategorien  ist ein Fehler {} aufgetreten", ex);
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getrootcategorieserror"));
            }
        });
    }

    /**
     * Wird verwendet, um eine Liste von Karten in einer Kategorie zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     * @param category          Die Kategorie, bei der die Karten gelöscht werden sollen
     * @param list                die Liste der zu löschenden Karten
     */
    public void removeCardsFromStudySystem(List<CardOverview> list, Category category, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                categoryLogic.removeCardsFromCategory(list, category);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (IllegalStateException ex) {
                log.error("Null Variable gegeben");
                if(singleDataCallback != null)
                    singleDataCallback.onFailure(ex.getMessage());
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("TODO"));
                log.error(ex.getMessage());
            }
        });
    }

    public void getCategoriesBySearchterm(String searchterm, DataCallback<Category> callback) {
        threadPool.exec(() -> {
            try {
                List<Category> catForSearchTerms = categoryLogic.getCategoriesBySearchterms(searchterm);

                if (catForSearchTerms.isEmpty()) {
                    callback.callInfo("Es gibt keine Kategorien für dieses Suchwort");
                    log.info(Locale.getCurrentLocale().getString("getcateoriesbysearchtermsempty"));
                } else
                    callback.callSuccess(catForSearchTerms);
            } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                callback.callFailure(ex.getMessage());
            } catch (final Exception ex) {
                log.error("Beim Suchen nach Karten mit dem Suchbegriff {} ist ein Fehler {} aufgetreten", searchterm, ex);
                callback.callFailure(Locale.getCurrentLocale().getString("getcateoriesbysearchtermserror"));
            }
        });
    }
}