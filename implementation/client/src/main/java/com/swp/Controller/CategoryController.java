package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Set;
@Slf4j
public class CategoryController
{
    private CategoryController() {
    }



    public static boolean updateCategoryData(Category category, boolean neu) {

        return CategoryLogic.updateCategoryData(category, neu);
        //TODO: Exceptions a la Data Callback, user
    }

    public static boolean editCategoryHierarchy(Category category, Set<String> parents, Set<String> children) {
        return CategoryLogic.editCategoryHierarchy(category, parents, children);
        //TODO: Exceptions a la Data Callback ,user
    }

    public static boolean deleteCategory(Category category) {
        return CategoryLogic.deleteCategory(category);
        //TODO: test how multicategory works???
        //TODO: Exception a la Data Callback, non user
    }

    public static boolean deleteCardToCategory(CardToCategory c2d) {
        return CategoryLogic.deleteCardToCategory(c2d);
        //TODO: test how multicategory works???
        //TODO: Exception a la Data Callback, non user
    }


    public static Category getCategoryByUUID(String uuid) {
        return CategoryLogic.getCategoryByUUID(uuid);
        //TODO: Exceptions a la Data Callback, non user
    }

    //TODO: where to use???
    public static int numCardsInCategory(Category category) {
        return CategoryLogic.numCardsInCategory(category);
    }


    public static boolean setCategoriesToCard(Card card, Set<Category> categories) {
        return CategoryLogic.setCardToCategories(card, categories);
        //TODO: Exceptions // data callback, non user
    }

    //OVERVIEW
    /**
     * Nutzung für Display einzelner Karten im CardGUI
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category: Die Kategorie, zu der die Karten abgerufen werden sollen
     * @return Sets an Karten mit spezifischer Kategorie
     */
    public static Set<Card> getCardsInCategory(Category category) {
        try {
            Set<Card> cards = CategoryLogic.getCardsInCategory(category);

            if (cards.isEmpty())
               log.info("Es gibt keine Karten zu dieser Kategorie");
            return cards;

        } catch (final NoResultException ex) {
            // keine Karten mit entsprechendem Inhalt gefunden
            log.info("Keine Karten mit der Kategorie {} gefunden", category);
            return null;
        } catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit der Kategorie {} ist ein Fehler {} aufgetreten", category
                    , ex);
            return null;
        }
    }

    /**
     * Nutzung für Filterfunktion für den User. Category wird als String übergeben und durch die CategoryLogik geprüft
     * ob der String richtig ist und ob es überhaupt eine Kategorie für die Karte gibt.
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category: Die Kategorie, zu der die Karten abgerufen werden sollen
     * @return Sets an Karten mit spezifischer Kategorie
     */
    public static Set<Card> getCardsInCategory(String category) {

        try {
            Set<Card> cards = CategoryLogic.getCardsInCategory(category);

            if (cards.isEmpty())
                NotificationGUI.addNotification("Es gibt keine Karten für diese Kategorie", Notification.NotificationType.INFO, 5);

            return cards;

        } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return null;
        } catch (final NullPointerException ex) {
            log.info(ex.getMessage());
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.INFO, 5);
            return null;
        } catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit der Kategorie {} ist ein Fehler {} aufgetreten", category
                    , ex);
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return null;
        }
    }

    /**
     * Kann verwendet werden, um einzelne Kategorien zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     * @param card Die Karte, zu der die Kategorien abgerufen werden sollen
     * @return Gefundene Kategorien für die spezifische Karte
     */
    public static Set<Category> getCategoriesToCard(Card card) {
        try {
            Set categoriesForCard = CategoryLogic.getCategoriesByCard(card);

            if (categoriesForCard.isEmpty())
                log.info("Keine Kategorien für diese Karte vorhanden");

            return categoriesForCard;

        } catch (final Exception ex) {
            log.error("Beim Suchen nach Kategorien mit Karten {} ist ein Fehler {} aufgetreten", card, ex);
            return null;
        }
    }

    //CARDEDITPAGE, CATEGORY OVERVIEW

    /**
     * Lädt alle Categories als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an die CategoryLogic.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     * @return Set mit bestehenden Categories
     */
    public static Set<Category> getCategories(){
        return CategoryLogic.getCategories();
    }

    //OLD?!
    public static boolean updateCategoryData(Category oldcategory, Category newcategory) {
        return false;
    }
}