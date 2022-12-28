package com.swp.Persistence;

import com.gumse.textures.Texture;
import com.gumse.tools.Debug;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CardRepository
{
    private static final PersistenceManager pm = new PersistenceManager();
    public static Set<Card> getCards(long from, long to)
    {
        //////////////////////////////////////
        // For Testing 
        //////////////////////////////////////
        Set<Card> cards = new HashSet<>();
        Texture ketTexture = new Texture("ket");
        ketTexture.load("textures/orange-ket.png", CardRepository.class);
        for(int i = 0; i < to - from; i += 6)
        {
            cards.add(new AudioCard(null, "AudioCardTitle", "Some Audio Related Question", "The Correct Audio Answer", false, true));
            cards.add(new ImageDescriptionCard("Some Image Description Question", "Correct Image Description Answer", "ImageDescriptionTitle", ketTexture, false));
            cards.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", ketTexture, "ImageTestCardTitle", false, true));
            cards.add(new MultipleChoiceCard("MultipleChoice Question", new String[]{"Correct Answer1", "Answer2", "Answer3", "Correct Answer4"}, new int[]{0, 3}, "MultipleChoiceCardTitle", false));
            cards.add(new TextCard("Some Text Question", "Correct Text Answer", "TextCardTitle", false));
            cards.add(new TrueFalseCard("TrueFalse Question", true, "TrueFalseCardTitle", false));
        }

        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createQuery("SELECT Card FROM Card").getResultStream().collect(Collectors.toSet());
            em.getTransaction().commit();
            return cards;
        } catch (final Exception e) {
            // wie soll die Fehlermeldung zur GUI gelangen?
            log.warn("Beim Abrufen aller Karten ist einer Fehler aufgetreten: " + e);
        }

        return cards;

        //////////////////////////////////////
        // For Implemenation we need a view because we need card and cardtodeck
        //////////////////////////////////////
        /*
        SELECT * FROM CARDS WHERE...
        LEFT JOIN CARDTODECK ON ...
         */
    }

    public static Set<Card> findCardsByCategory(Category category)
    {
        Set<Card> cards = null;
        log.info(String.format("Rufe alle Karten für Kategorie %s ab", category.getSName()));
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("CardToCategory.allCardsOfCategory")
                    .setParameter("category", category)
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        } catch (final NoResultException e) {
            log.info(String.format("Keine Karten mit der Kategorie \"%s\" gefunden"), category.getSName());
        } catch (final Exception e) {
            log.warn(String.format("Beim Suchen nach Karten mit der Kategorie \"%s\" ist eine Fehler aufgetreten: \"%s\"",
                    category.getSName(), e));
        }

        return cards;
    }

    public static Set<Card> findCardsByTag(Tag tag)
    {
        Set<Card> cards = null;
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("CardToTag.allCardsWithTag")
                    .setParameter("tag", tag)
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        } catch (final NoResultException e) {
        } catch (final Exception e) {
        }

        log.info("Rufe alle Karten für Tag " + tag.getSValue() + " ab");
        return cards;
    }

    public static Set<Card> findCardsContaining(String searchWords)
    {
        Set<Card> cards = null;
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("Card.findCardsByContent")
                    .setParameter("content", searchWords)
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        } catch (final NoResultException e) {
            // keine Karten mit entsprechendem Inhalt gefunden
            log.info(String.format("Keine Karten mit dem Inhalt \"%s\" gefunden"), searchWords);
        } catch (final Exception e) {
            log.warn(String.format("Beim Suchen nach Karten mit Inhalt \"%s\" ist ein Fehler aufgetreten: \"%s\"",
                    searchWords, e));
        }

        log.info("Rule alle Karten mit folgendem Inhalt ab: " + searchWords);
        return cards;
    }

    public static Card getCardByUUID(String uuid)
    {
        Card card = null;
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.createNamedQuery("Card.findCardByUUID")
                .setParameter("uuid", uuid)
                .getSingleResult();
            em.getTransaction().commit();
        } catch (final NoResultException e) {
            // Keine Karte mit entsprechender UUID gefunden
        } catch (final Exception e) {
            // andere Fehler auch über GUI an User melden
        }

        log.info();
        return card;
    }

    /**
     *
     * @param oldcard
     * @param newcard
     */
    public static boolean updateCard(Card oldcard, Card newcard)
    {
        //TODO: update Methode Hibernate? Kein Server Handling mehr
//        String jsonString = "";
//        if(!oldcard.getTitle().equals(newcard.getTitle()))
//            jsonString += "\"title\":" + newcard.getTitle();

        switch(newcard.getIType())
        {
            case AUDIO:
                break;
            case IMAGEDESC:
                break;
            case IMAGETEST:
                break;
            case MULITPLECHOICE:
                break;
            case TEXT:
                break;
            case TRUEFALSE:
                break;
            default:
                Debug.error("Unknown cardtype!");
                break;

        }


        return false;
    }

    public static boolean saveCard(Card card)
    {
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            return false;
        }
        return true;
        //TODO: create all CardTo..
    }

    public static boolean deleteCard(Card card)
    {
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.remove(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            return false;
        }
        return true;
        //TODO: Delete Card and all references for card: cardTo...
    }




    //
    // Tags
    //
    public static boolean saveTag(String value)
    {
        // prüfen, ob Tag bereits existiert.
        // falls ja -> return true
        // falls nein -> neuen Tag persisten
        return true;
    }

    public static Set<Tag> getTags()
    {
        Set<Tag> tags = Cache.getInstance().getTags();
        if(!tags.isEmpty())
            return tags;

        return null;
    }

    public static Set<CardToTag> getCardToTags()
    {
        Set<CardToTag> tags = Cache.getInstance().getCardToTags();
        if(!tags.isEmpty())
            return tags;

        return null;
    }

    public static boolean createCardToTag(Card card, Tag tag) {
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(new CardToTag(card, tag));
            em.getTransaction().commit();
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    public static boolean updateCard(Card card) {
        return false;
    }

    public static Optional<Tag> find(String name) {
        Optional<Tag> tag = Optional.empty();
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            tag = (Optional<Tag>) em.createNamedQuery("Tag.findTagByName")
                    .setParameter("text", name)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (final NoResultException e) {
            tag = Optional.empty();
        } catch (final Exception e) {

        }

        return tag;
    }
}
