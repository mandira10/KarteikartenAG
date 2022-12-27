package com.swp.Logic;

import java.util.*;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import static com.swp.Validator.checkNotNullOrBlank;

@Slf4j
public class CategoryLogic {
    public static boolean createCardToCategory(Card card, Category category) {
        if (getCategoriesByCard(card).contains(category))
            return true;

        return CategoryRepository.saveCardToCategory(card, category);
    }


    public static Category getCategory(String uuid) {
        Set<Category> cats = CategoryRepository.getCategories();
        for (Category c : cats) {
            if (c.getSUUID().equals(uuid))
                return c;
        }
        return null;
    }

    public static Category getCategoryByUUID(String uuid) {
        return null;
    }

    public static Set<Category> getCategoriesByCard(Card card) {
        Set<Category> retArr = new HashSet<>();
        for (CardToCategory c2c : CategoryRepository.getCardToCategories()) {
            if (c2c.getPCard() == card)
                retArr.add(c2c.getPCategory());
        }
        return retArr;
    }



    public static int numCardsInCategory(Category category) {
        return getCardsByCategory(category).size();
    }

    public static boolean updateCategoryData(Category oldcategory, Category newcategory) {
        if (newcategory.getSUUID().isEmpty())
            return CategoryRepository.saveCategory(newcategory);
        else
            return CategoryRepository.updateCategory(oldcategory, newcategory);
    }

    public static boolean deleteCategory(Category category) {
        return CategoryRepository.deleteCategory(category);
    }

    public static boolean deleteCategories(Category[] categories) {
        boolean ret = true;
        for (Category c : categories) {
            if (!CategoryRepository.deleteCategory(c))
                ret = false;
        }

        return ret;
    }

    public static boolean deleteCardToCategory(CardToCategory c2d) {
        return false;
    }

    public static Set<Card> getCardsByCategory(Category category) {

        Set<Card> retArr = new HashSet<>();
        for (CardToCategory c2c : CategoryRepository.getCardToCategories()) {
            if (c2c.getPCategory() == category)
                retArr.add(c2c.getPCard());
        }
        return retArr;
    }

    public static boolean createCardToCategory(Card card, ArrayList<String> categories) {
        boolean ret = true;
        for (String name : categories) {
            checkNotNullOrBlank(name, "Category Name");
            final Optional<Category> optionalCategory = CategoryRepository.find(name);
            if (optionalCategory.isPresent()) {
                final Category category = optionalCategory.get();
                if(!createCardToCategory(card,category));{ret = false;}
            }
            else{
                Category category = new Category(name);
                CategoryRepository.saveCategory(category);
                if(!createCardToCategory(card,category));{ret = false;}
            }
        }
        return ret;
    }



    //CARDOVERVIEW

    /**
     * Wird verwendet bei einer Filterung nach einer bestimmten Kategorie. Prüft zunächst, dass der übergebene Tag nicht
     * null oder leer ist und gibt die Funktion dann an das Category Repository weiter.
     * @param category: Die Kategorie, zu der die Karten gesucht werden sollen
     * @return Set der Karten, die die Kategorie enthalten
     */
    public static Set getCardsForCategory(Category category) {
        {
            checkNotNullOrBlank(category, "Kategorie");
            return CategoryRepository.getCardsForCategory(category);
        }
    }

    //CARDEDITPAGE, CATEGORY OVERVIEW

    /**
     * Lädt alle Categories als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an das CategoryRepository.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     * @return Set mit bestehenden Categories
     */
    public static Set<Category> getCategories() {
        Set<Category> catList = CategoryRepository.getCategories();

        //TODO: Convert parentuuid entries to actual category references, two methods?

        return catList;
    }


}
