package com.swp.Controller;


import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.Exporter.ExportFileType;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import com.gumse.gui.Locale;

import java.util.*;

@Slf4j
public class CardController {

    private static CardController cardController;
    private final CardLogic cardLogic = CardLogic.getInstance();
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    public static CardController getInstance() 
    {
        if (cardController == null)
            cardController = new CardController();
        return cardController;
    }

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an die CardLogic weiter.
     *
     * @param begin  Seitenauswahl Anfangswert
     * @param end  Seitenauswahl Endwert
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsToShow(int begin, int end, DataCallback<CardOverview> callback) {
        threadPool.exec(() -> {
            try {
                List<CardOverview> cardsToShow = cardLogic.getCardOverview(begin, end);

                if (cardsToShow.isEmpty()) {
                    log.info("Es wurden keine zugehörigen Karten gefunden");
                    callback.callInfo(Locale.getCurrentLocale().getString("getcardstoshowempty"));
                }
                else
                    callback.callSuccess(cardsToShow);

            } catch (final Exception ex) {
                log.error("Beim Suchen nach Karten ist ein Fehler {} aufgetreten"
                        , ex.getMessage());
                callback.callFailure("Beim Abrufen der Karten ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Nutzung für Display einzelner Karten in Filterfunktion in OverviewPage verwendet.
     * Wird an die CardLogic weitergegeben.
     *
     * @param tag  Der Tag, zu dem die Karten abgerufen werden sollen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsByTag(String tag, DataCallback<CardOverview> callback) {
        threadPool.exec(() -> {
            try {
                List<CardOverview> cardsForTag = cardLogic.getCardsByTag(tag);

                if (cardsForTag.isEmpty()){
                    log.info("Es wurden keine Karten für den Tag gefunden");
                    callback.callInfo(Locale.getCurrentLocale().getString("getcardsbytagempty"));
                }
                else
                    callback.callSuccess(cardsForTag);

            } catch (IllegalArgumentException ex) {
                log.error("Der übergebene Wert war leer");
                callback.callFailure(ex.getMessage());
            }  catch (final Exception ex) {
                log.error("Beim Suchen nach Karten mit Tag {} ist ein Fehler {} aufgetreten", tag
                        , ex);
                callback.callFailure(Locale.getCurrentLocale().getString("getcardsbytagerror"));
            }
        });
    }

    /**
     * Kann verwendet werden, um einzelne Tags zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     *
     * @param card Die Karte, zu der die Tags abgerufen werden sollen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getTagsToCard(Card card, DataCallback<Tag> callback) {
        threadPool.exec(() -> {
            try {
                List<Tag> tagsForCard = cardLogic.getTagsToCard(card);

                if (tagsForCard.isEmpty()) {
                    log.info(Locale.getCurrentLocale().getString("gettagstocard"));
                } else
                    callback.callSuccess(tagsForCard);
            } catch (final Exception ex) {
                log.error("Beim Suchen nach Tags der Karte {} ist ein Fehler {} aufgetreten", card.getUuid(), ex);
                callback.callFailure(Locale.getCurrentLocale().getString("gettagstocarderror"));
            }
        });
    }

    /**
     * Nutzung für Display bestimmter Karten bei CardOverviewPage. Wird an die CardLogic weitergegeben.
     *
     * @param searchterm Übergebener String mit dem Suchwort
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsBySearchterms(String searchterm, DataCallback<CardOverview> callback) {
        threadPool.exec(() -> {
            try {
                List<CardOverview> cardsForSearchTerms = cardLogic.getCardsBySearchterms(searchterm);

                if (cardsForSearchTerms.isEmpty()) {
                    callback.callInfo("Es gibt keine Karten für dieses Suchwort");
                    log.info(Locale.getCurrentLocale().getString("getcardsbysearchtermsempty"));
                } else
                    callback.callSuccess(cardsForSearchTerms);
            } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                callback.callFailure(ex.getMessage());
            } catch (final Exception ex) {
                log.error("Beim Suchen nach Karten mit dem Suchbegriff {} ist ein Fehler {} aufgetreten", searchterm, ex);
                callback.callFailure(Locale.getCurrentLocale().getString("getcardsbysearchtermserror"));
            }
        });
    }

    /**
     * Dient dem Löschen einzelner Karten. Wird an die CardLogic weitergegeben.
     *
     * @param card Die zu löschende Karte
     * @param singleDataCallback  Callback für die GUI, bei success passiert nichts, bei Fehler wird die Exception message an GUI weitergegeben.
     */
    public void deleteCard(Card card, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                cardLogic.deleteCard(card);
                singleDataCallback.callSuccess(true);
            }catch (IllegalStateException ex) {
                singleDataCallback.callFailure(ex.getMessage());
                log.error("Null-Value übergeben");
            }
            catch (Exception ex) {
                singleDataCallback.callFailure(Locale.getCurrentLocale().getString("deletecarderror"));
                log.error("Beim Löschen der Karte {} ist ein Fehler {} aufgetreten", card, ex);
            }
        });
    }

    /**
     * Dient dem Löschen mehrerer Karten. Wird an die CardLogic weitergegeben.
     *
     * @param cards Die zu löschenden Karten
     * @param singleDataCallback  Callback für die GUI, bei success passiert nichts, bei Fehler wird die Exception message an GUI weitergegeben.
     */
    public void deleteCards(List<CardOverview> cards, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                cardLogic.deleteCards(cards);
                singleDataCallback.callSuccess(true);
            }catch (IllegalStateException ex) {
                singleDataCallback.callFailure(ex.getMessage());
                log.error("Null-Value übergeben");
            } catch (Exception ex) {
                singleDataCallback.callFailure(Locale.getCurrentLocale().getString("deletecardserror"));
                log.error("Beim Löschen der Karten {} ist ein Fehler {} aufgetreten", cards, ex);
            }
        });
    }

    /**
     * Wird verwendet, um einzelne Karteninformationen über ihre UUID abzurufen. Wird an die CardLogic weitergegeben.
     *
     * @param uuid  UUID der abzurufenden Karte
     * @param singleDataCallback  Callback für die GUI, bei success wird Karte an GUI weitergegeben, bei Fehler wird die Exception message an GUI weitergegeben.
     *
     */
    public void getCardByUUID(String uuid, SingleDataCallback<Card> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                singleDataCallback.callSuccess(cardLogic.getCardByUUID(uuid));
            }  catch (IllegalArgumentException ex) {//übergebener Wert ist leer
                log.error("Der übergebene Wert war leer");
                singleDataCallback.callFailure(ex.getMessage());
            }
            catch(NoResultException ex){
                singleDataCallback.callFailure(Locale.getCurrentLocale().getString("getcardbyuuidempty"
                ));
                log.error("Es wurde keine Karte zur UUID {} gefunden",uuid);
            }
            catch(Exception ex){
                singleDataCallback.callFailure(Locale.getCurrentLocale().getString("getcardbyuuiderror"));
                log.error("Beim Abrufen der Karte ist ein Fehler {} aufgetreten",ex.getMessage());
            }
        });
    }


    /**
     * Wird verwendet, um Tags für eine Karte festzulegen. Wird an die CardLogic weitergegeben.
     *
     * @param card  die Karte
     * @param set  die Liste der Tags
     * @param singleDataCallback  Bei Success passiert nichts, bei Failure wird Exception an GUI weitergegeben.
     */
    public void setTagsToCard(Card card, List<Tag> set, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                cardLogic.setTagsToCard(card, set);
                singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.callFailure(Locale.getCurrentLocale().getString("settagstocarderror"));
                log.error("Beim Setzen der Tags für die Karte mit der UUID {} ist ein Fehler {} aufgetreten",card.getUuid(), ex.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um Data für eine Karte zu aktualisieren. Wird an die CardLogic weitergegeben.
     *
     * @param cardToChange  die Karte zu aktualisieren
     * @param neu  Ob, die Karte neue oder nicht ist zu verstehen
     * @param singleDataCallback  Bei Success passiert nichts, bei Failure wird Exception an GUI weitergegeben.
     */
    public void updateCardData(Card cardToChange, boolean neu, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                cardLogic.updateCardData(cardToChange, neu);
                singleDataCallback.callSuccess(true);
            }
            catch (IllegalStateException ex) {
                singleDataCallback.callFailure(ex.getMessage());
                log.error("Karte nicht gefunden");
            }
            catch(IllegalArgumentException ex){ //Array in MultipleChoiceCard falsch
                singleDataCallback.callFailure(ex.getMessage());
                log.error("Index der Multiple Choice Card falsch");
            }
            catch (Exception ex) {
                singleDataCallback.callFailure(String.format(Locale.getCurrentLocale().getString("updatecreatecarderror")));
                log.error("Beim Updaten/Speichern der Karte {} mit der ist ein Fehler {} aufgetreten",cardToChange.getUuid(),ex.getMessage());
            }
        });
    }

    //ZUSATZ

    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die CardLogic weitergereicht.
     *
     * @param cards    Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     * @param singleDataCallback  Bei Success passiert nichts, bei Failure wird Exception an GUI weitergegeben.
     */
    public void exportCards(List<CardOverview> cards, String destination, ExportFileType filetype, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try 
            {
                threadPool.addTaskToMainThread(() -> {
                    singleDataCallback.callSuccess(cardLogic.exportCards(cards, destination, filetype));
                });
            } 
            catch (Exception ex) 
            {
                singleDataCallback.callFailure(Locale.getCurrentLocale().getString("cardexporterror"));
                log.error("Beim Exportieren der Karte(n) gab es einen Fehler {}" + ex.getMessage());
            }
        });
    }

}


