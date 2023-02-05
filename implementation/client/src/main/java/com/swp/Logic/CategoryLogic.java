package com.swp.Logic;

import java.util.*;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.GUI.Extras.ListOrder;
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
public class CategoryLogic extends BaseLogic<Category>
{
    /**
     * Instanz von CategoryLogic
     */
    private static CategoryLogic categoryLogic;

    /**
     * Konstruktor für eine CategoryLogic-Instanz.
     */
    private CategoryLogic()
    {
        super(CategoryRepository.getInstance());
    }

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
        checkNotNullOrBlank(uuid);
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
     * Updated/Saved eine einzelne Kategorie mit ihren Daten. Nach Aufschlüsselung erfolgt Weitergabe ans Repository.
     *
     * @param category   Die upzudatende Kategorie
     * @param neu        gibt an, ob die Karte neu erstellt werden muss oder nur aktualisiert werden muss
     * @param nameChange Hat sich der name der Kategorie geändert?
     */
    public void updateCategoryData(Category category, boolean neu, boolean nameChange) {
        if (category == null) {
            throw new IllegalStateException("categorynullerror");
        }
        if (neu) {
            execTransactional(() -> {
                try {
                    Category catExists = categoryRepository.find(category.getName());
                    if (catExists != null) {
                        throw new IllegalArgumentException("categorywithnameexistsalready");
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
                            throw new IllegalArgumentException("categorywithnameexistsalready");
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
                    .delete(CardToCategoryRepository.getAllC2CForCategory(category));
            categoryHierarchyRepository.delete(categoryHierarchyRepository.getAllChildrenAndParentsForCategory(category));
            categoryRepository.delete(category);
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
     * Sucht für eine Kategoriebezeichnung die zugehörigen Karten. Prüft zunächst, ob der String null ist
     *
     * @param categoryName Name der Kategorie
     * @return Liste an Karten mit Kategorie
     */
    public List<CardOverview> getCardsInCategory(String categoryName) {
        checkNotNullOrBlank(categoryName);
        return execTransactional(() -> cardRepository.getCardsByCategory(categoryName));
    }


    /**
     * Gibt alle Karten in einer Kategorie wieder
     *
     * @param categoryName Der name der Kategorie
     * @param order        Die Reihenfolge
     * @param reverseOrder Ob die Reihenfolge verkehrtherum sein soll
     * @return Die liste der Karten in der Kategorie
     */
    public List<CardOverview> getCardsInCategory(String categoryName, ListOrder.Order order, boolean reverseOrder) {
        checkNotNullOrBlank(categoryName);
        return execTransactional(() -> {
            List<CardOverview> cards = new ArrayList<>();
            switch (order) {
                case ALPHABETICAL -> {
                    if (!reverseOrder)
                        cards = cardRepository.getCardsByCategory(categoryName, "co.titelToShow", "asc");
                    else
                        cards = cardRepository.getCardsByCategory(categoryName, "co.titelToShow", "desc");
                }
                case DATE -> {
                    if (!reverseOrder)
                        cards = cardRepository.getCardsByCategory(categoryName, "co.cardCreated", "asc");
                    else
                        cards = cardRepository.getCardsByCategory(categoryName, "co.cardCreated", "desc");
                }
                case NUM_DECKS -> {
                    if (!reverseOrder)
                        cards = cardRepository.getCardsByCategory(categoryName, "co.countDecks", "asc");
                    else
                        cards = cardRepository.getCardsByCategory(categoryName, "co.countDecks", "desc");
                }
            }
            return cards;
        });
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
        }
        else if (catOld.containsAll(catNew))
        {
            if (catOld.size() != catNew.size())
                checkAndRemoveCardToCategories(card, catNew, catOld);
        }
        else if (catNew.containsAll(catOld)) // nur neue hinzufügen
        {
            checkAndCreateCardToCategories(card, catNew, catOld);
        }
        else //neue und alte hinzufügen/entfernen
        {
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
     * @return Gibt bei selbstreferenzierung true wieder
     */
    public Boolean setCategoryHierarchy(Category category, List<Category> catNew, boolean child)
    {
        Boolean selfreference = false;

        List<Category> catOld;
        if (!child)
            catOld = getChildrenForCategory(category);
        else
            catOld = getParentsForCategory(category);

        if(catOld.isEmpty())
        {
            selfreference = checkAndCreateCategoryHierarchy(category, catNew, catOld, child);
        }
        else if(catOld.containsAll(catNew))
        {
            if(catOld.size() != catNew.size())
                checkAndRemoveCategoryHierarchy(category, catNew, catOld, child);
        }
        else if(catNew.containsAll(catOld)) // nur neue hinzufügen
        {
            selfreference = checkAndCreateCategoryHierarchy(category, catNew, catOld, child);
        }
        else // neue und alte hinzufügen/entfernen
        {
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
                if (!catOld.isEmpty() && catOld.contains(c)) {
                    log.info("Kategorie {} bereits in CategorieHierarchy enthalten, kein erneutes Hinzufügen notwendig", c.getUuid());
                }
                else if(category.equals(c)){
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
        try
        {
            c = categoryRepository.find(c.getName());
            log.info("Kategorie mit Namen {} bereits in Datenbank enthalten", c.getName());
        }
        catch (NoResultException ex)
        {
            //sollte aktuell gar nicht passieren, da nur aus bereits bestehenden ausgewählt werden kann?
            categoryRepository.save(c);
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
     * @return true, wenn Doppelreferenz vorhanden, false wenn nicht.
     */
    public Boolean editCategoryHierarchy(Category category, List<Category> parents, List<Category> children) {
        if(category == null || parents == null || children == null){
            throw new IllegalStateException("categorynullerror");
        }
        boolean bolP = setCategoryHierarchy(category, parents, true);
        boolean bolC = setCategoryHierarchy(category, children, false);

        //after editing check for doubleReference
        return checkForDoubleReference(category) || bolP || bolC;
    }

    /**
     * Prüft, ob für eine Kategorie Doppelreferenzierungen (Child und Parent identisch) existieren und
     * löscht diese. Gibt true zurück, so dass der User die Info bekommt.
     * @param category zu prüfende Kategorie
     * @return true, wenn Doppelreferenz vorhanden, false wenn nicht.
     */
    private boolean checkForDoubleReference(Category category) {
        return execTransactional(() -> {
           List<Category> catDouble = categoryRepository.checkDoubleReference(category);
           if(!catDouble.isEmpty()){
               log.info("Es wurden {} Doppelreferenzen gefunden", catDouble.size());
               for(Category c : catDouble){
                   categoryHierarchyRepository.deleteCategoryHierarchy(c,category);
                   categoryHierarchyRepository.deleteCategoryHierarchy(category,c);
               }
               return true;
           }
           else {
               return false;
           }
        });
    }



    /**
     * Verwendet, um alle Children für eine Kategorie auszugeben.
     *
     * @param parent: Parent, zu dem die Children gesucht werden
     * @return Liste mit Kategorien die Children für Parent sind.
     */
    public List<Category> getChildrenForCategory(Category parent) {
        if (parent == null) {
            throw new IllegalStateException("categorynullerror");
        }
        return execTransactional(() -> categoryRepository.getChildrenForCategory(parent));
    }

    /**
     * Verwendet, um alle Parents für eine Kategorie auszugeben.
     *
     * @param child: Child, zu dem die Parents gesucht werden
     * @return Liste mit Kategorien die Parents für Child sind.
     */
    public List<Category> getParentsForCategory(Category child) {
        if (child == null) {
            throw new IllegalStateException("categorynullerror");
        }
        return execTransactional(() -> categoryRepository.getParentsForCategory(child));
    }


    /**
     * Lädt alle Categories ohne Parents als Set.
     * Wird weitergegeben an das CategoryRepository.
     * Werden zudem verwendet, um die Baumstruktur der Categories anzuzeigen
     *
     * @param bReverseOrder sortiert die Rückgabe entsprechend (true/desc und false/asc)
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
        if(category  == null)
            throw new IllegalStateException("categorynullerror");

        execTransactional(() -> {
            List<Card> cards1 = cardRepository.getAllCardsForCardOverview(cards);
            for(Card c: cards1){
                if(c == null)
                    throw new IllegalStateException("cardnullerror");

                cardToCategoryRepository.delete(cardToCategoryRepository.getSpecific(c, category));}
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
        checkNotNullOrBlank(searchterm);
        return execTransactional(() -> categoryRepository.findCategoriesContaining(searchterm));
    }


}