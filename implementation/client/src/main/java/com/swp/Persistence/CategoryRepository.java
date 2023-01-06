package com.swp.Persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;

import com.swp.DataModel.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryRepository {


    private static final PersistenceManager pm = new PersistenceManager();

    public static boolean saveCategory(Category category) {
        final EntityManager em = pm.getEntityManager();
        em.getTransaction().begin();
        em.persist(category);
        em.getTransaction().commit();
        log.info("Category {} wurde erfolgreich gespeichert", category.getUuid());
        return true;
    }

    public static boolean saveCardToCategory(Card card, Category category) {
        final EntityManager em = pm.getEntityManager();//) {
        em.getTransaction().begin();
        em.persist(new CardToCategory(card, category));
        em.getTransaction().commit();
        log.info("CardToCategory für Karte {} und Kategorie {} wurde erfolgreich gespeichert", card.getUuid(), category.getUuid());
        return true;
    }

    public static boolean deleteCategory(Category card) {
        //server.send("/deletecategory", jsonString);
        return false;
    }

    public static Set<Category> getCategories() 
    {
        Set<Category> categories = Cache.getInstance().getCategories();
        if (!categories.isEmpty())
            return categories;


        try (final EntityManager em = pm.getEntityManager()) 
        {
            em.getTransaction().begin();
            categories = (Set<Category>) em.createQuery("SELECT c FROM Category c").getResultStream().collect(Collectors.toSet());
            em.getTransaction().commit();

            return categories;
        } 
        catch (final Exception e) 
        {
            log.warn("Beim abrufen der Categories ist einer Fehler aufgetreten: " + e);
            return null;
        }

        //server.send("/getcategories", jsonString);
    }

    public static Set<CardToCategory> getCardToCategories() {
        Set<CardToCategory> card2categories = Cache.getInstance().getCardToCategories();
        if (!card2categories.isEmpty())
            return card2categories;

        
        /////////////////////////////////////////////////////////////////
        //
        // TEMPORARY
        //
        ImageDescriptionCardAnswer[] answers = new ImageDescriptionCardAnswer[] {
            new ImageDescriptionCardAnswer("Orangenblatt", 75, 5),
            new ImageDescriptionCardAnswer("Orange",       61, 26),
            new ImageDescriptionCardAnswer("Nase",         40, 67),
            new ImageDescriptionCardAnswer("Hand",         82, 58),
            new ImageDescriptionCardAnswer("Fuß",          62, 89),
        };
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new MultipleChoiceCard("Multiple Choice Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", new String[] {"Answer 1", "Answer 2", "Answer 3"}, new int[] {1}, "MultipleChoiceCard", false));
        cards.add(new TrueFalseCard("True False Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", true, "TrueFalseCard", false));
        cards.add(new TextCard("Text Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "text answer", "TextCard", false));
        cards.add(new ImageDescriptionCard("Image Desc Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", answers, "ImageDescriptionCard", "textures/orange-ket.png", false));
        cards.add(new ImageTestCard("Image Test Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "image answer", "textures/orange-ket.png", "ImageTestCard", false, false));
        cards.add(new AudioCard("audios/thud.wav", "AudioCard", "Audio Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "audio answer", false, false));
        for(Category category : getCategories())
        {
            for(Card card : cards)
            {
                CardToCategory cardtocategory = new CardToCategory(card, category);
                card2categories.add(cardtocategory);
            }
        }
        Cache.getInstance().setCardToCategories(card2categories);
        return card2categories;
        /////////////////////////////////////////////////////////////////

        //server.send("/getcardtocategories", jsonString);
        //return null;
    }

    public static Optional<Category> find(final String name) {
        log.info(String.format("Rufe Kategorie für Namen %s ab", name));
        try (final EntityManager em = pm.getEntityManager()) {
            final Category ct = (Category) em.createNamedQuery("Category.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(ct);
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }


    public static  Set<Card> getCardsByCategory(Category category) {
        Set<Card> cards = new HashSet<>();
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("CardToCategory.allCardsOfCategory")
                    .setParameter("category", category)
                    .getResultStream().collect(Collectors.toSet());
            em.getTransaction().commit();
        }
        return cards;
    }


    public static Set<Category> getCategoriesToCard(Card card) {
        Set<Category> categories = Cache.getInstance().getCategories();

        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            categories = (Set<Category>) em.createNamedQuery("CardToCategory.allCategoriesOfCard")
                            .setParameter("card", card)
                                    .getResultStream().collect(Collectors.toSet());
            em.getTransaction().commit();
        }

        return categories;

    }

    /**
     * @param oldcategory
     * @param newcategory
     */
    public static boolean updateCategory(Category oldcategory, Category newcategory) {

        //server.send("/updatecategorydata", jsonString);
        return false;
    }
}