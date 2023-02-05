package com.swp.Persistence;

import com.swp.DataModel.*;

import java.util.List;

/**
 * Die Datenbank repository für CategoryHierarchy Objekte
 */
public class CategoryHierarchyRepository  extends BaseRepository<CategoryHierarchy> {
    private CategoryHierarchyRepository() {
        super(CategoryHierarchy.class);
    }

    // Singleton
    private static CategoryHierarchyRepository categoryHierarchyRepository = null;

    public static CategoryHierarchyRepository getInstance() {
        if (categoryHierarchyRepository == null)
            categoryHierarchyRepository = new CategoryHierarchyRepository();
        return categoryHierarchyRepository;
    }


    /**
     * Gibt alle Eltern-Kategorien einer angegebenen Kind-Kategorie zurück.
     *
     * @param category eine Kind-Kategorie
     * @return eine Liste aller Eltern-Kategorien der angegebenen Kind-Kategorie
     */
    public List<CategoryHierarchy> getAllChildrenAndParentsForCategory(Category category) {
        return getEntityManager()
                .createNamedQuery("CategoryH.getAllCHForCategory", CategoryHierarchy.class)
                .setParameter("category", category)
                .getResultList();
    }

    /**
     * Speichert für eine angegebene Kind-Kategorie und einer angegebenen Eltern-Kategorie
     * die entsprechende hierarchische Beziehung zwischen diesen beiden Kategorien in der Datenbank ab.
     *
     * @param child  eine untergeordnete Kategorie
     * @param parent eine übergeordnete Kategorie
     */
    public void saveCategoryHierarchy(Category child, Category parent) {
        getEntityManager().persist(new CategoryHierarchy(child, parent));
    }


    /**
     * Löscht eine Eltern-Kind-Verbindung zwischen der übergebenen Eltern- und Kind-Kategorie
     * aus der Datenbank.
     *
     * @param child die Kind-Kategorie, welche nicht mehr der Eltern-Kategorie untergeordnet sein soll.
     * @param parent die Eltern-Kategorie, welche nicht mehr die Kind-Kategorie untergeordnet haben soll.
     */
    public void deleteCategoryHierarchy(Category child, Category parent) {
        CategoryHierarchy ch = getEntityManager()
                .createNamedQuery("CategoryH.findSpecificCH", CategoryHierarchy.class)
                .setParameter("child", child)
                .setParameter("parent", parent)
                .getSingleResult();
        getEntityManager().remove(ch);
    }
}
