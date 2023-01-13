package com.swp.Persistence;
import com.swp.DataModel.*;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class CardRepository extends BaseRepository<Card>
{
    private CardRepository() {
        super(Card.class);
    }

    // Singleton
    private static CardRepository cardRepository = null;
    public static CardRepository getInstance()
    {
        if(cardRepository == null)
            cardRepository = new CardRepository();
        return cardRepository;
    }

    /**
     * Funktion um einen bestimmten Abschnitt der persistierten Objekte aus der Datenbank zu holen
     *
     * @param from     int: gibt an wie viele Zeilen des Ergebnisses übersprungen werden.
     * @param to       int: gibt an bis zu welcher Zeile die Ergebnisse geholt werden sollen.
     */
    public List<Card> getCardRange(final int from, final int to) {
        assert from <= to : "Ungültiger Bereich: `from` muss kleiner/gleich `to` sein";
        return getEntityManager().createQuery("SELECT c FROM Card c ORDER BY title", Card.class)
                .setFirstResult(from).setMaxResults(to-from).getResultList();
    }

    /**
     *
     * @return List<CardOverview> eine Übersicht
     */
    public static List<CardOverview> getCardOverview() {
        return getEntityManager().createQuery("SELECT c FROM CardOverview c", CardOverview.class).getResultList();
    }

    /**
     * Die Funktion `findCardsByCategory` such nach allen Karten die einer bestimmten Kategorie zugeordnet sind.
     *
     * @param category eine Category
     * @return List<Card> eine Liste von Karten, die der Kategorie zugeordnet sind.
     */
    public static List<Card> findCardsByCategory(Category category) {
        return getEntityManager().createNamedQuery("CardToCategory.allCardsOfCategory")
                .setParameter("category", category)
                .getResultList();
    }


    /**
     * Die Funktion `findCardsByTag` sucht nach Karten, der ein bestimmter Tag zugeordnet ist und gibt diese zurück.
     * @param tag ein Tag für den alle Karten gesucht werden sollen, die diesen haben.
     * @return Set<Card> eine Menge von Karten, welche in Verbindung zu dem Tag stehen.
     */
    public static List<Card> findCardsByTag(Tag tag) {
        return getEntityManager().createNamedQuery("CardToTag.allCardsWithTag")
                .setParameter("tag", tag).getResultList();
    }

    /**
     * Die Funktion `findCardsContaining` durchsucht den Inhalt aller Karten.
     * Es werden alle Karten zurückgegeben, die den übergebenen Suchtext als Teilstring enthalten.
     * @param searchWords ein String nach dem im Inhalt aller Karten gesucht werden soll.
     * @return Set<Card> eine Menge von Karten, welche `searchWords` als Teilstring im Inhalt hat.
     */
    public static List<Card> findCardsContaining(String searchWords) {
        return getEntityManager().createNamedQuery("Card.findCardsByContent")
                .setParameter("content", "%" + searchWords + "%")
                .getResultList();
    }

    /**
     * Die Funktion `getCardByUUID` sucht anhand einer UUID nach einer Karte und gibt diese zurück.
     * Existiert keine Karte mit angegebener UUID, dann
     * @param uuid eine UUID als String
     * @return eine Card mit entsprechender UUID, oder `null` falls keine gefunden wurde.
     */
    public static Card getCardByUUID(String uuid) {
        return (Card) getEntityManager().createNamedQuery("Card.findCardByUUID")
                .setParameter("uuid", uuid)
                .getSingleResult();
    }

    /**
     * Die Funktion `deleteCard` löscht eine Karte und alle Verbindungen, in der sie existiert hat.
     * Dazu zählen die Kategorien, Tags und Decks mit der die Karte eine Verbindung hatte.
     * @param card die Card die gelöscht werden soll
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall `false`.
     */
    public static boolean deleteCard(Card card) {
        getInstance().delete(card); // kann von der Logic auch direkt verwendet werden
        // wäre äquivalent zu folgendem
        // getEntityManager().remove(card);


        /*
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
         */
        return true; // boolean Rückgabewert macht eigentlich keinen Sinn mehr,
                     // weil bei Fehlern Exceptions geworfen werden.
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
        getEntityManager().persist(tag);
        return true;
    }

    /**
     * Die Funktion `getTags` liefer alle gespeicherten Tags zurück.
     * @return Set<Tag> eine Menge mit allen Tags
     */
    public static List<Tag> getTags() {
        return (List<Tag>) getEntityManager()
                .createQuery("SELECT t FROM Tag t")
                .getResultList();
    }

    /**
     * Die Funktion `getCardToTags` liefer alle `CardToTag`-Objekte zurück.
     * Sie stellen die Verbindungen zwischen Karten und Tags dar.
     * @return List<CardToTag> eine Menge mit allen `CardToTag`-Objekten.
     */
    public static List<CardToTag> getCardToTags() {
        return (List<CardToTag>) getEntityManager()
                .createQuery("SELECT CardToTag FROM CardToTag")
                .getResultList();
    }

    /**
     * Die Funktion `createCardToTag` erstellt eine neues `CardToTag`, welches eine Karte mit einem Tag in
     * Verbindung setzt und persistiert dieses in der Datenbank.
     * @param card eine Karte, der ein Tag zugeordnet werden soll
     * @param tag ein Tag, der der Karte zugeordnet werden soll
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean createCardToTag(Card card, Tag tag) {
        getEntityManager().persist(new CardToTag(card, tag));
        return true;
    }

    /**
     * Die Funktion `find` sucht nach einem Tag, der dem übergebenen Text entspricht.
     * Existiert kein Tag mit so einem Inhalt, dann wird ein leeres `Optional` zurückgegeben.
     * @param text ein String nach dem ein existierender Tag gesucht wird
     * @return Optional<Tag> entweder der gefundene Tag mit entsprechendem Namen, oder ein leeres `Optional`.
     * @throws NoResultException wenn kein entsprechender Tag gefunden wurde.
     */
    public static Tag findTag(String text) {
        return (Tag) getEntityManager().createNamedQuery("Tag.findTagByName")
                .setParameter("text", text)
                .getSingleResult();
    }

    /**
     * Die Funktion `getTagsToCard` liefer alle Tags zurück, die einer Karte zugeordnet sind.
     * @param card eine Karte
     * @return Set<Tag> eine Menge von Tags, die der Karte zugeordnet sind.
     */
    public static List<Tag> getTagsToCard(Card card) {
        return  (List<Tag>) getEntityManager()
                .createNamedQuery("CardToTag.allTagsWithCards")
                .setParameter("card", card)
                .getResultList();
    }

    public static CardToTag findSpecificCardToTag(Card c, Tag t) {
        return (CardToTag) getEntityManager().createNamedQuery("CardToTag.findSpecificC2T")
                .setParameter("card", c)
                .setParameter("tag", t)
                .getSingleResult();
    }

    public static boolean deleteCardToTag(Card c, Tag t) {
        getEntityManager().createNamedQuery("CardToTag.findSpecificC2T")
                .setParameter("card", c)
                .setParameter("tag", t)
                .getSingleResult();
            return true;
    }
}
