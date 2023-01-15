package com.swp.Logic;

import java.util.*;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.Persistence.CategoryRepository;
import lombok.extern.slf4j.Slf4j;


import static com.swp.Validator.checkNotNullOrBlank;

@Slf4j
public class CategoryLogic extends BaseLogic<Category>
{
    /**
     * Konstruktor für eine DeckLogic-Instanz.
     */
    public CategoryLogic() {
        super(CategoryRepository.getInstance());
    }

    /**
     *
     * @param uuid
     * @return
     */
    public static Category getCategory(String uuid) {
        return execTransactional(() -> CategoryRepository.findByUUID(uuid));
    }

    public static Category getCategoryByUUID(String uuid) {
        return execTransactional(() -> CategoryRepository.findByUUID(uuid));
    }

    public static List<Category> getCategoriesByCard(Card card) {
        return CategoryRepository.getCategoriesToCard(card);
    }




    public static int numCardsInCategory(Category category) {
        return getCardsInCategory(category).size();
    }

    public static boolean updateCategoryData(Category category, boolean neu) {
        if (neu)
            return CategoryRepository.saveCategory(category);
        else
            return CategoryRepository.updateCategory(category);
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

    public static List<Card> getCardsInCategory(String categoryName) {
            checkNotNullOrBlank(categoryName, "Kategorie",true);
            Optional<Category> catOpt = CategoryRepository.find(categoryName);
            if(catOpt.isEmpty())
                throw new NullPointerException("Es gibt keine Kategorie zu dem eingegebenen Wert: " + categoryName);
            Category category = catOpt.get();
            return getCardsInCategory(category);
    }

    public static List<Card> getCardsInCategory(Category category) {
        return CategoryRepository.getCardsByCategory(category);
    }

    public static <T> boolean setC2COrCH(T cardOrCategory, List<Category> catNew, boolean child) {
        boolean ret = true;
        List<Category> catOld = new ArrayList<>();
        if(cardOrCategory instanceof Card card)
             catOld = getCategoriesByCard(card); //check Old Tags to remove unused tags
        else if(cardOrCategory instanceof Category category && child == false)
            catOld = getChildrenForCategory(category);
        else if(cardOrCategory instanceof Category category && child == true)
            catOld = getParentsForCategory(category);

        if(catOld == null || catOld.isEmpty()){ //TODO:  vereinheitlichung rückgabewert
            if(!checkAndCreateCategories(cardOrCategory,catNew,catOld,child))
                ret = false;
        }

        else if(catOld.containsAll(catNew) && catOld.size() == catNew.size()) //no changes
            return true;



        else if(catOld.containsAll(catNew) && catOld.size() > catNew.size()) { //es wurden nur tags gelöscht
            if(!checkAndRemoveCategories(cardOrCategory,catNew,catOld,child))
                ret = false;
        }
        else if(catNew.containsAll(catOld)) {  // nur neue hinzufügen
            if(!checkAndCreateCategories(cardOrCategory,catNew,catOld,child))
                ret = false;

        }
        else { //neue und alte
             if(!checkAndCreateCategories(cardOrCategory,catNew,catOld,child))
                 ret = false;
            if(!checkAndRemoveCategories(cardOrCategory,catNew,catOld,child))
                ret = false;
        }
        return ret;
    }


    private static <T> boolean checkAndRemoveCategories(final T cardOrCategory, List<Category> catNew, List<Category> catOld, boolean child) {
        boolean ret = true;
        for (Category c : catOld) {
            if (!catNew.contains(c))
                if(cardOrCategory instanceof Card card) {
                    if (!CategoryRepository.deleteCardToCategory(card, c))
                        ret = false;
                }
                else if(cardOrCategory instanceof Category category && child == false){
                    if(!CategoryRepository.deleteCategoryHierarchy(c,category));
                    ret = false;
                }
                else if(cardOrCategory instanceof Category category && child == true){
                    if(!CategoryRepository.deleteCategoryHierarchy(category,c));
                    ret = false;
                }
        }
        return ret;
    }


    private static <T> boolean checkAndCreateCategories(final T cardOrCategory, List<Category> catNew, List<Category> catOld, boolean child) {
        boolean ret = true;
        for (Category c : catNew) {
            if (catOld != null && catOld.contains(c))
                log.info("Kategorie {} bereits in CardToCategory/ CategorieHierarchy enthalten, kein erneutes Hinzufügen notwendig", c.getUuid());
            else {
                Optional<Category> catOptional = CategoryRepository.find(c.getName());
                if (catOptional.isPresent()) {
                    c = catOptional.get();
                    log.info("Kategorie mit Namen {} bereits in Datenbank enthalten", c.getName());
                } else {
                    if (!CategoryRepository.saveCategory(c))
                        ret = false;
                }
                if (cardOrCategory instanceof Card card) {
                    log.info("Kategorie {} wird zu Karte {} hinzugefügt", c.getUuid(), card.getUuid());
                    if (!CategoryRepository.saveCardToCategory(card, c))
                        ret = false;
                }
                    else if (cardOrCategory instanceof Category category && child == false) {
                        log.info("Kategorie{} wird als Children zur KategorieHierarchie für Parent{} hinzugefügt", c.getName(), category.getName());
                        if (!CategoryRepository.saveCategoryHierarchy(c, category))
                        ret = false;
                    }
                    else if (cardOrCategory instanceof Category category && child == true) {
                        log.info("Kategorie{} wird als Children zur KategorieHierarchie für Parent{} hinzugefügt", c.getName(), category.getName());
                        if (!CategoryRepository.saveCategoryHierarchy(category, c))
                        ret = false;
                    }
                }

            }

        return ret;
    }

    //CARDEDITPAGE, CATEGORY OVERVIEW

    /**
     * Lädt alle Categories als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an das CategoryRepository.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     * @return Set mit bestehenden Categories
     */
    public static List<Category> getCategories() {
        List<Category> catList = CategoryRepository.getCategories();

        //TODO: Convert parentuuid entries to actual category references, two methods?

        return catList;
    }

    /**
     * Lädt alle Category Parents for specific category
     */
    public static List<Category> getCategoryHierarchy(Category c) {
        List<Category> catList = CategoryRepository.getParentsForCategory(c);

        //TODO: Convert parentuuid entries to actual category references, two methods?

        return catList;
    }


    public static List<Card> getCardsByCategory(Set<Category> categories) {
        List<Card> cardsOfAll = new ArrayList<>();
        for (Category c : categories){
            cardsOfAll.addAll(getCardsInCategory(c));
        }
        return cardsOfAll;
    }

    public static boolean editCategoryHierarchy(Category category, List<Category> parents, List<Category> children) {
        boolean ret = true;
         if(!setC2COrCH(category,parents,true))
             ret = false;
         if(!setC2COrCH(category,children,false))
             ret = false;

         return ret;
    }


    private static List<Category> getChildrenForCategory(Category parents) {
       return CategoryRepository.getChildrenForCategory(parents);
    }

    private static List<Category> getParentsForCategory(Category child) {
       return CategoryRepository.getParentsForCategory(child);
    }

}