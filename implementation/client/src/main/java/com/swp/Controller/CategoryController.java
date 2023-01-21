package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Logic.CategoryLogic;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

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
    public void updateCategoryData(Category category, boolean neu, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.updateCategoryData(category, neu);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Wird verwendet, um Hierarchy der Kategorie zu bearbeiten. Wird an die CategoryLogic weitergegeben.
     *
     * @param category: die Kategorie zu bearbeiten
     * @param parents : die Liste der Parents Kategorien für diese Katagorie
     * @param children: die Liste der Children Kategorien für diese Katagorie
     */
    public void editCategoryHierarchy(Category category, List<Category> parents, List<Category> children, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.editCategoryHierarchy(category, parents, children);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
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
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
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
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Wird verwendet, um eine Karte von einer Kategorie zu löschen. Wird an die CategoryLogic weitergegeben.
     * 
     * @param c2d: Partition Class dafür
     */
    public void deleteCardToCategory(CardToCategory c2d, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.deleteCardToCategory(c2d);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Wird verwendet, um die Parents einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     * 
     * @param child: die Child Kategorie,um die Parents zu bekommen
     */
    public void getParentsForCategory(Category child, DataCallback<Category> dataCallback) {
        try {
            dataCallback.onSuccess(categoryLogic.getParentsForCategory(child));
        } catch (Exception ex) {
            dataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Wird verwendet, um die Children einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     * 
     * @param parent: die Parent Kategorie,um die Children zu bekommen
     */
    public void getChildrenForCategory(Category parent, DataCallback<Category> dataCallback) {
        try {
            dataCallback.onSuccess(categoryLogic.getChildrenForCategory(parent));
        } catch (Exception ex) {
            dataCallback.onFailure(ex.getMessage());
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
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Wird verwendet, um die Anzahl der Karten einer Kategorie zu bekommen. Wird an die CategoryLogic weitergegeben.
     * @param category: die Kategorie,um die Anzahl der Karten drin zu bekommen
     */
    public void numCardsInCategory(Category category, SingleDataCallback<Integer> singleDataCallback) {
        try {
            singleDataCallback.onSuccess(categoryLogic.numCardsInCategory(category));
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Wird verwendet, um die Kategorien für eine Kategorie zu ändern.  Wird an die CategoryLogic weitergegeben.
     * @param card: die Karte,um zu ändern
     * @param categories: Liste der Kategorien
     */
    public void setCategoriesToCard(Card card, List<Category> categories, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.setC2COrCH(card, categories, false);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
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
        } catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit der Kategorie {} ist ein Fehler {} aufgetreten", category
                    , ex);
            dataCallback.onFailure(ex.getMessage());
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
                NotificationGUI.addNotification("Es gibt keine Karten für diese Kategorie", Notification.NotificationType.INFO, 5);

            dataCallback.onSuccess(cards);

        } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
            dataCallback.onFailure(ex.getMessage());
        } catch (final NoResultException ex) {
            dataCallback.onInfo("Es gibt keine Kategorie zum angegebenen Namen");
        } catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit der Kategorie {} ist ein Fehler {} aufgetreten", category
                    , ex);
            dataCallback.onFailure(ex.getMessage());
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
            log.error("Beim Suchen nach Kategorien mit Karten {} ist ein Fehler {} aufgetreten", card, ex);
            dataCallback.onFailure(ex.getMessage());
        }
    }

//CARDEDITPAGE, CATEGORY OVERVIEW

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
            dataCallback.onFailure(ex.getMessage());
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
            dataCallback.onFailure(ex.getMessage());
        }
    }
}
