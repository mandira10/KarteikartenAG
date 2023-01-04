package com.swp.Persistence;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.swp.DataModel.*;
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

    public static Set<Category> getCategories() {
        Set<Category> categories = Cache.getInstance().getCategories();

        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            categories = (Set<Category>) em.createQuery("SELECT c FROM Category c").getResultStream().collect(Collectors.toSet());
            em.getTransaction().commit();
        } catch (final Exception e) {
            log.warn("Beim abrufen der Categories ist einer Fehler aufgetreten: " + e);
        }

        if (!categories.isEmpty())
            return categories;

      return null;
    }

    public static Set<CardToCategory> getCardToCategories(String category) {
        Set<CardToCategory> cats = Cache.getInstance().getCardToCategories();
        if (!cats.isEmpty())
            return cats;

        //server.send("/getcardtocategories", jsonString);
        return null;
    }

    public static Optional<Category> find(final String name) {
        log.info(String.format("Rufe Kategorie für Namen %s ab", name));
        try (final EntityManager em = pm.getEntityManager()) {
            final Category category = (Category) em.createNamedQuery("Category.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(category);
        } catch (final NoResultException e) {
            log.info("Keine Kategorie mit dem Namen {} gefunden", name);
        } catch (final Exception e) {
            log.warn("Beim Suchen nach Kategorien mit dem Namen {} ist ein Fehler {} aufgetreten",
                    name, e);
        }
        return null;
    }


    public static  Set<Card> getCardsByCategory(Category category) {
        Set<Card> cards = new HashSet<>();


        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("CardToCategory.allCardsOfCategory")
                    .setParameter("category", category)
                    .getResultStream().collect(Collectors.toSet());
            em.getTransaction().commit();
        } catch (final Exception e) {
            log.warn("Beim abrufen der Categories ist einer Fehler aufgetreten: " + e);
        }

        return cards;
    }


    public static Set<Category> getCategoriesToCard(Card card) {
        Set<Category> categories = Cache.getInstance().getCategories();

        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            categories = (Set<Category>) em.createNamedQuery("CardToCategory.allCategoriesOfCard")
                            .setParameter("card", card)
                                    .getResultStream().collect(Collectors.toList());
            em.getTransaction().commit();
        } catch (final Exception e) {
            log.warn("Beim abrufen der Categories ist einer Fehler aufgetreten: " + e);
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