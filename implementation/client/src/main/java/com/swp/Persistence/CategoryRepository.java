package com.swp.Persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryRepository extends BaseRepository<Category> {
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

    public  Category findByUUID(String uuid) {
        return getEntityManager()
                .createNamedQuery("Category.findByUUID", Category.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
    }
    
    /**
     * Die Funktion `getCardToCategories` liefert alle gespeicherten `CardToCategory`-Objekte zurück.
     *
     * @return Set<CardToCategory> eine Menge mit allen `CardToCategory`
     */
    public  List<CardToCategory> getCardToCategories() {
       return CardToCategoryRepository.getInstance().getAll();
    }

    /**
     * Der Funktion `find` wird ein Name einer Kategorie übergeben.
     * Falls eine Kategorie mit entsprechendem Namen existiert, wird diese `Category` zurückgegeben.
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
     * Der Funktion `getCategoriesToCard` wird eine Karte übergeben.
     * Es werden alle Kategorien zurückgegeben, die dieser Karte zugeordnet sind.
     *
     * @param card eine Karte
     * @return Set<Category> eine Menge von Kategorien, die der Karte zugeordnet sind.
     */
    public List<Category> getCategoriesToCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allCategoriesOfCard", Category.class)
                .setParameter("card", card)
                .getResultList();
    }

    public void saveCategoryHierarchy(Category child, Category parent) {
        getEntityManager().persist(new CategoryHierarchy(child, parent));
    }

    public void deleteCategoryHierarchy(Category child, Category parent) {
        CategoryHierarchy ch = getEntityManager()
                .createNamedQuery("CategoryH.findSpecificCH", CategoryHierarchy.class)
                .setParameter("child", child)
                .setParameter("parent", parent)
                .getSingleResult();
        getEntityManager().remove(ch);
    }

    public List<Category> getChildrenForCategory(Category parent) {
        return getEntityManager()
                .createNamedQuery("CategoryH.getChildren", Category.class)
                .setParameter("parent", parent)
                .getResultList();
    }

    public List<Category> getParentsForCategory(Category child) {
        return getEntityManager()
                .createNamedQuery("CategoryH.getParents", Category.class)
                .setParameter("child", child)
                .getResultList();
    }

    public List<Category> getRoots() {
        return getEntityManager()
      .createQuery("SELECT c  FROM Category c WHERE NOT EXISTS (SELECT ch.child FROM CategoryHierarchy ch WHERE ch.child = c.uuid)", Category.class)
                .getResultList();
    }
}
