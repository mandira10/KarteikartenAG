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
import jakarta.persistence.EntityManagerFactory;
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
    private final static EntityManagerFactory emf = PersistenceManager.emFactory;

    /**
     * Die Funktion `getCards` gibt über ein `DataCallback` Karten aus einem bestimmten Bereich zurück.
     * @param from ein `long`, untere Schranke des angefragten Kartenbereichs
     * @param to ein `long`, obere Schranke des angefragten Kartenbereichs
     * @param callback DataCallBack<Card> das Objekt über welches die geforderten Karten zurückgegeben wird.
     */
    public static void getCards(long from, long to, DataCallback<Card> callback) {
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
            cards.add(new ImageDescriptionCard("Some Image Description Question", new ImageDescriptionCardAnswer[] {}, "ImageDescriptionTitle", "textures/orange-ket.png", false));
            cards.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", "textures/orange-ket.png", "ImageTestCardTitle", false, true));
            cards.add(new MultipleChoiceCard("MultipleChoice Question", new String[]{"Correct Answer1", "Answer2", "Answer3", "Correct Answer4"}, new int[]{0, 3}, "MultipleChoiceCardTitle", false));
            cards.add(new TextCard("Some Text Question", "Correct Text Answer", "TextCardTitle", false));
            cards.add(new TrueFalseCard("TrueFalse Question", true, "TrueFalseCardTitle", false));
        }
        callback.onSuccess(cards); //temporary

        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            cards = (List<Card>) em.createQuery("SELECT Card FROM Card").getResultStream().collect(Collectors.toList());
            em.getTransaction().commit();
            callback.onSuccess(cards);
        } catch (final Exception e) {
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

    /**
     * Die Funktion `findCardsByCategory` such nach allen Karten die einer bestimmten Kategorie zugeordnet sind.
     * @param category eine Category
     * @return Set<Card> eine Menge von Karten, die der Kategorie zugeordnet sind.
     */
    public static Set<Card> findCardsByCategory(Category category) {
        Set<Card> cards = null;
        log.info(String.format("Rufe alle Karten für Kategorie %s ab", category.getName()));
        try (final EntityManager em = emf.createEntityManager()) {
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

    /**
     * Die Funktion `findCardByTitle` durchsucht den Titel aller Karten nach einem String.
     * Es optional die entsprechende Card zurückgegeben, oder ein leeres `Optional`.
     * @param title ein String, der dem Titel einer Karte entsprechen soll.
     * @return Optional<Card> eine Karte, falls keine mit angegebenen Titel existiert, empty.
     */
    public static Optional<Card> findCardByTitle(String title) {
        log.info(String.format("Rufe Karte für Titel %s ab", title));
        try (final EntityManager em = emf.createEntityManager()) {
            final Card card = (Card) em.createNamedQuery("Card.findByTitle")
                    .setParameter("title", title)
                    .getSingleResult();
            return Optional.of(card);
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Die Funktion `findCardsByTag` sucht nach Karten, der ein bestimmter Tag zugeordnet ist und gibt diese zurück.
     * @param tag ein Tag für den alle Karten gesucht werden sollen, die diesen haben.
     * @return Set<Card> eine Menge von Karten, welche in Verbindung zu dem Tag stehen.
     */
    public static Set<Card> findCardsByTag(Tag tag) {
        Set<Card> cards;
        log.info("Rufe alle Karten für Tag " + tag.getVal() + " ab");
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("CardToTag.allCardsWithTag")
                    .setParameter("tag", tag)
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        }
        return cards;
    }

    /**
     * Die Funktion `findCardsContaining` durchsucht den Inhalt aller Karten.
     * Es werden alle Karten zurückgegeben, die den übergebenen Suchtext als Teilstring enthalten.
     * @param searchWords ein String nach dem im Inhalt aller Karten gesucht werden soll.
     * @return Set<Card> eine Menge von Karten, welche `searchWords` als Teilstring im Inhalt hat.
     */
    public static Set<Card> findCardsContaining(String searchWords) {
        Set<Card> cards;
        log.info("Rufe alle Karten mit folgendem Inhalt ab: " + searchWords);
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            cards = (Set<Card>) em.createNamedQuery("Card.findCardsByContent")
                    .setParameter("content", "%" + searchWords + "%")
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        }
        return cards;
    }

    /**
     * Die Funktion `getCardByUUID` sucht anhand einer UUID nach einer Karte und gibt diese zurück.
     * Existiert keine Karte mit angegebener UUID, dann
     * @param uuid eine UUID als String
     * @return eine Card mit entsprechender UUID, oder `null` falls keine gefunden wurde.
     */
    public static Card getCardByUUID(String uuid) {
        Card card = null;
        log.info("Suche Karte mit der UUID: " + uuid);
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            card = (Card) em.createNamedQuery("Card.findCardByUUID")
                .setParameter("uuid", uuid)
                .getSingleResult();
            em.getTransaction().commit();
        } catch (final NoResultException e) {
            // Keine Karte mit entsprechender UUID gefunden
            log.info(String.format("Keine Karte mit der UUID \"%s\" gefunden"), uuid);
        } catch (final Exception e) {
            // andere Fehler auch über GUI an KarteikartenUser melden
            log.warn(String.format("Beim Suchen nach einer Karte mit der UUID \"%s\" ist einer Fehler aufgetreten: \"%s\"",
                                   uuid, e));
        }
        return card;
    }

    /**
     * Die Funktion `saveCard` speichert eine übergebene Karte in der Datenbank.
     * @param card eine Card die persistiert werden soll
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall `false`.
     */
    public static boolean saveCard(Card card) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            log.warn(String.format("Karte \"%s\" konnte nicht gespeichert werden", card.getUuid()));
            return false;
        }
        log.info(String.format("Karte \"%s\" wurde erfolgreich gespeichert", card.getUuid()));
        return true;
        //TODO: create all CardTo..
    }

    /**
     * Die Funktion `deleteCard` löscht eine Karte und alle Verbindungen, in der sie existiert hat.
     * Dazu zählen die Kategorien, Tags und Decks mit der die Karte eine Verbindung hatte.
     * @param card die Card die gelöscht werden soll
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall `false`.
     */
    public static boolean deleteCard(Card card) {
        try (final EntityManager em = emf.createEntityManager()) {
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
    /**
     * Die Funktion `saveTag` speichert einen übergebenen Tag in der Datenbank
     * @param tag ein Tag der persistiert werden soll
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean saveTag(Tag tag) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(tag);
            em.getTransaction().commit();
        }
        log.info(String.format("Tag \"%s\" wurde erfolgreich gespeichert", tag.getUuid()));
        return true;
    }

    /**
     * Die Funktion `getTags` liefer alle gespeicherten Tags zurück.
     * @return Set<Tag> eine Menge mit allen Tags
     */
    public static Set<Tag> getTags() {
        Set<Tag> tags = Cache.getInstance().getTags();
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            tags = (Set<Tag>) em.createQuery("SELECT t FROM Tag t").getResultStream().collect(Collectors.toSet());
            em.getTransaction().commit();
        } catch (final Exception e) {
            log.warn("Beim abrufen der Tags ist einer Fehler aufgetreten: " + e);
        }
        return tags.isEmpty() ? null : tags;
    }

    /**
     * Die Funktion `getCardToTags` liefer alle `CardToTag`-Objekte zurück.
     * Sie stellen die Verbindungen zwischen Karten und Tags dar.
     * @return Set<CardToTag> eine Menge mit allen `CardToTag`-Objekten.
     */
    public static Set<CardToTag> getCardToTags() {
        Set<CardToTag> cardToTags;
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            cardToTags = (Set<CardToTag>) em.createQuery("SELECT CardToTag FROM CardToTag")
                    .getResultStream()
                    .collect(Collectors.toSet());
            em.getTransaction().commit();
        }
        if(!cardToTags.isEmpty()) {
            return cardToTags;
        }
        return null;
    }

    /**
     * Die Funktion `createCardToTag` erstellt eine neues `CardToTag`, welches eine Karte mit einem Tag in
     * Verbindung setzt und persistiert dieses in der Datenbank.
     * @param card eine Karte, der ein Tag zugeordnet werden soll
     * @param tag ein Tag, der der Karte zugeordnet werden soll
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean createCardToTag(Card card, Tag tag) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(new CardToTag(card, tag));
            em.getTransaction().commit();
       }
        log.info(String.format("Verbindung zwischen der Karte \"%s\" und dem Tag \"%s\" hergestellt",
                               card.getUuid(), tag.getVal()));
        return true;
    }

    /**
     * Die Funktion `updateCard` persistiert eine übergebene Karte, indem die Einträge in
     * der Datenbank mit einem `merge` aktualisiert werden.
     * @param card eine Karte
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean updateCard(Card card) {
        try (final EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(card);
            em.getTransaction().commit();
            return true;
        }
    }

    /**
     * Die Funktion `find` sucht nach einem Tag, der dem übergebenen Text entspricht.
     * Existiert kein Tag mit so einem Inhalt, dann wird ein leeres `Optional` zurückgegeben.
     * @param text ein String nach dem ein existierender Tag gesucht wird
     * @return Optional<Tag> entweder der gefundene Tag mit entsprechendem Namen, oder ein leeres `Optional`.
     */
    public static Optional<Tag> find(String text) {
        log.info("Suche nach Tag mit Namen {}",text);
        try (final EntityManager em = emf.createEntityManager()) {
            final Tag tag = (Tag) em.createNamedQuery("Tag.findTagByName")
                    .setParameter("text", text)
                    .getSingleResult();
            return Optional.ofNullable(tag);
        }
        catch(NoResultException ex){
            return Optional.empty();
        }
    }

    /**
     * Die Funktion `getTagsToCard` liefer alle Tags zurück, die einer Karte zugeordnet sind.
     * @param card eine Karte
     * @return Set<Tag> eine Menge von Tags, die der Karte zugeordnet sind.
     */
    public static Set<Tag> getTagsToCard(Card card) {
        Set<Tag> tags = new HashSet<>();
        log.info("Rufe alle Tags für Card " + card.getUuid() + " ab");
        try (final EntityManager em = emf.createEntityManager()) {
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
