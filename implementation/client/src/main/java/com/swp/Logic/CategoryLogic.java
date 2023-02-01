package com.swp.Logic;

import java.util.*;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToCategoryRepository;
import com.swp.Persistence.CategoryHierarchyRepository;
import com.swp.Persistence.CategoryRepository;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;


import static com.swp.Validator.checkNotNullOrBlank;


/**
 * CategoryLogic Klasse, behandelt alle Category spezifischen Aufrufe.
 * Erbt von der BaseLogic.
 */
@Slf4j
public class CategoryLogic extends BaseLogic<Category> {
    /**
     * Konstruktor für eine CategoryLogic-Instanz.
     */
    private CategoryLogic() {
        super(CategoryRepository.getInstance());
    }
    /**
     * Instanz von CategoryLogic
     */
    private static CategoryLogic categoryLogic;

    /**
     * Hilfsmethode. Wird genutzt, damit nicht mehrere Instanzen von Kategorie entstehen.
     *
     * @return categoryLogic Instanz, die benutzt werden kann.
     */
    public static CategoryLogic getInstance() {
        if (categoryLogic == null)
            categoryLogic = new CategoryLogic();
        return categoryLogic;
    }

    /**
     * Benutze Repositories, auf die zugegriffen wird.
     */
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final CategoryRepository categoryRepository = CategoryRepository.getInstance();
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();
    private final CategoryHierarchyRepository categoryHierarchyRepository = CategoryHierarchyRepository.getInstance();


    /**
     * Gibt eine Kategorie anhand ihrer UUID wieder zurück. Weitergabe ans Repo.
     *
     * @param uuid UUID der Kategorie
     * @return Kategorie mit entsprechender UUID
     */
    public Category getCategoryByUUID(String uuid) {
        checkNotNullOrBlank(uuid, "UUID", true);
        return execTransactional(() -> categoryRepository.findByUUID(uuid));
    }

    /**
     * Gibt alle Kategorien für die Karte zurück. Weitergabe ans Repo
     *
     * @param card Karte, für die die Kategorien abgerufen werden sollen.
     * @return Liste an Kategorien, die zur Karte gehören.
     */
    public List<Category> getCategoriesByCard(Card card) {
        return execTransactional(() -> categoryRepository.getCategoriesToCard(card));
    }


    /**
     * Gibt für eine einzelne Kategorie die Anzahl ihrer Karten wieder. Weitergabe getCardsInCategory.
     *
     * @param category Die Kategorie für die die Anzahl abgerufen werden soll
     * @return Anzahl der Karten als int-Wert
     */
    public int numCardsInCategory(Category category) {
        return getCardsInCategory(category).size();
    }

    /**
     * Updated/Saved eine einzelne Kategorie mit ihren Daten. Nach Aufschlüsselung erfolgt Weitergabe ans Repository.
     *
     * @param category Die upzudatende Kategorie
     * @param neu      gibt an, ob die Karte neu erstellt werden muss oder nur aktualisiert werden muss
     */
    public void updateCategoryData(Category category, boolean neu, boolean nameChange) {
        if (category == null) {
            throw new IllegalStateException(Locale.getCurrentLocale().getString("categorynullerror"));
        }
        if (neu) {
            execTransactional(() -> {
                try {
                    Category catExists = categoryRepository.find(category.getName());
                    if (catExists != null) {
                        throw new IllegalArgumentException(Locale.getCurrentLocale().getString("categorywithnameexistsalready"));
                    }
                } catch (NoResultException ex) {
                    categoryRepository.save(category);
                }
                return true;
            });
        } else {
            execTransactional(() -> {
                if (nameChange) {
                    try {
                        Category catExists = categoryRepository.find(category.getName());
                        if (catExists != null) {
                            throw new IllegalArgumentException(Locale.getCurrentLocale().getString("categorywithnameexistsalready"));
                        }
                    } catch (NoResultException ex) {
                        categoryRepository.save(category);
                    }
                } else
                    categoryRepository.update(category);
                return true;
            });
        }
    }

    /**
     * Löscht eine einzelne Kategorie. Weitergabe ans Repository.
     *
     * @param category Die zu löschende Kategorie.
     */
    public void deleteCategory(Category category) {
        if (category == null) {
            throw new IllegalStateException(Locale.getCurrentLocale().getString("categorynullerror"));
        }
        execTransactional(() -> {
            cardToCategoryRepository
                    .delete(cardToCategoryRepository.getAllC2CForCategory(category));
            categoryHierarchyRepository.delete(categoryHierarchyRepository.getAllChildrenAndParentsForCategory(category));
            categoryRepository.delete(category);
            //TODO alle Parents und Childs der Kategorie müssen noch gelöscht werden generell oder? TODO in CategoryRepo
            return true;
        });
    }

    /**
     * Löscht mehrere Kategorien. Je Kategorie wird deleteCategory aufgerufen.
     *
     * @param categories Liste an zu löschenden Kategorien.
     */
    public void deleteCategories(List<Category> categories) {
        for (Category c : categories) {
            deleteCategory(c);
        }
    }

    /**
     * Löscht einzelne CardToCategories.
     *
     * @param c2d Die zu löschende Instanz
     */
    public void deleteCardToCategory(CardToCategory c2d) {
        execTransactional(() -> {
            cardToCategoryRepository.delete(c2d);
            return true;
        });
    }

    /**
     * Sucht für eine Kategoriebezeichnung die zugehörigen Karten. Prüft zunächst, ob der String null ist
     *
     * @param categoryName Name der Kategorie
     * @return Liste an Karten mit Kategorie
     */
    public List<CardOverview> getCardsInCategory(String categoryName) {
        checkNotNullOrBlank(categoryName, Locale.getCurrentLocale().getString("category"), true);
        return execTransactional(() -> cardRepository.getCardsByCategory(categoryName));
    }

    /**
     * Sucht für eine Kategorie die zugehörigen Karten. Wird an das Repo weitergebeben.
     *
     * @param category Kategorie für die Karten gesucht werden sollen
     * @return Liste an Karten mit Kategorie
     */
    public List<CardOverview> getCardsInCategory(Category category) {
        if (category == null) {
            throw new IllegalStateException(Locale.getCurrentLocale().getString("categorynullerror"));
        }
        return execTransactional(() -> cardRepository.getCardsByCategory(category));
    }

    /**
     * Holt für jede Kategorie, aus der übergebenen Liste, jeweils alle Karten und sammelt sie in einer Liste.
     * @param categories eine Liste von Kategorien.
     * @return eine Liste von allen Karten, die den übergebenen Kategorien zugeordnet sind.
     * @throws IllegalStateException falls eine der übergebenen Kategorien `null` ist.
     */
    public List<CardOverview> getCardsInCategories(List<Category> categories) throws IllegalStateException {
        if (categories == null || categories.isEmpty())
            throw new IllegalStateException(Locale.getCurrentLocale().getString("categorynullerror"));
        List<CardOverview> cardsToCategories = new ArrayList<>();
        for (Category c : categories) {
            if (c == null) {
                throw new IllegalStateException(Locale.getCurrentLocale().getString("categorynullerror"));
            }
            cardsToCategories.addAll(getCardsInCategory(c));
        }
        // Die zurückgegebene Liste kann Karten mehrfach enthalten, wenn sie mehreren Kategorien zugeordnet war.
        return cardsToCategories;
    }

    /**
     * Methode, um die CardToCategory einer Karte anzupassen. Dafür wird in 5 Schritten vorgegangen.
     * Zunächst werden die neu übergebenen Kategorien für die CardToCategory mit den bereits
     * bestehenden Kategorien in CardToCategory in der Datenbank verglichen.
     * - Gibt es noch keine CardToCategories für die Karte, so werden alle neu hinzugefügt (Hilfsmethode: checkAndCreateCardToCategories)
     * - Stimmen alte und neue CardToCategories überein, dann ist nichts zu tun und die Methode kann verlassen werden.
     * - Wurden nur alte Kategorien entfernt, müssen diese CardToCategories gelöscht werden (Hilfsmethode:checkAndRemoveCardToCategories)
     * - Wurden nur neue Kategorien hinzugefügt, müssen diese ebenfalls wie bei Schritt 1 als CardToCategories hinzugefügt werden (Hilfsmethode: checkAndCreateCardToCategories)
     * - Gibt es sowohl hinzugefügte Kategorien, als auch welche die entfernt werden müssen, werden beide Hilfsmethoden aufgerufen.
     *
     * @param card           Karte zu der Kategorien hinzugefügt/gelöscht oder beibehalten werden sollen.
     * @param catNew         Alle neuen Kategorien für die CardToCategory
     */
    public void setCardToCategories(Card card, List<Category> catNew) {
        List<Category> catOld = getCategoriesByCard(card);
        if (catOld.isEmpty()) {
            checkAndCreateCardToCategories(card, catNew, catOld);
        } else if (catOld.containsAll(catNew)) {
            if (catOld.size() == catNew.size()) {
                //nothing to do
            } else { //nur Löschen
                checkAndRemoveCardToCategories(card, catNew, catOld);
            }
        } else if (catNew.containsAll(catOld)) {  // nur neue hinzufügen
            checkAndCreateCardToCategories(card, catNew, catOld);
        } else { //neue und alte hinzufügen/entfernen
            checkAndCreateCardToCategories(card, catNew, catOld);
            checkAndRemoveCardToCategories(card, catNew, catOld);
        }
    }

    /**
     * Methode, um die CategoryHierarchy einer Kategorie anzupassen. Dafür wird in 5 Schritten vorgegangen.
     * Zunächst werden die neu übergebenen Kategorien für die Hierarchieanpassung mit den bereits
     * bestehenden Kategorien in der Datenbank verglichen.
     * - Gibt es noch keine Hierarchie für die Kategorie, so werden alle neu hinzugefügt (Hilfsmethode: checkAndCreateCategoryHierarchy)
     * - Stimmen alte und neue Hierarchie überein, dann ist nichts zu tun und die Methode kann verlassen werden.
     * - Wurden nur alte Kategorien entfernt, müssen diese aus der Hierarchie gelöscht werden (Hilfsmethode:checkAndCreateCategoryHierarchy)
     * - Wurden nur neue Kategorien hinzugefügt, müssen diese ebenfalls wie bei Schritt 1 hinzugefügt werden (Hilfsmethode: checkAndCreateCategoryHierarchy)
     * - Gibt es sowohl hinzugefügte Kategorien, als auch welche die entfernt werden müssen, werden beide Hilfsmethoden aufgerufen.
     *
     * @param category       Category für die Hierarchieanpassung
     * @param catNew         Alle neuen Kategorien für die CategoryHierarchy
     * @param child          Gibt an, ob die übergebene Kategorie ein child ist.
     */
    public Boolean setCategoryHierarchy(Category category, List<Category> catNew, boolean child) {
        List<Category> catOld = new ArrayList<>();
        Boolean selfreference = false;
          if (!child)
            catOld = getChildrenForCategory(category);
        else
            catOld = getParentsForCategory(category);

        if (catOld.isEmpty()) {
            selfreference = checkAndCreateCategoryHierarchy(category, catNew, catOld, child);
        } else if (catOld.containsAll(catNew)) {
            if (catOld.size() == catNew.size()) {
                //nothing to do
            } else { //nur Löschen
                checkAndRemoveCategoryHierarchy(category, catNew, catOld, child);
            }
        } else if (catNew.containsAll(catOld)) {  // nur neue hinzufügen
            selfreference = checkAndCreateCategoryHierarchy(category, catNew, catOld, child);
        } else { //neue und alte hinzufügen/entfernen
            selfreference = checkAndCreateCategoryHierarchy(category, catNew, catOld, child);
            checkAndRemoveCategoryHierarchy(category, catNew, catOld, child);
        }
        return selfreference;
    }


    /**
     * Hilfsmethode für das Löschen einzelner Card2Category Elemente
     * @param card           Karte für Card2Category
     * @param catNew         Alle neuen Kategorien für die Card2Category
     * @param catOld         Alle alten Kategorien für die Card2Category
     */
    private void checkAndRemoveCardToCategories(final Card card, List<Category> catNew, List<Category> catOld) {
        execTransactional(() -> {
            for (Category c : catOld) {
                if (!catNew.contains(c)){
                        CardToCategory c2c = cardToCategoryRepository.getSpecific(card, c);
                        cardToCategoryRepository.delete(c2c);
            }}
            return null;
        });
    }

    /**
     * Hilfsmethode für das Löschen einzelner CardHierarchy Elemente
     * @param category       Category bei der die Hierarchie angepasst wird
     * @param catNew         Alle neuen Kategorien für die CategoryHierarchy
     * @param catOld         Alle alten Kategorienn für die CategoryHierarchy
     * @param child          Gibt an, ob die übergebene Kategorie, für die die CategoryHierarchy verändert werden soll, ein Child ist
     */
    private void checkAndRemoveCategoryHierarchy(final Category category, List<Category> catNew, List<Category> catOld, boolean child) {
        execTransactional(() -> {
            for (Category c : catOld) {
                if (!catNew.contains(c))
                  if (!child) {
                        categoryHierarchyRepository.deleteCategoryHierarchy(c, category);
                    }
                else  {
                        categoryHierarchyRepository.deleteCategoryHierarchy(category, c);
                    }
            }
            return null;
        });
    }


    /**
     * Hilfsmethode für das Erstellen einzelner Card2Category Elemente.
     * Prüft vor dem Erstellen zunächst, ob die Kategorie bereits in der alten Liste enthalten ist.
     * Danach wird mit einer Hilfsmethode geprüft, ob die Kategorie mit dem Namen bereits existiert und
     * somit nicht neu erstellt werden muss. Dann wird die CardToCategory erstellt.
     *
     * @param card            Karte für Card2Category
     * @param catNew         Alle neuen Kategorien für die Card2Category bzw. CategoryHierarchy
     * @param catOld         Alle alten Kategorienn für die Card2Category bzw. CategoryHierarchy
     */
    private void checkAndCreateCardToCategories(final Card card, List<Category> catNew, List<Category> catOld) {
        execTransactional(() -> {
            for (Category c : catNew) {
                if (!catOld.isEmpty() && catOld.contains(c))
                    log.info("Kategorie {} bereits in CardToCategory enthalten, kein erneutes Hinzufügen notwendig", c.getUuid());
                else {
                   Category cat = checkAndFindOrCreateCategory(c);
                        log.info("Kategorie {} wird zu Karte {} hinzugefügt", cat.getUuid(), card.getUuid());
                        cardToCategoryRepository.save(new CardToCategory(card, cat));
                }
            }
            return null;
        });

    }

    /**
     * Hilfsmethode für das Erstellen einzelner CategoryHierarchy Elemente.
     * Prüft vor dem Erstellen zunächst, ob die Kategorie bereits in der alten Liste enthalten ist.
     * Danach wird mittels einer Hilfsmethode geprüft, ob die Kategorie mit dem Namen bereits existiert und
     * somit nicht neu erstellt werden muss.
     *
     * @param category       Category für die Hierarchieanpassung
     * @param catNew         Alle neuen Kategorien für die CategoryHierarchy Erstellung
     * @param catOld         Alle alten Kategorien der CategoryHierarchy
     * @param child          Gibt an, ob die übergebene Kategorie ein child ist.
     */
    private Boolean checkAndCreateCategoryHierarchy(final Category category, List<Category> catNew, List<Category> catOld, boolean child) {
        return execTransactional(() -> {
            boolean selfreference = false;
            for (Category c : catNew) {
                if (!catOld.isEmpty() && catOld.contains(c))
                    log.info("Kategorie {} bereits in CategorieHierarchy enthalten, kein erneutes Hinzufügen notwendig", c.getUuid());
                if(category.equals(c)){
                    log.error("Selbstreferenz einer Kategorie in der Hierarchie nicht möglich");
                    selfreference = true;
                }
                else {
                    Category cat = checkAndFindOrCreateCategory(c);
                    if(!child) {
                        log.info("Kategorie {} wird als Children zur KategorieHierarchie für Parent {} hinzugefügt", c.getName(), category.getName());
                        categoryHierarchyRepository.saveCategoryHierarchy(cat, category);
                }
                     else  {
                        log.info("Kategorie {} wird als Children zur KategorieHierarchie für Parent {} hinzugefügt",  category.getName(), c.getName());
                        categoryHierarchyRepository.saveCategoryHierarchy(category, cat);
                    }
                }
            }
            return selfreference;
        });
    }

    /**
     * Hilfsmethode für das Prüfen, ob einzelne Kategorien mit Bezeichnung bereits in Datenbank enthalten sind, ansonsten werden diese neu erstellt.
     * Wird für das Anpassen der Hierarchie sowie beim Erstellen von CardToCategories verwendet.
     * @param c: Die zu prüfende Kategorie
     * @return: Die alte Kategorie oder die neu gespeicherte
     */
    private Category checkAndFindOrCreateCategory(Category c) {
                    try {
                        Category category = categoryRepository.find(c.getName());
                        c = category;
                        log.info("Kategorie mit Namen {} bereits in Datenbank enthalten", c.getName());
                    } catch (NoResultException ex) {
                       categoryRepository.save(c); //sollte aktuell gar nicht passieren, da nur aus bereits bestehenden ausgewählt werden kann?
                    }
            return c;
    }

    /**
     * Lädt alle Categories als Liste.
     * Werden in der CardEditPage als Dropdown angezeigt.
     * Wird weitergegeben an das CategoryRepository.
     *
     * @return Liste mit bestehenden Categories
     */
    public List<Category> getCategories() {
        return execTransactional(categoryRepository::getAll);
    }


    /**
     * Wird verwendet, um die CategoryHierarchy Elemente einer Kategorie anzupassen. Weitergabe an C2CCH Methode.
     *
     * @param category Kategorie für die Parents und Children übergeben werden.
     * @param parents  Parents der Kategorie
     * @param children Children der Kategorie
     */
    public Boolean editCategoryHierarchy(Category category, List<Category> parents, List<Category> children) {
        if(setCategoryHierarchy(category, parents, true) || setCategoryHierarchy(category, children, false))
            return true;

        else
            return false;
    }


    /**
     * Verwendet, um alle Children für eine Kategorie auszugeben.
     *
     * @param parent: Parent, zu dem die Children gesucht werden
     * @return Liste mit Kategorien die Children für Parent sind.
     */
    public List<Category> getChildrenForCategory(Category parent) {
        return execTransactional(() -> categoryRepository.getChildrenForCategory(parent));
    }

    /**
     * Verwendet, um alle Parents für eine Kategorie auszugeben.
     *
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
     *
     * @return Liste mit bestehenden Categories
     */
    public List<Category> getRootCategories(boolean bReverseOrder) {
        return execTransactional(() -> {
            if (bReverseOrder)
              return   categoryRepository.getRoots("desc");

            else
               return categoryRepository.getRoots("asc");

        });
    }

    /**
     *  Wird verwendet, um eine Liste von Karten in einer Kategorie zu löschen.
     * @param cards: Die zu löschenden Karten aus der Kategorie
     * @param category: Die Kategorie, für die die Karten gelöscht werden sollen
     */
    public void removeCardsFromCategory(List<CardOverview> cards, Category category) {
        execTransactional(() -> {
            List<Card> cards1 = cardRepository.getAllCardsForCardOverview(cards);
            for(Card c: cards1)
                cardToCategoryRepository.delete(cardToCategoryRepository.getSpecific(c, category));
            return null;
        });
    }
    /**
     * Methode wird verwendet, um passende Kategorien für die angegebenen Suchwörter zu identifizieren. Prüft zunächst,
     * dass der übergebene Tag nicht null oder leer ist und gibt die Funktion dann an das Category Repository weiter.
     *
     * @param searchterm Eingegebenes Suchwort
     * @return Set der Karten, die Suchwörter enthalten.
     */
    public List<Category> getCategoriesBySearchterms(String searchterm) {
        checkNotNullOrBlank(searchterm, Locale.getCurrentLocale().getString("searchterm"), true);
        return execTransactional(() -> categoryRepository.findCategoriesContaining(searchterm));
    }
}