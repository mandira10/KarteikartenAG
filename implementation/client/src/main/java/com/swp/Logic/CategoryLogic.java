package com.swp.Logic;

import java.util.*;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
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
     * Konstruktor für eine CategoryLogic-Instanz.
     */
    private CategoryLogic() {
        super(CategoryRepository.getInstance());
    }

    private final CardRepository cardRepository = CardRepository.getInstance();
    private final CategoryRepository categoryRepository = CategoryRepository.getInstance();
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();


    private static CategoryLogic categoryLogic;

    /**
     * Hilfsmethode. Wird genutzt, damit nicht mehrere Instanzen von Kategorie entstehen.
     * @return categoryLogic Instanz, die benutzt werden kann.
     */
    public static CategoryLogic getInstance() {
        if (categoryLogic == null)
            categoryLogic = new CategoryLogic();
        return categoryLogic;
    }

    /**
     * Gibt eine Kategorie anhand ihrer UUID wieder zurück. Weitergabe ans Repo.
     * @param uuid UUID der Kategorie
     * @return Kategorie mit entsprechender UUID
     */
    public Category getCategoryByUUID(String uuid) {
        return execTransactional(() -> categoryRepository.findByUUID(uuid));
    }

    /**
     * Gibt alle Kategorien für die Karte zurück. Weitergabe ans Repo
     * @param card Karte, für die die Kategorien abgerufen werden sollen.
     * @return Liste an Kategorien, die zur Karte gehören.
     */
    public List<Category> getCategoriesByCard(Card card) {
        return execTransactional(() ->categoryRepository.getCategoriesToCard(card));
    }


    /**
     * Gibt für eine einzelne Kategorie die Anzahl ihrer Karten wieder. Weitergabe getCardsInCategory.
     * @param category Die Kategorie für die die Anzahl abgerufen werden soll
     * @return Anzahl der Karten als int-Wert
     */
    public int numCardsInCategory(Category category) {
        return getCardsInCategory(category).size();
    }

    /**
     * Updated/Saved eine einzelne Kategorie mit ihren Daten. Nach Aufschlüsselung erfolgt Weitergabe ans Repository.
     * @param category Die upzudatende Kategorie
     * @param neu gibt an, ob die Karte neu erstellt werden muss oder nur aktualisiert werden muss
     */
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

    /**
     * Löscht eine einzelne Kategorie. Weitergabe ans Repository.
     * @param category Die zu löschende Kategorie.
     */
    public void deleteCategory(Category category) {
         execTransactional(() -> {
            categoryRepository.delete(category);
            cardToCategoryRepository
                    .delete(cardToCategoryRepository.getAllC2CForCategory(category));
            //TODO alle Parents und Childs der Kategorie müssen noch gelöscht werden generell oder? TODO in CategoryRepo
            return true;
        });
    }

    /**
     * Löscht mehrere Kategorien. Je Kategorie wird deleteCategory aufgerufen.
     * @param categories Liste an zu löschenden Kategorien.
     */
    public void deleteCategories(List<Category> categories) {
        for (Category c : categories) {
            deleteCategory(c);
        }
    }

    /**
     * Löscht einzelne CardToCategories.
     * @param c2d Die zu löschende Instanz
     */
    public void deleteCardToCategory(CardToCategory c2d) {
        execTransactional(() -> { cardToCategoryRepository.delete(c2d);
        return true;});
    }

    /**
     * Sucht für eine Kategoriebezeichnung die zugehörigen Karten. Prüft zunächst, ob der String null ist
     * @param categoryName Name der Kategorie
     * @return Liste an Karten mit Kategorie
     */
    public List<CardOverview> getCardsInCategory(String categoryName) {
        checkNotNullOrBlank(categoryName, "Kategorie",true);
        return execTransactional(() -> cardRepository.getCardsByCategory(categoryRepository.find(categoryName)));
    }

    /**
     * Sucht für eine Kategorie die zugehörigen Karten. Wird an das Repo weitergebeben.
     * @param category Kategorie für die Karten gesucht werden sollen
     * @return Liste an Karten mit Kategorie
     */
    public List<CardOverview> getCardsInCategory(Category category) {
       return execTransactional(() -> cardRepository.getCardsByCategory(category));
    }

    /**
     * Methode, um Card2Category bzw. CardHierarchy anzupassen. Dafür wird in 5 Schritten vorgegangen.
     * Zunächst werden die neu übergebenen Kategorien für die Karte/Hierarchieanpassung mit den bereits
     * bestehenden Kategorien in der DB verglichen.
     * - Gibt es noch keine Card2Categories bzw. Hierarchie für die Karte/Kategorie,
     * so werden alle neu hinzugefügt (Hilfsmethode: checkAndCreateCategories)
     * - Stimmen alte und neue Hierarchie/Card2Category überein, dann ist nichts zu tun und die Methode
     * kann verlassen werden. Dies ist z.B. der Fall, wenn im Editiermodus der Karte der Name der Karte, aber
     * nicht ihre Kategorien angepasst worden sind
     * - Wurden nur alte Kategorien entfernt, müssen diese aus dem Card2Category bzw. der Hierarchie gelöscht werden (Hilfsmethode:checkAndRemoveCategories=
     * - Wurden nur neue Kategorien hinzugefügt, müssen diese ebenfalls wie bei Schritt 1 hinzugefügt werden (Hilfsmethode: checkAndCreateCategories)
     * - Gibt es sowohl hinzugefügte Kategorien, als auch welche die entfernt werden müssen, werden beide Hilfsmethoden aufgerufen.
     * @param cardOrCategory Karte für Card2Category bzw. Category bei Hierarchieanpassung
     * @param catNew Alle neuen Kategorien für die Card2Category bzw. CategoryHierarchy
     * @param child Gibt an, ob die übergebene Kategorie in cardOrCategory ein child ist.
     * @param <T> Karte oder Kategorie, je nach Nutzung.
     */
    public <T> void setC2COrCH(T cardOrCategory, List<Category> catNew, boolean child) {
        if (!catNew.isEmpty()) {
            List<Category> catOld = new ArrayList<>();
            if (cardOrCategory instanceof Card card)
                catOld = getCategoriesByCard(card); //check Old Tags to remove unused tags
            else if (cardOrCategory instanceof Category category && !child)
                catOld = getChildrenForCategory(category);
            else if (cardOrCategory instanceof Category category)
                catOld = getParentsForCategory(category);

            if (catOld == null || catOld.isEmpty()) { //TODO:  vereinheitlichung rückgabewert
                checkAndCreateCategories(cardOrCategory, catNew, catOld, child);
            } else if (new HashSet<>(catOld).containsAll(catNew)) {
                if (catOld.size() == catNew.size()) {
                    //nothing to do
                } else { //nur Löschen
                    checkAndRemoveCategories(cardOrCategory, catNew, catOld, child);
                }
            } else if (new HashSet<>(catNew).containsAll(catOld)) {  // nur neue hinzufügen
                checkAndCreateCategories(cardOrCategory, catNew, catOld, child);
            } else { //neue und alte hinzufügen/entfernen
                checkAndCreateCategories(cardOrCategory, catNew, catOld, child);
                checkAndRemoveCategories(cardOrCategory, catNew, catOld, child);
            }
        }
    }



    /**
     * Hilfsmethode für das Löschen einzelner Kategorien in Card2Category bzw. der CategoryHierarchy
     * @param cardOrCategory  Karte für Card2Category bzw. Category bei Hierarchieanpassung
     * @param catNew Alle neuen Kategorien für die Card2Category bzw. CategoryHierarchy
     * @param child Gibt an, ob die übergebene Kategorie in cardOrCategory ein child ist.
     * @param catOld Alle alten Kategorienn für die Card2Category bzw. CategoryHierarchy
     * @param <T> Karte oder Kategorie, je nach Nutzung.
     */
    private <T> void checkAndRemoveCategories(final T cardOrCategory, List<Category> catNew, List<Category> catOld, boolean child) {
        execTransactional(() -> {
            //TODO: Auch noch Prüfung, ob Category überhaupt vorhanden?
        for (Category c : catOld) {
            if (!catNew.contains(c))
                if (cardOrCategory instanceof Card card) {
                    CardToCategory c2c = cardToCategoryRepository.getSpecific(card, c);
                    cardToCategoryRepository.delete(c2c);
                } else if (cardOrCategory instanceof Category category && !child) {
                    categoryRepository.deleteCategoryHierarchy(c, category);
                } else if (cardOrCategory instanceof Category category) {
                    categoryRepository.deleteCategoryHierarchy(category, c);
                }
        }
            return null;
        });
    }

    /**
     * Hilfsmethode für das Erstellen einzelner Kategorien in Card2Category bzw. der CategoryHierarchy.
     * Prüft vor dem Erstellen zunächst, ob die Kategorie bereits in der alten Liste enthalten ist.
     * Danach wird geprüft, ob die Kategorie mit dem Namen bereits existiert und
     * somit nicht neu erstellt werden muss.
     * @param cardOrCategory  Karte für Card2Category bzw. Category bei Hierarchieanpassung
     * @param catNew Alle neuen Kategorien für die Card2Category bzw. CategoryHierarchy
     * @param child Gibt an, ob die übergebene Kategorie in cardOrCategory ein child ist.
     * @param catOld Alle alten Kategorienn für die Card2Category bzw. CategoryHierarchy
     * @param <T> Karte oder Kategorie, je nach Nutzung.
     */
    private <T> void checkAndCreateCategories(final T cardOrCategory, List<Category> catNew, List<Category> catOld, boolean child) {
        execTransactional(() -> {
        for (Category c : catNew) {
            if (catOld != null && catOld.contains(c))
                log.info("Kategorie {} bereits in CardToCategory/ CategorieHierarchy enthalten, kein erneutes Hinzufügen notwendig", c.getUuid());
            else {
                try { Category category = categoryRepository.find(c.getName());
                    c = category;
                log.info("Kategorie mit Namen {} bereits in Datenbank enthalten", c.getName());}
                catch(NoResultException ex){
                    categoryRepository.save(c);
                }
                if (cardOrCategory instanceof Card card) {
                    log.info("Kategorie {} wird zu Karte {} hinzugefügt", c.getUuid(), card.getUuid());
                        cardToCategoryRepository.save(new CardToCategory(card, c));
                    } else if (cardOrCategory instanceof Category category && !child) {
                        log.info("Kategorie{} wird als Children zur KategorieHierarchie für Parent{} hinzugefügt", c.getName(), category.getName());
                        categoryRepository.saveCategoryHierarchy(c, category);
                    } else if (cardOrCategory instanceof Category category) {
                        log.info("Kategorie{} wird als Children zur KategorieHierarchie für Parent{} hinzugefügt", c.getName(), category.getName());
                        categoryRepository.saveCategoryHierarchy(category, c);
                    }
                }

            }
            return null; });

    }


    /**
     * Lädt alle Categories als Liste.
     * Werden in der CardEditPage als Dropdown angezeigt. 
     * Wird weitergegeben an das CategoryRepository.
     * @return Liste mit bestehenden Categories
     */
    public List<Category> getCategories() {
        return execTransactional(categoryRepository::getAll);
    }


    /**
     * Wird verwendet, um die CategoryHierarchy Elemente einer Kategorie anzupassen. Weitergabe an C2CCH Methode.
     * @param category Kategorie für die Parents und Children übergeben werden.
     * @param parents Parents der Kategorie
     * @param children Children der Kategorie
     */
    public void editCategoryHierarchy(Category category, List<Category> parents, List<Category> children) {

        setC2COrCH(category,parents,true);
        setC2COrCH(category,children,false);
    }


    /**
     * Verwendet, um alle Children für eine Kategorie auszugeben.
     * @param parent: Parent, zu dem die Children gesucht werden
     * @return Liste mit Kategorien die Children für Parent sind.
     */
    public List<Category> getChildrenForCategory(Category parent) {
        return execTransactional(() -> categoryRepository.getChildrenForCategory(parent));
    }

    /**
     * Verwendet, um alle Parents für eine Kategorie auszugeben.
     * @param child: Child, zu dem die Parents gesucht werden
     * @return Liste mit Kategorien die Parents für Child sind.
     */
    public List<Category> getParentsForCategory(Category child) {
        return execTransactional(() -> categoryRepository.getParentsForCategory(child));
    }


    /**
     * Lädt alle Categories ohne Parents als Set.
     * Wird weitergegeben an das CategoryRepository.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     * @return Liste mit bestehenden Categories
     */
    public List<Category> getRootCategories()
    {
        //Wahrscheinlich nicht die beste lösung
        List<Category> rootCats = new ArrayList<>();
        for(Category cat : getCategories())
        {
            if(getParentsForCategory(cat).size() == 0)
                rootCats.add(cat);
        }

        return rootCats;
    }
}