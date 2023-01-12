package com.swp.Logic;

import java.util.*;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CategoryRepository;
import com.swp.Persistence.DeckRepository;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.html.Option;

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
     * @throws NoResultException
     */
    public static Category getCategory(String uuid) {
        return execTransactional(() -> CategoryRepository.findByUUID(uuid));
    }

    public static Category getCategoryByUUID(String uuid) {
        return null;
    }

    public static Set<Category> getCategoriesByCard(Card card) {
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

    public static Set<Card> getCardsInCategory(String categoryName) {
            checkNotNullOrBlank(categoryName, "Kategorie");
            Optional<Category> catOpt = CategoryRepository.find(categoryName);
            if(catOpt.isEmpty())
                throw new NullPointerException("Es gibt keine Kategorie zu dem eingegebenen Wert: " + categoryName);
            Category category = catOpt.get();
            return getCardsInCategory(category);
    }

    public static Set<Card> getCardsInCategory(Category category) {
        return CategoryRepository.getCardsByCategory(category);
    }

    public static boolean setCardToCategories(Card card, Set<Category> catNew) {
        boolean ret = true;
        Set<Category> catOld = getCategoriesByCard(card); //check Old Tags to remove unused tags

        if(catOld == null){
            if(!checkAndCreateCategories(card,catNew,catOld))
                ret = false;
        }

        else if(catOld.containsAll(catNew) && catOld.size() == catNew.size()) //no changes
            return true;



        else if(catOld.containsAll(catNew) || catOld.size() > catNew.size()) { //es wurden nur tags gelöscht
            if(!checkAndRemoveCategories(card,catNew,catOld))
                ret = false;
        }
        else if(catNew.containsAll(catOld)) {  // nur neue hinzufügen
            if(checkAndCreateCategories(card,catNew,catOld))
                ret = false;

        }
        else { //neue und alte
             if(!checkAndCreateCategories(card,catNew,catOld))
                 ret = false;
            if(!checkAndRemoveCategories(card,catNew,catOld))
                ret = false;
        }
        return ret;
    }

    private static boolean checkAndRemoveCategories(Card card, Set<Category> catNew, Set<Category> catOld) {
        boolean ret = true;
        for (Category c : catOld) {
            if (!catNew.contains(c))
                if (!CategoryRepository.deleteCardToCategory(card, c))
                    ret = false;
        }
        return ret;
    }


    private static boolean checkAndCreateCategories(Card card, Set<Category> catNew, Set<Category> catOld) {
       boolean ret = true;
       Set<Category> catExi = CategoryRepository.getCategories();
        for (Category c : catNew) {
            if (catOld != null && catOld.contains(c))
                log.info("Kategorie {} bereits für Karte {} in CardToCategory enthalten, kein erneutes Hinzufügen notwendig", c.getUuid(), card.getUuid());
            else{
                if (catExi == null || catExi.stream().filter(cat -> cat.getUuid().equals(c.getUuid())).findFirst().isEmpty()){
                    if(!CategoryRepository.saveCategory(c)){
                        ret = false;}
                }
                log.info("Kategorie {} wird zu Karte {} hinzugefügt", c.getUuid(), card.getUuid());
                if(!CategoryRepository.saveCardToCategory(card, c))
                    ret = false;
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
    public static Set<Category> getCategories() {
        Set<Category> catList = CategoryRepository.getCategories();

        //TODO: Convert parentuuid entries to actual category references, two methods?

        return catList;
    }

    /**
     * Lädt alle Category Parents for specific category
     */
    public static Set<Category> getCategoryHierarchy(Category c) {
        Set<Category> catList = CategoryRepository.getParentsForCategory(c);

        //TODO: Convert parentuuid entries to actual category references, two methods?

        return catList;
    }


    public static Set<Card> getCardsByCategory(Set<Category> categories) {
        Set<Card> cardsOfAll = new HashSet<>();
        for (Category c : categories){
            cardsOfAll.addAll(getCardsInCategory(c));
        }
        return cardsOfAll;
    }

    public static boolean editCategoryHierarchy(Category category, Set<String> parents, Set<String> children) {
        Set<Category> p = checkCategoryAndSave(category,parents);
        Set<Category> c = checkCategoryAndSave(category,children);
        category.setChildren(c);
        category.setParents(p);
        return CategoryRepository.updateCategory(category); //TODO: Testing -- works for all combinations???
    }

    private static Set<Category> checkCategoryAndSave(Category category, Set<String> multiHierarchyCategories){
        Set<Category> categoriesToReturn = new HashSet<>();
        for (String s : multiHierarchyCategories){
            Optional<Category> cOpt = CategoryRepository.find(s);
            if(cOpt.isEmpty()){
                Category c = new Category(s);
                CategoryRepository.saveCategory(c);
                categoriesToReturn.add(c);
            }
            else{
                Category c = cOpt.get();
                categoriesToReturn.add(c);
            }
        }
        return categoriesToReturn;
    }
}
