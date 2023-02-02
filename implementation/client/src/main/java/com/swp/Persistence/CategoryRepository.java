package com.swp.Persistence;

import java.util.List;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import jakarta.persistence.NoResultException;

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
     * @return eine Liste von Wurzel-Kategorien.
     */
    public List<Category> getRoots(String order) {
        return getEntityManager()
                .createQuery("SELECT c  FROM Category c WHERE NOT EXISTS (SELECT ch.child FROM CategoryHierarchy ch WHERE ch.child = c.uuid) ORDER BY c.name " +  order, Category.class)
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