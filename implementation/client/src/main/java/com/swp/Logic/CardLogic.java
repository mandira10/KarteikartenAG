package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.Tag;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.Exporter;
import com.swp.Persistence.Exporter.ExportFileType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
@Slf4j
public class CardLogic
{
    public static Set<Card> getCardsByTag(Tag tag)
	{
        return CardRepository.findCardsByTag(tag);

    }

    public static Card getCardByUUID(String uuid)
    {
        if(uuid == null || uuid.length() == 0)
            return null;

        return CardRepository.getCardByUUID(uuid);
    }

    /**
     * Methode wird verwendet, um passende Karten für die angegebenen Suchwörter zu identifizieren.
     * @param terms Suchwörter, die durch ein Leerzeichen voneinander getrennt sind
     * @return Set der Karten, die Suchwörter enthalten.
     */
    public static Set<Card> getCardsBySearchterms(String terms)
	{
        return CardRepository.findCardsContaining(terms);
    }

    public static Set<Card> getCardsToShow(long begin, long end){
        return CardRepository.getCards(begin, end);
    }


    public static boolean createCardToTag(Card card, Tag category)
    {
        return CardRepository.createCardToTag(card, category);
    }

    public static boolean createTag(String value)
    {
        return CardRepository.saveTag(value);
    }

    public static Set<Tag> getTags()
    {
        return CardRepository.getTags();
    }
    //TODO: was ist mit Cache

    //private void changeCard() {}

    
    /**
     * Aktualisiert die Datenbankeinträge der Karte
     * @param oldcard Alte Karte
     * @param newcard Neue Karte
     */
    public static boolean updateCardData(Card oldcard, Card newcard)
    {
        //TODO: Logik ändern?
        if(newcard.getUUID().isEmpty())
            return CardRepository.saveCard(newcard);
        else
            return CardRepository.updateCard(oldcard, newcard);

    }

    /**
     * Wird aufgerufen, wenn eine spezifische Karte gelöscht werden soll. Gibt die Karte weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn die Aktion erfolgreich war.
     * @param card
     * @return wahr, wenn Karte gelöscht wurde
     */
    public static boolean deleteCard(Card card)
    {

        if(card == null){
            throw new IllegalStateException("Karte existiert nicht");
            //TODO: welche weitere Exceptions können auftreten?
        }
        return CardRepository.deleteCard(card);
    }

    /**
     * Wird aufgerufen, wenn mehrere Karten gelöscht werden sollen. Gibt die Karten einzeln weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn alle Karten erfolgreich gelöscht wurden.
     * @param cards
     * @return wahr, wenn alle Karten erfolgreich gelöscht wurden
     */
    public static boolean deleteCards(Card[] cards)
    {
        boolean ret = true;
        for(Card c : cards)
        {
            if(!deleteCard(c))
                ret = false;
            //TODO: Meldung prüfen, welche Exceptions können auftreten und wie darstellen?
        }
        return ret;
    }


    public static boolean exportCards(Card[] cards, ExportFileType filetype)
    {
        return (new Exporter(filetype)).export(cards);
        //TODO:Exceptions?
    }
}
