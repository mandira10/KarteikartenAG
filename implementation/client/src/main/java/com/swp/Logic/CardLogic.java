package com.swp.Logic;

import com.swp.DataModel.*;
import com.swp.GUI.Extras.ListOrder;
import com.swp.Persistence.*;
import com.swp.Persistence.Exporter.ExportFileType;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

import static com.swp.Validator.checkNotNullOrBlank;



/**
 * CardLogic Klasse, behandelt alle Card spezifischen Aufrufe.
 * Erbt von der BaseLogic.
 */
@Slf4j
public class CardLogic extends BaseLogic<Card>
{
    /**
     * Konstruktor für eine CardLogic-Instanz.
     */
    public CardLogic() {
        super(CardRepository.getInstance());
    }

    /**
     * Instanz von CardLogic
     */
    private static CardLogic cardLogic;

    /**
     * Hilfsmethode. Wird genutzt, damit nicht mehrere Instanzen von CardLogic entstehen.
     *
     * @return cardLogic Instanz, die benutzt werden kann.
     */
    public static CardLogic getInstance() {
        if (cardLogic == null)
            cardLogic = new CardLogic();
        return cardLogic;
    }

    /**
     * Benutze Repositories, auf die zugegriffen wird.
     */
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final TagRepository tagRepository = TagRepository.getInstance();
    private final CardToTagRepository cardToTagRepository = CardToTagRepository.getInstance();
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();


    /**
     * Wird verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an das CardRepository weiter.
     * @param begin: Seitenauswahl Anfangswert
     * @param end: Seitenauswahl Endwert
     * @return anzuzeigende Karten
     */
    public List<CardOverview> getCardOverview(int begin, int end) {
        return execTransactional(() -> cardRepository.getCardOverview(begin, end));
    }

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an das CardRepository weiter.
     *
     * @param begin Seitenauswahl Anfangswert
     * @param end: Seitenauswahl Endwert
     * @param order: Parameter der zum Sortieren verwendet werden soll
     * @param reverseOrder: Gibt Sortierung an
     * @return anzuzeigende Karten
     */

    public List<CardOverview> getCardOverview(int begin, int end, ListOrder.Order order, boolean reverseOrder) {
        return execTransactional(() -> {
            List<CardOverview> cards = new ArrayList<>();
            switch (order) {
                case ALPHABETICAL -> {
                    if (!reverseOrder)
                        cards = cardRepository.getCardOverview(begin, end, "c.titelToShow", "asc");
                    else
                        cards = cardRepository.getCardOverview(begin, end, "c.titelToShow", "desc");
                }
                case DATE -> {
                    if (!reverseOrder)
                        cards = cardRepository.getCardOverview(begin, end, "c.cardCreated", "asc");
                    else
                        cards = cardRepository.getCardOverview(begin, end, "c.cardCreated", "desc");
                }
                case NUM_DECKS -> {
                    if (!reverseOrder)
                        cards = cardRepository.getCardOverview(begin, end, "c.countDecks", "asc");
                    else
                        cards = cardRepository.getCardOverview(begin, end, "c.countDecks", "desc");
                }
            }
            return cards;
        });
    }

    /**
     * Wird verwendet bei einer Filterung nach einem bestimmten Tag. Prüft zunächst, dass der übergebene Tag nicht
     * null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param tagName: Der textuelle Tag, zu dem die Karten gesucht werden sollen
     * @return Set der Karten, die den Tag enthalten
     */
    public List<CardOverview> getCardsByTag(String tagName) {
        checkNotNullOrBlank(tagName);
        return execTransactional(() -> cardRepository.findCardsByTag(tagName));
    }

    /**
     * Methode wird verwendet, um passende Karten für die angegebenen Suchwörter zu identifizieren. Prüft zunächst,
     * dass der übergebene Tag nicht null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     *
     * @param terms Suchwörter, die durch ein Leerzeichen voneinander getrennt sind
     * @return Set der Karten, die Suchwörter enthalten.
     */
    public List<CardOverview> getCardsBySearchterms(String terms) {
        checkNotNullOrBlank(terms);
        return execTransactional(() -> cardRepository.findCardsContaining(terms));
    }

    public List<CardOverview> getCardsBySearchterms(String searchterm, ListOrder.Order order, boolean reverseOrder) {
        checkNotNullOrBlank(searchterm);
        return execTransactional(() -> {
            List<CardOverview> cards = new ArrayList<>();
            switch (order) {
                case ALPHABETICAL -> {
                    if (!reverseOrder)
                        cards = cardRepository.findCardsContaining(searchterm, "co.titelToShow", "asc");
                    else
                        cards = cardRepository.findCardsContaining(searchterm, "co.titelToShow", "desc");
                }
                case DATE -> {
                    if (!reverseOrder)
                        cards = cardRepository.findCardsContaining(searchterm, "co.cardCreated", "asc");
                    else
                        cards = cardRepository.findCardsContaining(searchterm, "co.cardCreated", "desc");
                }
                case NUM_DECKS -> {
                    if (!reverseOrder)
                        cards = cardRepository.findCardsContaining(searchterm, "co.countDecks", "asc");
                    else
                        cards = cardRepository.findCardsContaining(searchterm, "co.countDecks", "desc");
                }
            }
            return cards;
        });
    }

    public List<CardOverview> getCardsByTag(String tag, ListOrder.Order order, boolean reverseOrder) {
        checkNotNullOrBlank(tag);
        return execTransactional(() -> {
            List<CardOverview> cards = new ArrayList<>();
            switch (order) {
                case ALPHABETICAL -> {
                    if (!reverseOrder)
                        cards = cardRepository.findCardsByTag(tag, "co.titelToShow", "asc");
                    else
                        cards = cardRepository.findCardsByTag(tag, "co.titelToShow", "desc");
                }
                case DATE -> {
                    if (!reverseOrder)
                        cards = cardRepository.findCardsByTag(tag, "co.cardCreated", "asc");
                    else
                        cards = cardRepository.findCardsByTag(tag, "co.cardCreated", "desc");
                }
                case NUM_DECKS -> {
                    if (!reverseOrder)
                        cards = cardRepository.findCardsByTag(tag, "co.countDecks", "asc");
                    else
                        cards = cardRepository.findCardsByTag(tag, "co.countDecks", "desc");
                }
            }
            return cards;
        });
    }


    /**
     * Wird aufgerufen, wenn eine spezifische Karte gelöscht werden soll. Gibt die Karte weiter
     * an das CardRepository.
     * @param card zu löschende Karte
     */
    public void deleteCard(Card card)
    {
        if(card == null){
            throw new IllegalStateException("cardnullerror");
        }
        execTransactional(() -> {
            log.info("Lösche alle Card To Categories zur Karte");
            cardToCategoryRepository.delete(cardToCategoryRepository.getAllC2CForCard(card));
            log.info("Lösche alle Card To Boxes zur Karte");
            cardToBoxRepository.delete(cardToBoxRepository.getAllB2CForCard(card));
            log.info("Lösche alle Card To Tags zur Karte");
            cardToTagRepository.delete(cardToTagRepository.getAllC2TForCard(card));
            log.info("Lösche die Karte");
            cardRepository.delete(card);
            return null; // Lambda braucht immer einen return
        });
    }

    /**
     * Wird aufgerufen, wenn mehrere Karten gelöscht werden sollen. Gibt die Karten einzeln weiter
     * an das CardRepository.
     * @param cards zu löschende Karten
     */
    public void deleteCards(List<CardOverview> cards)
    {
        for(CardOverview c : cards){
           deleteCard(execTransactional(() -> cardRepository.getCardByUUID(c.getUUUID())));
        }
    }


    /**
     * Wird verwendet, um einzelne Karteninformationen über ihre UUID abzurufen. Wird an das CardRepository weitergegeben.
     * @param uuid: UUID der abzurufenden Karte
     * @return Zugehörige Karte
     */
    public Card getCardByUUID(String uuid)
    {
        checkNotNullOrBlank(uuid);
        return execTransactional(() -> cardRepository.getCardByUUID(uuid));
    }

    /**
     * Lädt alle Tags als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an das CardRepository.
     * @return Set mit bestehenden Tags.
     */
    public List<Tag> getTags()
    {
        return execTransactional(tagRepository::getAll);
    }


    /**
     * Wird verwendet, um Tags einer Karte zu bekommen. Wird an das TagRepository weitergegeben.
     * @param card: Karte, um Ihre Tags zu bekommen.
     * @return eine Liste von Tag
     */
    public List<Tag> getTagsToCard(Card card) {
        if (card == null)
            throw new IllegalStateException("cardnullerror");

        return execTransactional(() -> tagRepository.getTagsToCard(card));
    }

    /**
     * Wird aufgerufen, wenn eine neue Karte erstellt wird oder eine bestehende angepasst.
     * @param cardToChange Die Karte, die neu erstellt werden soll oder die Kopie der Karte mit den Änderungen
     * @param neu gibt an, ob es sich um eine komplett neue Karte handelt
     */
    public void updateCardData(Card cardToChange, boolean neu) {
        if(cardToChange == null){
            throw new IllegalStateException(("cardnullerror"));
        }
        cardToChange.setContent();
        execTransactional(() -> {
            if (neu)
                cardRepository.save(cardToChange);
            else
                cardRepository.update(cardToChange);
            return true;
        });
    }


    /**
     * Wird verwendet, um die Tags einer Karte zu ändern.
     * @param card: Karte, um Ihre Tags zu ändern.
     * @param tagNew: die Liste von Tags
     */
    public void setTagsToCard(Card card, List<Tag> tagNew) {
        if(card == null){
            throw new IllegalStateException("cardnullerror");
        }
            List<Tag> tagOld = getTagsToCard(card); //check Old Tags to remove unused tags
            if (tagOld.isEmpty()) {
                checkAndCreateTags(card, tagNew, tagOld);
            } else if (tagOld.containsAll(tagNew)) {
                if (tagOld.size() == tagNew.size()) {
                    //nothing to do
                } else { //nur Löschen
                    checkAndRemoveTags(card, tagNew, tagOld);
                }
            } else if (tagNew.containsAll(tagOld)) {  // nur neue hinzufügen
                checkAndCreateTags(card, tagNew, tagOld);
            } else { //neue und alte
                checkAndCreateTags(card, tagNew, tagOld);
                checkAndRemoveTags(card, tagNew, tagOld);
            }

    }

    /* Mit `cardRepository.update(card.setAssignedTags(List<Tag>))` kann man eine angepasste Karte
     * persistieren. Die Relation zu den Tags wird kaskadiert.
     */
    /**
     * Wird verwendet, Um zu überprüfen, ob die angegebene Liste von Tags bereits Tags für die Karte sind oder nicht. Wird an das CardToTagRepository weitergegeben.
     * @param card: die Karte, um ihre Tags zu überprüfen
     * @param tagOld: die Liste von Tags der Karte
     * @param tagNew: die angegebene Liste von Tags zu vergleichen
     */
    private void checkAndRemoveTags(Card card, List<Tag> tagNew, List<Tag> tagOld) {
        for (Tag t : tagOld) {
            if (!tagNew.contains(t))
                    removeCardToTag(card,t);
        }
    }

    /**
     * Wird verwendet, um einen spezifischen CardToTag zu löschen. Wird ans cardToTagRepo weitegegeben.
     * @param c Karte für den CardToTag
     * @param t Tag für den CardToTag
     */
    private void removeCardToTag(Card c, Tag t) {
        execTransactional(() -> {
            cardToTagRepository.delete(
                    cardToTagRepository.findSpecificCardToTag(c, t));
            return null; // Lambda braucht einen return
        });
    }


    /**
     * Wird verwendet, um zu überprüfen, ob die angegebene Liste von Tags bereits Tags für die Karte sind oder nicht.
     * Wenn nicht, wird diese Liste von Tags in der Karte hinzufügen. Wird an das CardToTagRepository weitergegeben.
     * @param card: die Karte, um ihre Tags zu überprüfen
     * @param tagOld: die Liste von Tags der Karte
     * @param tagNew: die angegebene Liste von Tags zu vergleichen und hinzufügen
     */
    private void checkAndCreateTags(Card card, List<Tag> tagNew, List<Tag> tagOld) {
        execTransactional(() -> {
            for (Tag t : tagNew) {
                if (!tagOld.isEmpty() && tagOld.contains(t)) {
                    log.info("Tag {} bereits für Karte {} in CardToTag enthalten, kein erneutes Hinzufügen notwendig", t.getVal(), card.getUuid());
                } else {
                    checkNotNullOrBlank(t.getVal());
                    try {
                        t = tagRepository.findTag(t.getVal());
                        log.info("Tag mit Namen {} bereits in Datenbank enthalten", t.getVal());
                    } catch (NoResultException ex) {
                        t = tagRepository.save(t);
                    }
                        log.info("Tag {} wird zu Karte {} hinzugefügt", t.getVal(), card.getUuid());
                        cardToTagRepository.createCardToTag(card, t);
                    }
                }
            return null;
            });

    }

    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die Exporter Klasse weitergereicht.
     * @param cards Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     */
    public boolean exportCards(List<CardOverview> cards, String destination, ExportFileType filetype) 
    {
        List<Card> cardlist = new ArrayList<>();
        for(CardOverview ov : cards)
            cardlist.add(getCardByUUID(ov.getUUUID()));
        
        if(!new Exporter(filetype).export(cardlist, destination))
        {
            throw new RuntimeException("Failed to export cards");
        }

        return true;
    }

}
