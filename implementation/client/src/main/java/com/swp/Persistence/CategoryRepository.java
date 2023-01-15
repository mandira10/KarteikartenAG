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

    public static Category findByUUID(String uuid) {
        return getEntityManager()
                .createNamedQuery("Category.findByUUID", Category.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
    }

    /**
     * Die Funktion `saveCategory` persistiert die übergebene Kategorie.
     *
     * @param category eine Kategorie
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean saveCategory(Category category) {
        getInstance().save(category);
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
        // die Funktion sollte wahrscheinlich nur im CardToCategoryRepository existieren.
        CardToCategoryRepository.getInstance().save(new CardToCategory(card, category));
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
        getInstance().delete(category);
        return true;
    }

    /**
     * Die Funktion `getCardToCategories` liefert alle gespeicherten `CardToCategory`-Objekte zurück.
     *
     * @return Set<CardToCategory> eine Menge mit allen `CardToCategory`
     */
    public static List<CardToCategory> getCardToCategories() {
        // TODO: diese Funktion sollte nicht verwendet werden, sondern folgendes:
        // CardToCategoryRepository.getInstance().getAll();

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
        //cards.add(new AudioCard("audios/thud.wav", "AudioCard", "Audio Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "audio answer", false, false));
        for (Category category : CategoryRepository.getInstance().getAll()) {
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
     *
     * @param name einer Kategorie als String.
     * @return Category die gefundene Kategorie
     * @throws NoResultException falls keine Kategorie mit entsprechendem Namen existiert
     */
    public static Category find(final String name) throws NoResultException {
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
    public static List<Category> getCategoriesToCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allCategoriesOfCard", Category.class)
                .setParameter("card", card)
                .getResultList();
    }

    public boolean saveCategoryHierarchy(Category child, Category parent) {
        getEntityManager().persist(new CategoryHierarchy(child, parent));
        return true;
    }

    public static boolean deleteCategoryHierarchy(Category child, Category parent) {
        CategoryHierarchy ch = getEntityManager()
                .createNamedQuery("CategoryH.findSpecificCH", CategoryHierarchy.class)
                .setParameter("child", child)
                .setParameter("parent", parent)
                .getSingleResult();
        getEntityManager().remove(ch);
        return true;
    }

    public static List<Category> getChildrenForCategory(Category parent) {
        return getEntityManager()
                .createNamedQuery("CategoryH.getChildren", Category.class)
                .setParameter("parent", parent)
                .getResultList();
    }

    public static List<Category> getParentsForCategory(Category child) {
        return getEntityManager()
                .createNamedQuery("CategoryH.getParents", Category.class)
                .setParameter("child", child)
                .getResultList();
    }
}
