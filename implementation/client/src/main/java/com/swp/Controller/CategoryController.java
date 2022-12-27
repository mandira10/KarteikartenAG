package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Set;
@Slf4j
public class CategoryController
{
    private CategoryController() {}

    public static boolean updateCategoryData(Category oldcategory, Category newcategory) { return CategoryLogic.updateCategoryData(oldcategory, newcategory); }
    public static boolean deleteCategory(Category category)                              { return CategoryLogic.deleteCategory(category); }
    public static boolean deleteCardToCategory(CardToCategory c2d)                       { return CategoryLogic.deleteCardToCategory(c2d); }


    public static Category getCategoryByUUID(String uuid)                                { return CategoryLogic.getCategoryByUUID(uuid); }

    public static int numCardsInCategory(Category category)                              { return CategoryLogic.numCardsInCategory(category); }
    public static boolean addCategoriesToCard(Card card, ArrayList<String> categories){
        return CategoryLogic.createCardToCategory(card, categories);
    }

    //OVERVIEW

    /**
     * Nutzung für Display einzelner Karten in SingleCardViewPage(?), Filterfunktion in OverviewPage.
     * Wird an die CategoryLogic weitergegeben.
     *
     * @param category: Die Kategorie, zu der die Karten abgerufen werden sollen
     * @return Sets an Karten mit spezifischer Kategorie
     */

    public static Set<Card> getCardsInCategory(Category category) {

        try {
            Set cardsForCategory = CategoryLogic.getCardsForCategory(category);

            if (cardsForCategory.isEmpty()) {
                NotificationGUI.addNotification("Es gibt keine Karten für diese Kategorie", Notification.NotificationType.INFO, 5);
            }

            return cardsForCategory; //TODO Verknüpfung mit GUI und Darstellung im Overview

        } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return null;
        }

        //TODO zusätzlich findCategoryByString? wie soll Filterfunktion aussehen?
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
}