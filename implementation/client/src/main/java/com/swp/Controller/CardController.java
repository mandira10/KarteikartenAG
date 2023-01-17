package com.swp.Controller;


import com.swp.DataModel.Card;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.Exporter.ExportFileType;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CardController {

    private final CardLogic cardLogic = CardLogic.getInstance();

    private static CardController cardController;

    public static CardController getInstance() {
        if (cardController == null)
            cardController = new CardController();
        return cardController;
    }


    //CARDOVERVIEW

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an die CardLogic weiter.
     *
     * @param begin: Seitenauswahl Anfangswert
     * @param end: Seitenauswahl Endwert
     */
    public void getCardsToShow(int begin, int end, DataCallback<Card> callback) {
        try {
            List<Card> cardsToShow = cardLogic.getCardsToShow(begin, end);

            if (cardsToShow.isEmpty())
                callback.onInfo("Es gibt bisher noch keine Karten");
            else
                callback.onSuccess(cardsToShow);

        } catch (final Exception ex) {
            log.error("Beim Suchen nach Karten ist ein Fehler aufgetreten"
                    , ex);
            callback.onFailure(ex.getMessage());
        }

    }

    /**
     * Nutzung für Display einzelner Karten in Filterfunktion in OverviewPage verwendet.
     * Wird an die CardLogic weitergegeben.
     *
     * @param tag: Der Tag, zu dem die Karten abgerufen werden sollen
     */
    public void getCardsByTag(String tag, DataCallback<Card> callback) {
        try {
            List<Card> cardsForTag = cardLogic.getCardsByTag(tag);

            if (cardsForTag.isEmpty())
                callback.onInfo("Es gibt keine Karten für diesen Tag");
            else
                callback.onSuccess(cardsForTag);

        } catch (IllegalArgumentException ex) {
            callback.onFailure(ex.getMessage()); //übergebener wert ist leer
        } catch (final NoResultException ex) {
            callback.onFailure(ex.getMessage());
        } catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit Tag {} ist ein Fehler {} aufgetreten", tag
                    , ex);
            callback.onFailure(ex.getMessage());
        }

    }

    /**
     * Kann verwendet werden, um einzelne Tags zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     *
     * @param card Die Karte, zu der die Tags abgerufen werden sollen
     */
    public void getTagsToCard(Card card, DataCallback<Tag> callback) {
        try {
            List<Tag> tagsForCard = cardLogic.getTagsToCard(card);

            if (tagsForCard.isEmpty())
                callback.onInfo("Keine Tags für diese Karten vorhanden"); // log.info("Keine Tags für diese Karte vorhanden");
            else
                callback.onSuccess(tagsForCard);
        } catch (final Exception ex) {
            callback.onFailure(ex.getMessage()); //log.error("Beim Suchen nach Tags mit Karten {} ist ein Fehler {} aufgetreten", card, ex);
        }
    }

    /**
     * Nutzung für Display bestimmter Karten bei CardOverviewPage. Wird an die CardLogic weitergegeben.
     *
     * @param searchterm Übergebener String mit dem Suchwort
     */
    public void getCardsBySearchterms(String searchterm, DataCallback<Card> callback) {
        try {
            List<Card> cardsForSearchTerms = cardLogic.getCardsBySearchterms(searchterm);

            if (cardsForSearchTerms.isEmpty()) {
                callback.onInfo("Es gibt keine Karten für dieses Suchwort");
            } else
                callback.onSuccess(cardsForSearchTerms);
        } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
            callback.onFailure(ex.getMessage()); //log.error
        } catch (final Exception ex) {
            callback.onFailure(ex.getMessage()); //log.error
        }
    }

    /**
     * Dient dem Löschen einzelner Karten. Wird an die CardLogic weitergegeben.
     *
     * @param card Die zu löschende Karte
     */
    public void deleteCard(Card card, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            cardLogic.deleteCard(card);
        } catch (IllegalStateException ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Dient dem Löschen mehrerer Karten. Wird an die CardLogic weitergegeben.
     *
     * @param cards Die zu löschenden Karten
     */
    public void deleteCards(List<Card> cards, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            cardLogic.deleteCards(cards);
        } catch (IllegalStateException ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }


    //SINGLEVIEWCARDPAGE, CARDEDITPAGE

    /**
     * Wird verwendet, um einzelne Karteninformationen über ihre UUID abzurufen. Wird an die CardLogic weitergegeben.
     *
     * @param uuid: UUID der abzurufenden Karte
     */
    public void getCardByUUID(String uuid, SingleDataCallback<Card> singleDataCallback) {
        try {
            singleDataCallback.onSuccess(cardLogic.getCardByUUID(uuid));
        }  catch (IllegalArgumentException ex) {//übergebener Wert ist leer
            singleDataCallback.onFailure(ex.getMessage());
        }
        catch(NoResultException ex){
            singleDataCallback.onFailure("Es konnte kein Deck zur UUID gefunden werden");
        }
        catch(Exception ex){
            singleDataCallback.onFailure("Beim Abrufen des Decks ist ein Fehler aufgetreten");
        }
    }


    public void setTagsToCard(Card card, List<Tag> set, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            cardLogic.setTagsToCard(card, set);
        } catch (Exception ex) {
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    public void updateCardData(Card cardToChange, boolean neu, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            cardLogic.updateCardData(cardToChange, neu);
        } catch (Exception e) {
            singleDataCallback.onFailure(String.format("Karte \"%s\" konnte nicht gespeichert oder geupdatet werden", cardToChange.getUuid()));
        }
    }

    //ZUSATZ

    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die CardLogic weitergereicht.
     *
     * @param cards    Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     */
    public void exportCards(Card[] cards, ExportFileType filetype, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            cardLogic.exportCards(cards, filetype);
        } catch (Exception ex) {
            singleDataCallback.onFailure("Probleme");
        }
    }
}


