package com.swp.Logic;

import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.*;
import com.swp.Persistence.*;
import com.swp.Persistence.Exporter.ExportFileType;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.swp.Validator.checkNotNullOrBlank;

@Slf4j
public class CardLogic extends BaseLogic<Card>
{
    /**
     * Konstruktor für eine CardLogic-Instanz.
     */
    public CardLogic() {
        super(CardRepository.getInstance());
    }

    private final CardRepository cardRepository = CardRepository.getInstance();
    private final TagRepository tagRepository = TagRepository.getInstance();

    private static CardLogic cardLogic;
    public static CardLogic getInstance() {
        if (cardLogic == null)
            cardLogic = new CardLogic();
        return cardLogic;
    }
    //OVERVIEW

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an das CardRepository weiter.
     * @param begin: Seitenauswahl Anfangswert
     * @param end: Seitenauswahl Endwert
     * @return anzuzeigende Karten
     */
    public List<Card> getCardsToShow(int begin, int end)
    {
        return execTransactional(() -> CardRepository.getInstance().getCardRange(begin, end));
    }

    /**
     * Wird verwendet bei einer Filterung nach einem bestimmten Tag. Prüft zunächst, dass der übergebene Tag nicht
     * null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param tagName: Der textuelle Tag, zu dem die Karten gesucht werden sollen
     * @return Set der Karten, die Tag enthalten
     * @throws NoResultException falls es keinen Tag, oder Karte mit entsprechendem Tag gibt.
     */
    public List<Card> getCardsByTag(String tagName) {
        checkNotNullOrBlank(tagName, "Tag",true);
        return execTransactional(() -> cardRepository.findCardsByTag(
                tagRepository.findTag(tagName)));
    }

    /**
     * Methode wird verwendet, um passende Karten für die angegebenen Suchwörter zu identifizieren. Prüft zunächst,
     * dass der übergebene Tag nicht null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param terms Suchwörter, die durch ein Leerzeichen voneinander getrennt sind
     * @return Set der Karten, die Suchwörter enthalten.
     */
    public List<Card> getCardsBySearchterms(String terms)
    {
        checkNotNullOrBlank(terms, "Suchbegriffe",true);
        return execTransactional(() -> cardRepository.findCardsContaining(terms));
    }

    /**
     * Wird aufgerufen, wenn eine spezifische Karte gelöscht werden soll. Gibt die Karte weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn die Aktion erfolgreich war.
     * @param card zu löschende Karte
     * @return wahr, wenn Karte gelöscht wurde
     */
    public boolean deleteCard(Card card)
    {
        if(card == null){
            throw new IllegalStateException("Karte existiert nicht");

        }
        execTransactional(() -> {
            CardRepository.getInstance().delete(card);
            return null; // Lambda braucht immer einen return
        });
        return true;
    }

    /**
     * Wird aufgerufen, wenn mehrere Karten gelöscht werden sollen. Gibt die Karten einzeln weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn alle Karten erfolgreich gelöscht wurden.
     * @param cards zu läschende Karten
     * @return wahr, wenn alle Karten erfolgreich gelöscht wurden
     */
    public boolean deleteCards(List<Card> cards)
    {
        boolean ret = true;
        for(Card c : cards)
        {
            if(!deleteCard(c))
                ret = false;
        }
        return ret;
    }



    //SINGLEVIEWCARDPAGE, CARDEDITPAGE

    /**
     * Wird verwendet, um einzelne Karteninformationen über ihre UUID abzurufen. Wird an das CardRepository weitergegeben.
     * @param uuid: UUID der abzurufenden Karte
     * @return Zugehörige Karte
     */
    public Card getCardByUUID(String uuid)
    {
        checkNotNullOrBlank(uuid, "UUID",true);
        return execTransactional(() -> cardRepository.getCardByUUID(uuid));
    }

    /**
     * Lädt alle Tags als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an das CardRepository.
     * @return Set mit bestehenden Tags.
     */
    public List<Tag> getTags()
    {
        return execTransactional(() -> tagRepository.getAll());
    }


    public List<Tag> getTagsToCard(Card card) {
        return execTransactional(() -> cardRepository.getTagsToCard(card));
    }

    /**
     * Wird aufgerufen, wenn eine neue Karte erstellt wird oder eine bestehende angepasst.
     * @param cardToChange Die Karte, die neu erstellt werden soll oder die Kopie der Karte mit den Änderungen
     * @param neu gibt an, ob es sich um eine komplett neue Karte handelt
     * @return
     */
    public boolean updateCardData(Card cardToChange, boolean neu) {
        cardToChange.setContent();
        return execTransactional(() -> {
            if (neu)
                CardRepository.getInstance().save(cardToChange);
            else
                CardRepository.getInstance().update(cardToChange);
            return true;
        });
    }


    public boolean setTagsToCard(Card card, List<Tag> tagNew) {
        boolean ret = true;
        List<Tag> tagOld = getTagsToCard(card); //check Old Tags to remove unused tags
        if(tagOld == null){
            if(!checkAndCreateTags(card,tagNew,tagOld))
                ret = false;
        }

        else if(tagOld.containsAll(tagNew) && tagOld.size() == tagNew.size()) //no changes
            return true;


        else if(tagOld.containsAll(tagNew) || tagOld.size() > tagNew.size()) { //es wurden nur tags gelöscht
            if(!checkAndRemoveTags(card,tagNew,tagOld))
                ret = false;
        }
        else if(tagNew.containsAll(tagOld)){
            if(!checkAndCreateTags(card,tagNew,tagOld))
               ret = false;
        }
        else { //neue und alte
            if(!checkAndCreateTags(card,tagNew,tagOld))
                ret = false;
            if(!checkAndRemoveTags(card,tagNew,tagOld))
                ret = false;
        }
        return ret;
    }

    private boolean checkAndRemoveTags(Card card, List<Tag> tagNew, List<Tag> tagOld) {
        boolean ret = true;
        for (Tag t : tagOld) {
            if (!tagNew.contains(t))
                // wirft im Fehlerfall eine Exception, return-value könnte void sein
                execTransactional(() -> {
                    CardToTag c2t = CardToTagRepository.findSpecificCardToTag(card, t);
                    CardToTagRepository.getInstance().delete(c2t);
                    return null;
                });
        }
        return ret;
    }


    private boolean checkAndCreateTags(Card card, List<Tag> tagNew, List<Tag> tagOld) {
        return execTransactional(() -> {
            boolean ret = true;
            for (Tag t : tagNew) {
                if (tagOld != null && tagOld.contains(t)) {
                    log.info("Tag {} bereits für Karte {} in CardToTag enthalten, kein erneutes Hinzufügen notwendig", t.getUuid(), card.getUuid());
                } else {
                    try {
                        Tag tag = tagRepository.findTag(t.getVal());
                        t = tag;
                        log.info("Kategorie mit Namen {} bereits in Datenbank enthalten", t.getVal());
                    } catch (NoResultException ex) {
                        TagRepository.getInstance().save(t);
                    }
                    log.info("Tag {} wird zu Karte {} hinzugefügt", t.getUuid(), card.getUuid());
                    cardRepository.createCardToTag(card, t);
                }
            }
            return ret;
        });
    }



    //ZUSATZ

    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die Exporter Klasse weitergereicht.
     * @param cards Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     * @return Exportierte Datei
     */
    public boolean exportCards(Card[] cards, ExportFileType filetype)
    {
        return (new Exporter(filetype)).export(cards);
        //TODO:Exceptions?
    }



    //private void changeCard() {}


    private void removeCardToTag(Card c, Tag t) {
        execTransactional(() -> {
            CardToTagRepository.getInstance().delete(
                    CardToTagRepository.findSpecificCardToTag(c,t));
            return null; // Lambda braucht einen return
        });
    }
}
