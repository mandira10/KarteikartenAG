package com.swp.Controller;

import com.swp.DataModel.Card;
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

    public void updateCategoryData(Category category, boolean neu, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.updateCategoryData(category, neu);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    public void editCategoryHierarchy(Category category, List<Category> parents, List<Category> children, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.editCategoryHierarchy(category, parents, children);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    public void deleteCategory(Category category, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.deleteCategory(category);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    public void deleteCategories(List<Category> categories, SingleDataCallback<Boolean> singleDataCallback){
        try {
            categoryLogic.deleteCategories(categories);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    public void deleteCardToCategory(CardToCategory c2d, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            categoryLogic.deleteCardToCategory(c2d);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    public void getParentsForCategory(Category child, DataCallback<Category> dataCallback) {
        try {
            dataCallback.onSuccess(categoryLogic.getParentsForCategory(child));
        } catch (Exception ex) {
            dataCallback.onFailure(ex.getMessage());
        }
    }

    public void getChildrenForCategory(Category parent, DataCallback<Category> dataCallback) {
        try {
            dataCallback.onSuccess(categoryLogic.getChildrenForCategory(parent));
        } catch (Exception ex) {
            dataCallback.onFailure(ex.getMessage());
        }
    }

    public void getCategoryByUUID(String uuid, SingleDataCallback<Category> singleDataCallback) {
        try {
            singleDataCallback.onSuccess(categoryLogic.getCategoryByUUID(uuid));
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    public void numCardsInCategory(Category category, SingleDataCallback<Integer> singleDataCallback) {
        try {
            singleDataCallback.onSuccess(categoryLogic.numCardsInCategory(category));
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }


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
    public void getCardsInCategory(Category category, DataCallback<Card> dataCallback) {
        try {
            List<Card> cards = categoryLogic.getCardsInCategory(category);

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
    public void getCardsInCategory(String category, DataCallback<Card> dataCallback) {

        try {
            List<Card> cards = categoryLogic.getCardsInCategory(category);

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
     * Lädt alle Categories als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an die CategoryLogic.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     *
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
}
