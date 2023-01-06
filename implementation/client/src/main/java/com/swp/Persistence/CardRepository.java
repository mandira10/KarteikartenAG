package com.swp.Persistence;

import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CardRepository
{
    private static final PersistenceManager pm = new PersistenceManager();
    public static void getCards(long from, long to, DataCallback<Card> callback)
    {
        //
        //Kann jetzt in einen thread verpackt werden
        //

        //////////////////////////////////////
        // For Testing 
        //////////////////////////////////////
        List<Card> cards = new ArrayList<>();
       // Texture ketTexture = new Texture("ket");
       // ketTexture.load("textures/orange-ket.png", CardRepository.class); //TODO: byte[] nutzen
        for(int i = 0; i < to - from; i += 6)
        {
            cards.add(new AudioCard(null, "AudioCardTitle", "Some Audio Related Question", "The Correct Audio Answer", false, true));
            cards.add(new ImageDescriptionCard("Some Image Description Question", new ImageDescriptionCardAnswer[] {}, "ImageDescriptionTitle", null, false));
            cards.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", null, "ImageTestCardTitle", false, true));
            cards.add(new MultipleChoiceCard("MultipleChoice Question", new String[]{"Correct Answer1", "Answer2", "Answer3", "Correct Answer4"}, new int[]{0, 3}, "MultipleChoiceCardTitle", false));
            cards.add(new TextCard("Some Text Question", "Correct Text Answer", "TextCardTitle", false));
            cards.add(new TrueFalseCard("TrueFalse Question", true, "TrueFalseCardTitle", false));
        }
        callback.onSuccess(cards); //temporary

        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (List<Card>) em.createQuery("SELECT Card FROM Card").getResultStream().collect(Collectors.toList());
            em.getTransaction().commit();
            callback.onSuccess(cards);
        } 
        catch (final Exception e) 
        {
            // wie soll die Fehlermeldung zur GUI gelangen?
            callback.onFailure("Beim Abrufen aller Karten ist einer Fehler aufgetreten: " + e);
        }

        //////////////////////////////////////
        // For Implementation we need a view because we need card and cardtodeck
        //////////////////////////////////////
        /*
           EntityManager em = pm.getEntityManager();
          em.getTransaction().begin();
          List <CardOverview> co = em.createQuery("SELECT c FROM CardOverview c", CardOverview.class).getResultList();
          em.getTransaction().commit();
          em.close();
          }
         */
    }

    public static Set<Card> findCardsByCategory(Category category)
    {
        Set<Card> cards = null;
        log.info(String.format("Rufe alle Karten für Kategorie %s ab", category.getName()));
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("CardToCategory.allCardsOfCategory")
                    .setParameter("category", category)
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        } catch (final NoResultException e) {
            log.info(String.format("Keine Karten mit der Kategorie \"%s\" gefunden"), category.getName());
        } catch (final Exception e) {
            log.warn(String.format("Beim Suchen nach Karten mit der Kategorie \"%s\" ist eine Fehler aufgetreten: \"%s\"",
                    category.getName(), e));
        }

        return cards;
    }

    public static Optional<Card> findCardByTitle(String title) {
        log.info(String.format("Rufe Karte für Titel %s ab", title));
        try (final EntityManager em = pm.getEntityManager()) {
            final Card card = (Card) em.createNamedQuery("Card.findByTitle")
                    .setParameter("title", title)
                    .getSingleResult();
            return Optional.of(card);
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    public static Set<Card> findCardsByTag(Tag tag)
    {
        Set<Card> cards = new HashSet<>();
        log.info("Rufe alle Karten für Tag " + tag.getVal() + " ab");
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("CardToTag.allCardsWithTag")
                    .setParameter("tag", tag)
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        }
        return cards;
    }

    public static Set<Card> findCardsContaining(String searchWords)
    {
        Set<Card> cards = new HashSet<>();
        log.info("Rufe alle Karten mit folgendem Inhalt ab: " + searchWords);
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("Card.findCardsByContent")
                    .setParameter("content", "%" + searchWords + "%")
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        }
        return cards;
    }

    public static Card getCardByUUID(String uuid)
    {
        Card card = null;
        log.info("Suche Karte mit der UUID: " + uuid);
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.createNamedQuery("Card.findCardByUUID")
                .setParameter("uuid", uuid)
                .getSingleResult();
            em.getTransaction().commit();
        } catch (final NoResultException e) {
            // Keine Karte mit entsprechender UUID gefunden
            log.info(String.format("Keine Karte mit der UUID \"%s\" gefunden"), uuid);
        } catch (final Exception e) {
            // andere Fehler auch über GUI an KarteikartenUser melden
            log.warn(String.format("Beim Suchen nach einer Karte mit der UUID \"%s\" ist einer Fehler aufgetreten: \"%s\""),
                    uuid, e);
        }

        return card;
    }




    public static boolean saveCard(Card card)
    {

        try{
        final EntityManager em = pm.getEntityManager();
        em.getTransaction().begin();
        em.persist(card);
        em.getTransaction().commit();
         }
        catch (Exception e) {
            log.warn(String.format("Karte \"%s\" konnte nicht gespeichert werden", card.getUuid()));
            return false;
        }
        log.info(String.format("Karte \"%s\" wurde erfolgreich gespeichert", card.getUuid()));
        return true;
        //TODO: create all CardTo..
    }

    public static boolean deleteCard(Card card)
    {
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.remove(card);
            // remove matching `CardToCategory`
            em.createNamedQuery("CardToCategory.allC2CByCard")
                    .setParameter("card", card)
                    .getResultStream()
                    .forEach(c2c -> em.remove(c2c));
            // remove matching `CardToTag`
            em.createNamedQuery("CardToTag.allC2TByCard")
                    .setParameter("card", card)
                    .getResultStream()
                    .forEach(c2t -> em.remove(c2t));
            // remove matching `CardToDeck`
            em.createNamedQuery("CardToDeck.allC2DByCard")
                        .setParameter("card", card)
                        .getResultStream()
                        .forEach(c2d -> em.remove(c2d));
            em.getTransaction().commit();
        } catch (Exception e) {
            log.warn(String.format("Karte \"%s\" konnte nicht gelöscht werden", card.getUuid()));
            return false;
        }
        log.info(String.format("Karte \"%s\" wurde erfolgreich gelöscht", card.getUuid()));
        return true;
    }




    //
    // Tags
    //
    public static boolean saveTag(Tag tag)
    {
       // try{
            final EntityManager em = pm.getEntityManager();
            em.getTransaction().begin();
            em.persist(tag);
            em.getTransaction().commit();
       // }
        //catch (Exception e) {
        //    log.warn(String.format("Karte \"%s\" konnte nicht gespeichert werden", tag.getUuid()));
         //   return false;
        //}
        log.info(String.format("Tag \"%s\" wurde erfolgreich gespeichert", tag.getUuid()));
        return true;
    }

    public static Set<Tag> getTags()
    {
        Set<Tag> tags = Cache.getInstance().getTags();

        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            tags = (Set<Tag>) em.createQuery("SELECT t FROM Tag t").getResultStream().collect(Collectors.toSet());
            em.getTransaction().commit();
        } catch (final Exception e) {
            log.warn("Beim abrufen der Tags ist einer Fehler aufgetreten: " + e);
        }

        if (!tags.isEmpty())
            return tags;

        return null;
    }

    public static Set<CardToTag> getCardToTags()
    {
        Set<CardToTag> cardToTags = Cache.getInstance().getCardToTags();

        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cardToTags = (Set<CardToTag>) em.createQuery("SELECT CardToTag FROM CardToTag")
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        }

        if(!cardToTags.isEmpty())
            return cardToTags;

        return null;
    }

    public static boolean createCardToTag(Card card, Tag tag) {
        try (
                final EntityManager em = pm.getEntityManager();) {
            em.getTransaction().begin();
            em.persist(new CardToTag(card, tag));
            em.getTransaction().commit();
       } //catch (final Exception e) {
       //     log.warn(String.format("Beim Speichern von `CardToTag` zwischen Karte \"%s\" und Tag \"%s\" ist ein Fehler aufgetreten: %s",
        //            card.getUuid(), tag.getVal(), e));
//
//        }
        log.info(String.format("Verbindung zwischen der Karte \"%s\" und dem Tag \"%s\" hergestellt",
                card.getUuid(), tag.getVal()));
        return true;
    }

    public static boolean updateCard(Card card) {
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.merge(card);
            em.getTransaction().commit();
            return true;
        }
    }

    public static Optional<Tag> find(String text) {
        log.info("Suche nach Tag mit Namen {}",text);
        try (final EntityManager em = pm.getEntityManager()) {
            final Tag tag = (Tag) em.createNamedQuery("Tag.findTagByName")
                    .setParameter("text", text)
                    .getSingleResult();
            return Optional.ofNullable(tag);
        }
        catch(NoResultException ex){
            return Optional.empty();
        }
    }


    public static Set<Tag> getTagsToCard(Card card) {
        Set<Tag> tags = new HashSet<>();
        log.info("Rufe alle Tags für Card " + card.getUuid() + " ab");
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            tags = (Set<Tag>) em.createNamedQuery("CardToTag.allTagsWithCards")
                    .setParameter("card", card)
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        }

        return tags;
    }
}
