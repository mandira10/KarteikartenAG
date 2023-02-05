package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import jakarta.persistence.NoResultException;

import java.util.List;

/**
 * Die Datenbank repository für Kategorien
 * @author Ole-Nikas Mahlstädt
 */
public class CategoryRepository extends BaseRepository<Category> 
{
    private CategoryRepository() {
        super(Category.class);
    }

    // Singleton
    private static CategoryRepository categoryRepository = null;

    public static CategoryRepository getInstance() {
        if (categoryRepository == null)
            categoryRepository = new CategoryRepository();
        return categoryRepository;
    }

    /**
     * Holt für eine angegebene UUID die entsprechende Kategorie aus der Datenbank.
     * Sollte es keine Kategorie mit dieser UUID geben, so wird eine Exception geworfen.
     *
     * @param uuid die UUID einer Kategorie als String.
     * @return eine Kategorie mit entsprechender UUID.
     * @throws NoResultException falls keine Kategorie mit angegebener UUID in der Datenbank existiert.
     */
    public Category findByUUID(String uuid) throws NoResultException {
        return getEntityManager()
                .createNamedQuery("Category.findByUUID", Category.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
    }

    /**
     * Holt für einen angegebenen Namen die entsprechende Kategorie aus der Datenbank.
     * Falls keine Kategorie mit entsprechendem Namen existiert, wird eine Exception geworfen.
     *
     * @param name einer Kategorie als String.
     * @return Category die gefundene Kategorie
     * @throws NoResultException falls keine Kategorie mit entsprechendem Namen existiert
     */
    public Category find(final String name) throws NoResultException {
        return getEntityManager()
                .createNamedQuery("Category.findByName", Category.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    /**
     * Holt für eine angegebene Karte eine Liste von Kategorien, die der Karte zugeordnet sind,
     * aus der Datenbank. Falls die Karte keiner Kategorie zugeordnet ist, so wird eine leere Liste zurückgegeben.
     *
     * @param card eine Karte
     * @return List<Category> eine Liste von Kategorien, die der Karte zugeordnet sind.
     */
    public List<Category> getCategoriesToCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allCategoriesOfCard", Category.class)
                .setParameter("card", card)
                .getResultList();
    }



    /**
     * Holt für eine angegebene Kategorie eine Liste aller untergeordneten Kategorien aus der Datenbank.
     * Sollte der Kategorie keine weiteren untergeordnet sein, so wird eine leere Liste zurückgegeben.
     *
     * @param parent die Kategorie für die alle Kind-Kategorien geholt werden sollen.
     * @return eine Liste von untergeordneten Kategorien.
     */
    public List<Category> getChildrenForCategory(Category parent) {
        return getEntityManager()
                .createNamedQuery("CategoryH.getChildren", Category.class)
                .setParameter("parent", parent)
                .getResultList();
    }

    /**
     * Holt für eine angegebene Kategorie alle übergeordneten Eltern-Kategorien aus der Datenbank.
     * Sollte es keine übergeordneten Kategorien geben, so wird eine leere Liste zurückgegeben.
     *
     * @param child die Kategorie für die alle Eltern-Kategorien geholt werden sollen.
     * @return eine Liste von übergeordneten Kategorien.
     */
    public List<Category> getParentsForCategory(Category child) {
        return getEntityManager()
                .createNamedQuery("CategoryH.getParents", Category.class)
                .setParameter("child", child)
                .getResultList();
    }

    /**
     * Holt die 'Wurzel-Kategorien' aus der Datenbank.
     * Diese Kategorien haben keine übergeordneten Eltern-Kategorien.
     *
     * @param order Die Reihenfolge
     * @return eine Liste von Wurzel-Kategorien.
     */
    public List<Category> getRoots(String order) {
        return getEntityManager()
                .createQuery("SELECT c  FROM Category c WHERE NOT EXISTS (SELECT ch.child FROM CategoryHierarchy ch WHERE ch.child = c.uuid) ORDER BY LOWER(c.name) " +  order, Category.class)
                .getResultList();
    }


    /**
     * Prüft für eine bestimmte Kategorie,
     * ob diese eine Doppelreferenz (Child/Parent) mit einer anderen Kategorie hat.
     * Wird bei editCategoryHierarchy aufgerufen, um sicherzustellen, dass wenn Doppelreferenzen erstellt wurden, diese
     * identifiziert und gelöscht werden.
     *
     * @param category Die Kategorie, welche geprüft werden soll
     * @return eine Liste von Doppelreferenzen.
     */
    public List<Category> checkDoubleReference(Category category) {
        return getEntityManager()
                .createQuery("SELECT ch1.parent FROM CategoryHierarchy ch1 WHERE ch1.child = :category AND EXISTS( SELECT ch2.id from CategoryHierarchy ch2 WHERE ch2.parent = :category AND ch2.child = ch1.parent)", Category.class)
                .setParameter("category",category)
                .getResultList();
    }

    /**
     * Holt aus der Datenbank eine Liste von Kategorien.
     * Es wird danach gefiltert, ob die Kategorie das angegebene Suchwort als
     * Teilstring im Inhalt hat.
     *
     * @param terms ein String nach dem im Inhalt aller Karten gesucht werden soll.
     * @return List<Category> eine List von Kategorien, welche `terms` als Teilstring im Inhalt hat.
     */
    public List<Category> findCategoriesContaining(String terms){
        return getEntityManager()
                .createNamedQuery("Category.findCategoriesByContent", Category.class)
                .setParameter("content", "%" + terms + "%")
                .getResultList();
    }
}