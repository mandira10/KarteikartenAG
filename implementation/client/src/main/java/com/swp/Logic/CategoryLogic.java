package com.swp.Logic;

import java.util.*;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToCategoryRepository;
import com.swp.Persistence.CategoryRepository;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;


import static com.swp.Validator.checkNotNullOrBlank;

@Slf4j
public class CategoryLogic extends BaseLogic<Category>
{
    /**
     * Konstruktor für eine DeckLogic-Instanz.
     */
    private CategoryLogic() {
        super(CategoryRepository.getInstance());
    }

    private final CardRepository cardRepository = CardRepository.getInstance();
    private final CategoryRepository categoryRepository = CategoryRepository.getInstance();
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();

    // Singleton
    private static CategoryLogic categoryLogic;
    public static CategoryLogic getInstance() {
        if (categoryLogic == null)
            categoryLogic = new CategoryLogic();
        return categoryLogic;
    }

    public Category getCategoryByUUID(String uuid) {
        return execTransactional(() -> CategoryRepository.findByUUID(uuid));
    }

    public List<Category> getCategoriesByCard(Card card) {
        return CategoryRepository.getCategoriesToCard(card);
    }




    public int numCardsInCategory(Category category) {
        return getCardsInCategory(category).size();
    }

    public void updateCategoryData(Category category, boolean neu) {
        if (neu) {
            execTransactional(() -> {
                categoryRepository.save(category);
                return true;
            });
        } else {
            execTransactional(() -> {
                categoryRepository.update(category);
                return true;
            });
        }
    }

    public void deleteCategory(Category category) {
         execTransactional(() -> {
            CategoryRepository.getInstance().delete(category);
            CardToCategoryRepository.getInstance()
                    .delete(CardToCategoryRepository.getAllC2CForCategory(category));
            // TODO: parents/children von anderen Kategorien müssen ggf. noch geändert werden
            return true;
        });
    }

    public void deleteCategories(Category[] categories) {
        for (Category c : categories) {
            deleteCategory(c);
        }
    }

    public void deleteCardToCategory(CardToCategory c2d) {
       //TODO
    }

    public List<Card> getCardsInCategory(String categoryName) {
        checkNotNullOrBlank(categoryName, "Kategorie",true);
        Optional<Category> catOpt;
        try {
            catOpt = Optional.of(
                    execTransactional(() -> CategoryRepository.find(categoryName)));
        } catch (final NoResultException ex) {
            catOpt = Optional.empty();
        }
        // oder die `NoResultException` nicht fangen und weiter hoch geben,
        // anstatt eine neue `NullPointerException` zu werfen
        if(catOpt.isEmpty())
            throw new NullPointerException("Es gibt keine Kategorie zu dem eingegebenen Wert: " + categoryName);
        Category category = catOpt.get();
        return execTransactional(() -> getCardsInCategory(category));

        //return execTransactional(() -> getCardsInCategory(CategoryRepository.find(categoryName)));
    }

    public List<Card> getCardsInCategory(Category category) {
        return cardRepository.getCardsByCategory(category);
    }

    public <T> void setC2COrCH(T cardOrCategory, List<Category> catNew, boolean child) {
        List<Category> catOld = new ArrayList<>();
        if(cardOrCategory instanceof Card card)
             catOld = getCategoriesByCard(card); //check Old Tags to remove unused tags
        else if(cardOrCategory instanceof Category category && !child)
            catOld = getChildrenForCategory(category);
        else if(cardOrCategory instanceof Category category && child)
            catOld = getParentsForCategory(category);

        if(catOld == null || catOld.isEmpty()){ //TODO:  vereinheitlichung rückgabewert
            checkAndCreateCategories(cardOrCategory,catNew,catOld,child);}

        else if(catOld.containsAll(catNew) && catOld.size() == catNew.size()){
            return;}//no changes}

        else if(catOld.containsAll(catNew) && catOld.size() > catNew.size()) { //es wurden nur tags gelöscht
            checkAndRemoveCategories(cardOrCategory,catNew,catOld,child);}


        else if(catNew.containsAll(catOld)) {  // nur neue hinzufügen
            checkAndCreateCategories(cardOrCategory,catNew,catOld,child);}

        else { //neue und alte
             checkAndCreateCategories(cardOrCategory,catNew,catOld,child);
             checkAndRemoveCategories(cardOrCategory,catNew,catOld,child);
        }
    }


    private <T> void checkAndRemoveCategories(final T cardOrCategory, List<Category> catNew, List<Category> catOld, boolean child) {
        execTransactional(() -> {
        for (Category c : catOld) {
            if (!catNew.contains(c))
                if (cardOrCategory instanceof Card card) {
                    CardToCategoryRepository.getInstance().delete(CardToCategoryRepository.getSpecific(card, c));
                } else if (cardOrCategory instanceof Category category && !child) {
                    CategoryRepository.deleteCategoryHierarchy(c, category);
                } else if (cardOrCategory instanceof Category category && child) {
                    CategoryRepository.deleteCategoryHierarchy(category, c);
                }
        }
            return null;
        });
    }


    private <T> void checkAndCreateCategories(final T cardOrCategory, List<Category> catNew, List<Category> catOld, boolean child) {
        execTransactional(() -> {
        for (Category c : catNew) {
            if (catOld != null && catOld.contains(c))
                log.info("Kategorie {} bereits in CardToCategory/ CategorieHierarchy enthalten, kein erneutes Hinzufügen notwendig", c.getUuid());
            else {
                Optional<Category> catOptional = Optional.of(CategoryRepository.find(c.getName()));
                if (catOptional.isPresent()) {
                    c = catOptional.get();
                    log.info("Kategorie mit Namen {} bereits in Datenbank enthalten", c.getName());
                } else {
                     CategoryRepository.saveCategory(c);
                }
                if (cardOrCategory instanceof Card card) {
                    log.info("Kategorie {} wird zu Karte {} hinzugefügt", c.getUuid(), card.getUuid());
                        cardToCategoryRepository.save(new CardToCategory(card, c));
                    } else if (cardOrCategory instanceof Category category && child == false) {
                        log.info("Kategorie{} wird als Children zur KategorieHierarchie für Parent{} hinzugefügt", c.getName(), category.getName());
                        categoryRepository.saveCategoryHierarchy(c, category);
                    } else if (cardOrCategory instanceof Category category && child == true) {
                        log.info("Kategorie{} wird als Children zur KategorieHierarchie für Parent{} hinzugefügt", c.getName(), category.getName());
                        categoryRepository.saveCategoryHierarchy(category, c);
                    }
                }

            }
            return null; });

    }

    //CARDEDITPAGE, CATEGORY OVERVIEW

    /**
     * Lädt alle Categories als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an das CategoryRepository.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     * @return Set mit bestehenden Categories
     */
    public List<Category> getCategories() {
        return execTransactional(() -> categoryRepository.getAll());
    }

    /**
     * Lädt alle Category Parents for specific category
     */
    public List<Category> getCategoryHierarchy(Category c) {
        return execTransactional(() -> categoryRepository.getParentsForCategory(c));
    }


    public void editCategoryHierarchy(Category category, List<Category> parents, List<Category> children) {

       setC2COrCH(category,parents,true);
        setC2COrCH(category,children,false);
    }


    public List<Category> getChildrenForCategory(Category parents) {
        return execTransactional(() -> categoryRepository.getChildrenForCategory(parents));
    }

    public List<Category> getParentsForCategory(Category child) {
        return execTransactional(() -> categoryRepository.getParentsForCategory(child));
    }

}