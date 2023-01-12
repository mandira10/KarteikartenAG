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

    //OVERVIEW

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an das CardRepository weiter.
     * @param begin: Seitenauswahl Anfangswert
     * @param end: Seitenauswahl Endwert
     * @return anzuzeigende Karten
     */
    public static List<Card> getCardsToShow(int begin, int end)
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
    public static List<Card> getCardsByTag(String tagName) {
        checkNotNullOrBlank(tagName, "Tag");
        return execTransactional(() -> CardRepository.findCardsByTag(
                CardRepository.findTag(tagName)));
    }

    public static Card getCardByTitle(String title) {
        // nicht Optional, sondern gibt immer eine Card zurück, oder wirft eine Exception
        // also im 'nicht findbar' Fall die NoResultException.
        return execTransactional(() -> CardRepository.findCardByTitle(title) );
    }

    /**
     * Methode wird verwendet, um passende Karten für die angegebenen Suchwörter zu identifizieren. Prüft zunächst,
     * dass der übergebene Tag nicht null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param terms Suchwörter, die durch ein Leerzeichen voneinander getrennt sind
     * @return Set der Karten, die Suchwörter enthalten.
     */
    public static List<Card> getCardsBySearchterms(String terms)
    {
        checkNotNullOrBlank(terms, "Suchbegriffe");
        return execTransactional(() -> CardRepository.findCardsContaining(terms));
    }

    /**
     * Wird aufgerufen, wenn eine spezifische Karte gelöscht werden soll. Gibt die Karte weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn die Aktion erfolgreich war.
     * @param card zu löschende Karte
     * @return wahr, wenn Karte gelöscht wurde
     */
    public static boolean deleteCard(Card card)
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
    public static boolean deleteCards(List<Card> cards)
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
    public static Card getCardByUUID(String uuid)
    {
        checkNotNullOrBlank(uuid, "UUID");
        return execTransactional(() -> CardRepository.getCardByUUID(uuid));
    }

    /**
     * Lädt alle Tags als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an das CardRepository.
     * @return Set mit bestehenden Tags.
     */
    public static List<Tag> getTags()
    {
        return execTransactional(CardRepository::getTags);
    }


    public static List<Tag> getTagsToCard(Card card) {
        return execTransactional(() -> CardRepository.getTagsToCard(card));
    }

    /**
     * Wird aufgerufen, wenn eine neue Karte erstellt wird oder eine bestehende angepasst.
     * @param cardToChange Die Karte die neu erstellt werden soll oder die Kopie der Karte mit den Änderungen
     * @param neu gibt an, ob es sich um eine komplett neue Karte handelt
     * @return
     */
    public static boolean updateCardData(Card cardToChange, boolean neu) {
        // muss eigentlich nicht `boolean` als Rückgabewert haben,
        // weil im Fehlerfall über `execTransactional` bereits eine Exception hochgeworfen wird.
        cardToChange.setContent();
        return execTransactional(() -> {
            if (neu)
                CardRepository.getInstance().save(cardToChange);
            else
                CardRepository.getInstance().update(cardToChange);
            return true;
        });
    }


    public static boolean setTagsToCard(Card card, Set<Tag> tagNew) {
        boolean ret = true;
        List<Tag> tagOld = getTagsToCard(card); //check Old Tags to remove unused tags
        if(tagOld == null){
            //if(!checkAndCreateTags(card,tagNew,tagOld))
            //    ret = false;
        }

        else if(tagOld.containsAll(tagNew) && tagOld.size() == tagNew.size()) //no changes
            return true;


        else if(tagOld.containsAll(tagNew) || tagOld.size() > tagNew.size()) { //es wurden nur tags gelöscht
            //if(!checkAndRemoveTags(card,tagNew,tagOld))
            //    ret = false;
        }
        else if(tagNew.containsAll(tagOld)){
            //if(!checkAndCreateTags(card,tagNew,tagOld))
            //   ret = false;
        }
        else { //neue und alte
            //if(!checkAndCreateTags(card,tagNew,tagOld))
            //    ret = false;
            //if(!checkAndRemoveTags(card,tagNew,tagOld))
            //    ret = false;
        }
        return ret;
    }

    private static boolean checkAndRemoveTags(Card card, Set<Tag> tagNew, Set<Tag> tagOld) {
        boolean ret = true;
        for (Tag t : tagOld) {
            if (!tagNew.contains(t))
                if (!CardRepository.deleteCardToTag(card, t))
                    ret = false;
        }
        return ret;
    }


    private static boolean checkAndCreateTags(Card card, Set<Tag> tagNew, Set<Tag> tagOld) {
        boolean ret = true;
        List<Tag> tagExi = CardRepository.getTags();
        for (Tag t : tagNew) {
            if (tagOld != null && tagOld.contains(t))
                log.info("Tag {} bereits für Karte {} in CardToTag enthalten, kein erneutes Hinzufügen notwendig", t.getUuid(), card.getUuid());
           else{
                if (tagExi == null || tagExi.stream().filter(tag -> tag.getUuid().equals(t.getUuid())).findFirst().isEmpty()){
                    if(!CardRepository.saveTag(t)){
                        ret = false;}
                }
                log.info("Tag {} wird zu Karte {} hinzugefügt", t.getUuid(), card.getUuid());
                if(!CardRepository.createCardToTag(card, t))
                ret = false;
        }
        }
        return ret;
    }



    //ZUSATZ

    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die Exporter Klasse weitergereicht.
     * @param cards Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     * @return Exportierte Datei
     */
    public static boolean exportCards(Card[] cards, ExportFileType filetype)
    {
        return (new Exporter(filetype)).export(cards);
        //TODO:Exceptions?
    }



    //KEEP FOR TEST REASONS AS FOR NOW
    /**
     * Teilmethode zum Erstellen/Aktualisieren von Karten. Wird über den Controller aufgerufen und prüft zunächst, ob eine
     * neue Karte erstellt werden muss. Das Setzen der Attribute erfolgt über die Methode updateCardContent.
     * @param card Karte zum Aktualisieren, wenn null, dann muss eine neue Karte erstellt werden
     * @param type Der Kartentyp als String von der GUI übergeben
     * @param attributes Die HashMap mit den Attributen
     * @param tags Zugehörige Tags als String Set für die Karte
     * @param categories Zugehörige Categories als String Set für die Karte
     * @return true, wenn erfolgreich erstellt
     */
    public static boolean updateCardData(Card card, String type, HashMap<String, Object> attributes, Set<Tag> tags, Set<Category> categories) {
        if (card == null) {
            switch (type) {
                case "TRUEFALSE":
                    TrueFalseCard cardTF = new TrueFalseCard();
                    return updateCardContent(cardTF, attributes, tags, categories, true);
                case "IMAGETEST":
                    ImageTestCard cardIT = new ImageTestCard();
                    return updateCardContent(cardIT, attributes, tags, categories, true);
                case "IMAGEDESC":
                    ImageDescriptionCard cardID = new ImageDescriptionCard();
                    return updateCardContent(cardID, attributes, tags, categories, true);
                case "MULTIPLECHOICE":
                    MultipleChoiceCard cardMC = new MultipleChoiceCard();
                    return updateCardContent(cardMC, attributes, tags, categories, true);
                case "TEXT":
                    TextCard cardT = new TextCard();
                    return updateCardContent(cardT, attributes, tags, categories, true);
                case "AUDIO":
                    AudioCard cardAu = new AudioCard();
                    return updateCardContent(cardAu, attributes, tags, categories, true);
                default:
                    return false;
            }
        } else {
            return updateCardContent(card, attributes, tags, categories, false);
        }
    }

    /**
     * Teilmethode zum Erstellen/Aktualisieren von Karten. Wird verwendet, um die einzelnen Attribute der neuen Karten oder bestehenden Karten anzupassen.
     * @param card Die zu bearbeitende Karte
     * @param attributes Attribute der Karte
     * @param tags Tags, die angepasst werden
     * @param categories Kategorien, die angepasst werden
     * @param neu wenn true, dann ist die Karte neu erstellt worden
     * @return ob erfolgreich
     */
    private static boolean updateCardContent(Card card, HashMap<String, Object> attributes, Set<Tag> tags, Set<Category> categories, boolean neu) {
        try {
            log.info("Versuche die Attribute der Karte {} zu aktualisieren", card.getUuid());
            BeanUtils.populate(card, attributes);
            card.setContent();
        } catch (IllegalAccessException | InvocationTargetException ex) {
            //TODO + weitere Exceptions, Prüfung mit Validator? in Constructor einnbauen
        }
        return execTransactional(() -> {
            if (neu) {
                CardRepository.getInstance().save(card);
            } else {
                CardRepository.getInstance().update(card);
            }
            createTagsCategoriesForCard(card, tags, categories);
            return true;
        });

        //TODO: Fehler genauer aufschlüsseln
    }

    private static boolean createTagsCategoriesForCard(Card card, Set<Tag> tags, Set<Category> categories) {
        if (tags != null) {
            log.info("Versuche übergebene Tag(s) der Karte {} zuzuordnen", card.getUuid());
            if(!setTagsToCard(card, tags))
                return false;
        }
        if (categories != null) {
            log.info("Versuche übergebene Kategorie(n) der Karte {} zuzuordnen", card.getUuid());
            if(!CategoryLogic.setCardToCategories(card, categories))
                return false;
        }
        return true;
    }


    //TODO: kann weg=?
    public static boolean createTag(Tag tag)
    {
        return CardRepository.saveTag(tag);
    }


    //private void changeCard() {}


    private static void removeCardToTag(Card c, Tag t) {
        CardRepository.deleteCardToTag(c,t);
        execTransactional(() -> {
            CardToTagRepository.getInstance().delete(
                    CardRepository.findSpecificCardToTag(c,t));
            return null; // Lambda braucht einen return
        });
    }
    }
