package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Logic.CategoryLogic;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.UniqueConstraint;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.SQLGrammarException;

import java.util.List;

@Slf4j
public class CategoryController {
    private CategoryController() {
    }

    CategoryLogic categoryLogic = CategoryLogic.getInstance();

    private static CategoryController categoryController;

    public static CategoryController getInstance() {
        if (categoryController == null)
            categoryController = new CategoryController();
        return categoryController;
    }

    /**
     * Wird verwendet, um Data für eine Kategorie zu aktualisieren. Wird an die CategoryLogic weitergegeben.
     *
     * @param category: die Kategorie zu aktualisieren
     * @param neu: Ob, die Kategorie neue oder nicht ist zu verstehen
     */
    public void updateCategoryData(Category category, boolean neu, boolean nameChange, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.updateCategoryData(category, neu, nameChange);
        }
        catch (IllegalStateException ex) {
            singleDataCallback.onFailure(ex.getMessage());
            log.error("Kategorie nicht gefunden");
        }
        catch (Exception ex) {
            singleDataCallback.onFailure(String.format("Kategorie konnte nicht gespeichert oder geupdatet werden"));
            log.error("Beim Updaten/Speichern der Kategorie {} ist ein Fehler {} aufgetreten",category.getUuid(),ex.getMessage());

        }
    }

    /**
     * Wird verwendet, um Hierarchy der Kategorie zu bearbeiten. Wird an die CategoryLogic weitergegeben.
     *
     * @param category: die Kategorie zu bearbeiten
     * @param parents : die Liste der Parents Kategorien für diese Kategorie
     * @param children: die Liste der Children Kategorien für diese Kategorie
     */
    public void editCategoryHierarchy(Category category, List<Category> parents, List<Category> children, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.editCategoryHierarchy(category, parents, children);
        } catch (Exception ex) {
            log.error("Beim Updaten der Kategorie ist der Fehler {} aufgetreten", ex.getMessage());
            singleDataCallback.onFailure("Beim Updaten der Hierarchie ist ein Fehler aufgetreten");
        }
    }

    /**
     * Wird verwendet, um eine Kategorie zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param category: die Kategorie zu löschen
     */
    public void deleteCategory(Category category, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.deleteCategory(category);
        } catch (IllegalStateException ex) { //übergebener Wert ist leer
            log.error("Der übergebene Wert war leer");
            singleDataCallback.onFailure(ex.getMessage());
        }
        catch (Exception ex) {
            singleDataCallback.onFailure("Beim Löschen der Kategorie ist ein Fehler aufgetreten");
            log.error("Beim Löschen der Kategorie {} ist ein Fehler {} aufgetreten", category, ex);
        }
    }

    /**
     * Wird verwendet, um mehrere Kategorien zu löschen. Wird an die CategoryLogic weitergegeben.
     *
     * @param categories: die Liste der Kategorien zu löschen
     */
    public void deleteCategories(List<Category> categories, SingleDataCallback<Boolean> singleDataCallback){
        try {
            categoryLogic.deleteCategories(categories);
        } catch (Exception ex) {
            singleDataCallback.onFailure("Beim Löschen der Kategorien ist ein Fehler aufgetreten");
            log.error("Beim Löschen der Kategorien {} ist ein Fehler {} aufgetreten", categories, ex);
        }
    }


    /**
     * Wird verwendet, um die Children einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     * 
     * @param parent: die Parent Kategorie, um die Children zu bekommen
     */
    public void getChildrenForCategory(Category parent, DataCallback<Category> dataCallback) {
        try {
            List<Category> categoryChildren = categoryLogic.getChildrenForCategory(parent);
            if (categoryChildren.isEmpty())
                log.info("Keine Children für diese Kategorie vorhanden");

            dataCallback.onSuccess(categoryChildren);
        } catch (Exception ex) {
            dataCallback.onFailure("Beim Abrufen der Children für die Kategorie ist ein Fehler aufgetreten");
            log.error("Beim Beim Abrufen der Children für die Kategorie {} ist ein Fehler {} aufgetreten", parent, ex);
        }
    }

    /**
     * Wird verwendet, um einzelne Kategorieinformationen über ihre UUID abzurufen. Wird an die CategoryLogic weitergegeben.
     *
     * @param uuid: UUID der abzurufenden Kategorie
     */
    public void getCategoryByUUID(String uuid, SingleDataCallback<Category> singleDataCallback) {
        try {
            singleDataCallback.onSuccess(categoryLogic.getCategoryByUUID(uuid));
        } catch (IllegalArgumentException ex) {//übergebener Wert ist leer
            log.error("Der übergebene Wert war leer");
            singleDataCallback.onFailure(ex.getMessage());
        }
        catch(NoResultException ex){
            singleDataCallback.onFailure("Es konnte keine Kategorie zur UUID gefunden werden");
            log.error("Es wurde keine Kategorie zur UUID {} gefunden",uuid);
        }
        catch(Exception ex){
            singleDataCallback.onFailure("Beim Abrufen der Kategorie ist ein Fehler aufgetreten");
            log.error("Beim Abrufen der Kategorie ist ein Fehler {} aufgetreten",ex.getMessage());
        }
    }



    /**
     * Wird verwendet, um die Kategorien für eine Kategorie zu ändern.  Wird an die CategoryLogic weitergegeben.
     * @param card: die Karte,um zu ändern
     * @param categories: Liste der Kategorien
     */
    public void setCategoriesToCard(Card card, List<Category> categories, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.setCardToCategories(card, categories);
        } catch (Exception ex) {
            singleDataCallback.onFailure("Beim Hinzufügen der Kategorien zu der Karte ist ein Fehler aufgetreten");
            log.error("Beim Setzen der Kategorien für die Karte mit der UUID {} ist ein Fehler {} aufgetreten",card.getUuid(), ex.getMessage());
        }
    }

//OVERVIEW

    /**
     * Nutzung für Display einzelner Karten im CardGUI
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category: Die Kategorie, zu der die Karten abgerufen werden sollen
     */
    public void getCardsInCategory(Category category, DataCallback<CardOverview> dataCallback) {
        try {
            List<CardOverview> cards = categoryLogic.getCardsInCategory(category);

            if (cards.isEmpty())
                log.info("Es gibt keine Karten zu dieser Kategorie");

            dataCallback.onSuccess(cards);

        } catch (final NoResultException ex) {
            // keine Karten mit entsprechendem Inhalt gefunden
            log.info("Keine Karten mit der Kategorie {} gefunden", category);
            dataCallback.onFailure(ex.getMessage());
        } catch (IllegalStateException ex) { //übergebener Wert ist leer
            log.error("Der übergebene Wert war leer");
            dataCallback.onFailure(ex.getMessage());
        }
        catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit Kategorie {} ist ein Fehler {} aufgetreten", category
                    , ex);
            dataCallback.onFailure("Beim Suchen nach Karten für die Kategorie " + category.getName() +" ist ein Fehler aufgetreten");
        }
    }

    /**
     * Nutzung für Filterfunktion für den User. Category wird als String übergeben und durch die CategoryLogik geprüft
     * ob der String richtig ist und ob es überhaupt eine Kategorie für die Karte gibt.
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category: Die Kategorie, zu der die Karten abgerufen werden sollen
     */
    public void getCardsInCategory(String category, DataCallback<CardOverview> dataCallback) {

        try {
            List<CardOverview> cards = categoryLogic.getCardsInCategory(category);

            if (cards.isEmpty())
               dataCallback.onInfo("Es gibt keine Karten für diese Kategorie");

            dataCallback.onSuccess(cards);

        } catch (IllegalStateException ex) { //übergebener Wert ist leer
            log.error("Der übergebene Wert war leer");
            dataCallback.onFailure(ex.getMessage());
        }  catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit der Kategorie {} ist ein Fehler {} aufgetreten", category
                    , ex);
            dataCallback.onFailure("Beim Suchen nach Karten für die Kategorie " + category +" ist ein Fehler aufgetreten");
        }
    }


    /**
     * Gibt Karten für mehrere Kategorien wider. Wird an die CategoryLogic weitergegeben.
     * @param categories Die Kategorien, zu denen die zugehörigen Karten gesucht werden sollen
     * @param dataCallback: Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsInCategories(List<Category> categories, DataCallback<CardOverview> dataCallback)
    {
        try {
            List<CardOverview> cards = categoryLogic.getCardsInCategories(categories);

            if (cards.isEmpty())
                dataCallback.onInfo("Es gibt keine Karten für diese Kategorien");

            dataCallback.onSuccess(cards);

        } catch (IllegalStateException ex) { //null
            log.error("Der übergebene Wert war leer");
            dataCallback.onFailure(ex.getMessage());
        }  catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit Kategorien ist ein Fehler {} aufgetreten", ex);
            dataCallback.onFailure("Beim Suchen nach den Karten für die angegebenen Kategorien ist ein Fehler aufgetreten");
        }
    }


    /**
     * Kann verwendet werden, um einzelne Kategorien zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     *
     * @param card Die Karte, zu der die Kategorien abgerufen werden sollen
     */
    public void getCategoriesToCard(Card card, DataCallback<Category> dataCallback) {
        try {
            List<Category> categoriesForCard = categoryLogic.getCategoriesByCard(card);

            if (categoriesForCard.isEmpty())
                log.info("Keine Kategorien für diese Karte vorhanden");

            dataCallback.onSuccess(categoriesForCard);

        } catch (final Exception ex) {
            log.error("Beim Suchen nach Kategorien für die Karte {} ist ein Fehler {} aufgetreten", card, ex);
            dataCallback.onFailure("Beim Suchen nach Kategorien für die Karte ist ein Fehler aufgetreten");
        }
    }

    /**
     * Lädt alle Categories als Set. 
     * Werden in der CardEditPage als Dropdown angezeigt. 
     * Wird weitergegeben an die CategoryLogic.
     */
    public void getCategories(DataCallback<Category> dataCallback) {
        try {
            List<Category> categories = categoryLogic.getCategories();

            if (categories.isEmpty())
                log.info("Keine Kategorien vorhanden");

            dataCallback.onSuccess(categories);
        } catch (Exception ex) {
            log.error("Beim Suchen nach Kategorien  ist ein Fehler {} aufgetreten", ex);
            dataCallback.onFailure("Beim Suchen nach Kategorien ist ein Fehler aufgetreten");
        }
    }

    /**
     * Lädt alle Categories welche keine Parent-Categories haben als Liste. 
     * Wird weitergegeben an die CategoryLogic.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     *
     */
    public void getRootCategories(DataCallback<Category> dataCallback)
    {
        try {
            dataCallback.onSuccess(categoryLogic.getRootCategories());
        } catch (Exception ex) {
            log.error("Beim Suchen nach Root-Kategorien  ist ein Fehler {} aufgetreten", ex);
            dataCallback.onFailure("Beim Suchen nach Root-Kategorien ist ein Fehler aufgetreten");
        }
    }
}
