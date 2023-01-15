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
public class CategoryRepository extends BaseRepository<Category>
{
    private CategoryRepository() {
        super(Category.class);
    }

    // Singleton
    private static CategoryRepository categoryRepository = null;
    public static CategoryRepository getInstance()
    {
        if(categoryRepository == null)
            categoryRepository = new CategoryRepository();
        return categoryRepository;
    }

    public static Category findByUUID(String uuid) {
        return (Category) getEntityManager().createNamedQuery("Category.findByUUID")
                .setParameter("uuid", uuid)
                .getSingleResult();
    }
    private final static EntityManagerFactory emf = PersistenceManager.emFactory; // TODO: muss entfernt werden.

    /**
     * Die Funktion `saveCategory` persistiert die übergebene Kategorie.
     *
     * @param category eine Kategorie
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean saveCategory(Category category) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(category);
            em.getTransaction().commit();
        }
        log.info("Category {} wurde erfolgreich gespeichert", category.getUuid());
        return true;
    }

    /**
     * Die Funktion `saveCardToCategory` erstellt eine neue Verbindung zwischen einer Karte und einer Kategorie.
     * Dafür wird ein neues `CardToCategory`-Objekt erzeugt und persistiert.
     *
     * @param card     eine Karte
     * @param category eine Kategorie
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean saveCardToCategory(Card card, Category category) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(new CardToCategory(card, category));
            em.getTransaction().commit();
        }
        log.info("CardToCategory für Karte {} und Kategorie {} wurde erfolgreich gespeichert", card.getUuid(), category.getUuid());
        return true;
    }

    /**
     * Die Funktion `deleteCategory` löscht die angegebene Kategorie und alle Verbindungen,
     * die als `CardToCategory`-Objekte vorliegen.
     *
     * @param category die zu löschende Kategorie.
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean deleteCategory(Category category) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.remove(category); //TODO: was ist mit Referenzen auf andere Categories???
            // Alle CardToCategory entfernen, die die zu löschende Kategorie enthalten.
            Set<CardToCategory> c2cWithDeletedCategory;
            c2cWithDeletedCategory = (Set<CardToCategory>) em.createNamedQuery("CardToCategory.allC2CByCategory")
                    .setParameter("category", category)
                    .getResultStream().collect(Collectors.toList());
            for (CardToCategory c2c : c2cWithDeletedCategory) {
                em.remove(c2c);
            }
            em.getTransaction().commit();
        }
        return true;
    }

    /**
     * Die Funktion `getCategories` liefert alle gespeicherten `Category`-Objekte zurück.
     *
     * @return Set<Category> eine Menge mit allen `Category`
     */
    public static List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();//Cache.getInstance().getCategories();
        if (!categories.isEmpty()) {
            return categories;
        }
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            categories = (List<Category>) em.createQuery("SELECT c FROM Category c").getResultStream().collect(Collectors.toList());
            em.getTransaction().commit();
        }
        return categories;
    }

    /**
     * Die Funktion `getCardToCategories` liefert alle gespeicherten `CardToCategory`-Objekte zurück.
     *
     * @return Set<CardToCategory> eine Menge mit allen `CardToCategory`
     */
    public static List<CardToCategory> getCardToCategories() {
            List<CardToCategory> card2categories = new ArrayList<>();
//        if (!card2categories.isEmpty()) {
//            return card2categories;
//        }

        /////////////////////////////////////////////////////////////////
        //
        // TEMPORARY
        //
        ImageDescriptionCardAnswer[] answers = new ImageDescriptionCardAnswer[]{
                new ImageDescriptionCardAnswer("Orangenblatt", 75, 5),
                new ImageDescriptionCardAnswer("Orange", 61, 26),
                new ImageDescriptionCardAnswer("Nase", 40, 67),
                new ImageDescriptionCardAnswer("Hand", 82, 58),
                new ImageDescriptionCardAnswer("Fuß", 62, 89),
        };
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new MultipleChoiceCard("Multiple Choice Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", new String[]{"Answer 1", "Answer 2", "Answer 3"}, new int[]{1}, "MultipleChoiceCard", false));
        cards.add(new TrueFalseCard("True False Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", true, "TrueFalseCard", false));
        cards.add(new TextCard("Text Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "text answer", "TextCard", false));
        cards.add(new ImageDescriptionCard("Image Desc Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", answers, "ImageDescriptionCard", "textures/orange-ket.png", false));
        cards.add(new ImageTestCard("Image Test Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "image answer", "textures/orange-ket.png", "ImageTestCard", false, false));
        cards.add(new AudioCard("audios/thud.wav", "AudioCard", "Audio Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "audio answer", false, false));
        for (Category category : getCategories()) {
            for (Card card : cards) {
                CardToCategory cardtocategory = new CardToCategory(card, category);
              card2categories.add(cardtocategory);
            }
        }
        //Cache.getInstance().setCardToCategories(card2categories);
        return card2categories;
        /////////////////////////////////////////////////////////////////

        //server.send("/getcardtocategories", jsonString);
        //return null;
    }

    /**
     * Der Funktion `find` wird ein Name einer Kategorie übergeben.
     * Falls eine Kategorie mit entsprechendem Namen existiert, wird diese `Category` zurückgegeben.
     * Gibt es keine Kategorie mit diesem Namen, wird ein leeres `Optional` zurückgegeben.
     *
     * @param name einer Kategorie als String.
     * @return Optional<Category>
     */
    public static Optional<Category> find(final String name) {
        log.info(String.format("Rufe Kategorie für Namen %s ab", name));
        try (final EntityManager em = emf.createEntityManager()) {
            final Category ct = (Category) em.createNamedQuery("Category.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(ct);
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Der Funktion `getCardsByCategory` wird eine Kategorie übergeben.
     * Es werden alle Karten zurückgegeben, die dieser Kategorie zugeordnet sind.
     *
     * @param category eine Kategorie
     * @return Set<Card> eine Menge von Karten, die der Kategorie zugeordnet sind.
     */
    public static List<Card> getCardsByCategory(Category category) {
        List<Card> cards;
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            cards = (List<Card>) em.createNamedQuery("CardToCategory.allCardsOfCategory")
                    .setParameter("category", category)
                    .getResultStream().collect(Collectors.toList());
            em.getTransaction().commit();
        }
        return cards;
    }

    /**
     * Der Funktion `getCategoriesToCard` wird eine Karte übergeben.
     * Es werden alle Kategorien zurückgegeben, die dieser Karte zugeordnet sind.
     *
     * @param card eine Karte
     * @return Set<Category> eine Menge von Kategorien, die der Karte zugeordnet sind.
     */
    public static List<Category> getCategoriesToCard(Card card) {
        List<Category> categories;
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            categories = (List<Category>) em.createNamedQuery("CardToCategory.allCategoriesOfCard")
                    .setParameter("card", card)
                    .getResultStream().collect(Collectors.toList());
            em.getTransaction().commit();
        }
        if(!categories.isEmpty()) {
            return categories;
        }
        return null;
    }


    /**
     * @param category: Die zu updatende Category
     */
    public static boolean updateCategory(Category category) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(category);
            em.getTransaction().commit();
            return true;
        }
    }



    public static boolean deleteCardToCategory(Card card, Category c) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            CardToCategory C2C = (CardToCategory) em.createNamedQuery("CardToCategory.findSpecificC2C")
                    .setParameter("card", card)
                    .setParameter("category", c)
                    .getSingleResult();
            em.remove(C2C);
            em.getTransaction().commit();
            return true;
        }
    }

    public static boolean saveCategoryHierarchy(Category child, Category parent) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(new CategoryHierarchy(child, parent));
            em.getTransaction().commit();
        }
        log.info("Kategorie {} wurde erfolgreich als Child von Parent {} gespeichert", child.getUuid(), parent.getUuid());
        return true;
    }

    public static boolean deleteCategoryHierarchy(Category child, Category parent) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            CategoryHierarchy C2C = (CategoryHierarchy) em.createNamedQuery("CategoryH.findSpecificCH")
                    .setParameter("child", child)
                    .setParameter("parent", parent)
                    .getSingleResult();
            em.remove(C2C);
            em.getTransaction().commit();
            return true;
    }
}

    public static List<Category> getChildrenForCategory(Category parent) {
        List<Category> categories;
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            categories = (List<Category>) em.createNamedQuery("CategoryH.getChildren")
                    .setParameter("parent", parent)
                    .getResultStream().collect(Collectors.toList());
            em.getTransaction().commit();
        }
        return categories;
    }

    public static List<Category> getParentsForCategory(Category child) {
        List<Category> categories;
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            categories = (List<Category>) em.createNamedQuery("CategoryH.getParents")
                    .setParameter("child", child)
                    .getResultStream().collect(Collectors.toList());
            em.getTransaction().commit();
        }
        return categories;
    }
    }
