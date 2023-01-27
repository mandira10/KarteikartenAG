package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.StudySystemLogic;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class StudySystemController {

    private static StudySystemController studySystemController;

    public static StudySystemController getInstance() {
        if (studySystemController == null)
            studySystemController = new StudySystemController();
        return studySystemController;
    }

    StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems. Wird an die StudySystemLogic weitergegeben
     *
     * @param card  Zu verschiebende Karte
     * @param box  Index der Box, in den die Karte verschoben werden soll
     * @param studySystem  Das StudySystem, das benötigt wird.
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void moveCardToSpecificBox(Card card, int box, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.moveCardToBox(card, box, studySystem);
                singleDataCallback.onSuccess(true);
            } catch (IllegalStateException s) {
                singleDataCallback.onFailure("Null Value Gegeben");
            } catch (Exception exception) {
                singleDataCallback.onFailure("Ein Fehler aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird nach der Erstellung eines neuen StudySystem verwendet und verschiebt alle Karten für das StudySystem in die erste Box.
     *
     * @param cards              Karten, die StudySystem enthalten soll.
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void moveAllCardsForDeckToFirstBox(List<Card> cards, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                if (cards.isEmpty()) {
                    throw new IllegalStateException();
                } else {
                    studySystemLogic.moveAllCardsForDeckToFirstBox(cards, studySystem);
                    singleDataCallback.onSuccess(true);
                }
            } catch (Exception e) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }


    /**
     * Wird verwendet, um alle Karten in diesem Studiensystem zu erhalten. Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem  Das StudySystem, das benötigt wird.
     * @param dataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getAllCardsInStudySystem(StudySystem studySystem, DataCallback<CardOverview> dataCallback) {
        //new Thread(() -> {
            try {
                List<CardOverview> cards = studySystemLogic.getAllCardsInStudySystem(studySystem);

                if (cards.isEmpty()) {
                    dataCallback.onInfo("Es gibt keine Karten zu diesem StudySystem");
                } else {
                    dataCallback.onSuccess(cards);
                }
            } catch (Exception ex) {
                dataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, sodass
     * je nach Antwort die Karte in den Boxen verschoben werden kann. Wird an die StudySystemLogic weitergegeben
     *
     * @param answer             Frage war richtig / falsch beantwortet
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void giveAnswer(StudySystem studySystem, boolean answer, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.giveAnswer(studySystem, answer);
                singleDataCallback.onSuccess(true);
            } catch (Exception e) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um eine Bewertung vom Benutzer für VoteStudySystem zu bekommen.
     * Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     * @param rating   Bewertung von GUI
     */
    public void giveRating(StudySystem studySystem, int rating, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.giveRating(studySystem, rating);
                singleDataCallback.onSuccess(true);
            } catch (Exception e) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um eine Antwortzeit vom Benutzer für TimingStudySystem zu bekommen.
     * Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     * @param seconds   Antwortzeit vom Benutzer für die Frage
     */
    public void giveTime(StudySystem studySystem, float seconds, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.giveTime(studySystem, seconds);
                singleDataCallback.onSuccess(true);
            } catch (Exception e) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um den Test zu beenden und Ergebnispunkt zu berechnen
     * wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void finishTestAndGetResult(StudySystem studySystem, SingleDataCallback<Integer> singleDataCallback) {
        //new Thread(() -> {
            try {
                singleDataCallback.onSuccess(studySystemLogic.finishTestAndGetResult(studySystem));
            } catch (Exception e) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }


    /**
     * Wird verwendet, um die nächste Frage zu bekommen,
     * wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     *                           //TODO  javadoc, ich würde sagen wir brauchen hier die Box noch, damit wir wissen, woraus wir die nächste Karte ziehen
     */
    public void getNextCard(StudySystem studySystem, SingleDataCallback<Card> singleDataCallback) {
        //new Thread(() -> {
            try {
                singleDataCallback.onSuccess(studySystemLogic.getNextCard(studySystem));
            } catch (Exception e) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um Progress für dieses Deck zu bekommen.
     * Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getProgress(StudySystem studySystem, SingleDataCallback<Float> singleDataCallback) {
        //new Thread(() -> {
            try {
                singleDataCallback.onSuccess(studySystemLogic.getProgress(studySystem));
            } catch (Exception e) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um die Anzahl der Karten in einem studySystem zu bekommen. Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem         studySystem, um die Anzahl der Karten darin zu suchen
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void numCardsInDeck(StudySystem studySystem, SingleDataCallback<Integer> singleDataCallback) {
        //new Thread(() -> {
            try {
                singleDataCallback.onSuccess(studySystemLogic.numCardsInDeck(studySystem));
            } catch (Exception ex) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
                log.error("Beim Abrufen von der Anzahl der studySystems ist ein Fehler aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um ein StudySystem nach UUID zu bekommen. Wird an die StudySystemLogic weitergegeben
     *
     * @param uuid   UUID zu suchen
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystemByUUID(String uuid, SingleDataCallback<StudySystem> singleDataCallback) {
        //new Thread(() -> {
            try {
                singleDataCallback.onSuccess(studySystemLogic.getStudySystemByUUID(uuid));
            } catch (IllegalArgumentException ex) {//übergebener Wert ist leer
                singleDataCallback.onFailure("UUID darf nicht null sein");
            } catch (NoResultException ex) {
                singleDataCallback.onFailure("Es konnte kein studySystem zur UUID gefunden werden");
            } catch (Exception ex) {
                singleDataCallback.onFailure("Beim Abrufen des studySystems ist ein Fehler aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um eine Liste von Karten in einem StudySystem zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     * @param studySystem         Das StudySystem, das benötigt wird.
     * @param list                die Liste der Karten zu löschen
     */
    public void removeCardsFromStudySystem(List<CardOverview> list, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.removeCardsFromStudySystem(list, studySystem);
                singleDataCallback.onSuccess(true);
            } catch (IllegalStateException ex) {
                singleDataCallback.onFailure("Null Variable gegeben");
            } catch (Exception ex) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }


    /**
     * Wird verwendet, um ein StudySystem zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     * @param studySystem   StudySystem zu löschen.
     */
    public void deleteStudySystem(StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.deleteStudySystem(studySystem);
                singleDataCallback.onSuccess(true);
            } catch (IllegalStateException ex) {
                singleDataCallback.onFailure(ex.getMessage());
            } catch (Exception ex) {
                singleDataCallback.onFailure("Beim Löschen der StudySystem ist ein Fehler aufgetreten");
            }
         //}).start();
    }


    /**
     * Wird verwendet, um eine Liste von studySystems zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void deleteDecks(StudySystem[] studySystems, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.deleteStudySystem(studySystems);
                singleDataCallback.onSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um StudySystem zu bekommen
     *
     * @param dataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystems(DataCallback<StudySystem> dataCallback) {
        //new Thread(() -> {
            try {
                List<StudySystem> studySystems = studySystemLogic.getStudySystems();
                if (studySystems.isEmpty()) {
                    dataCallback.onInfo("Es gibt derzeit studySystems");
                }

                dataCallback.onSuccess(studySystems);
            } catch (Exception ex) {
                dataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um eine Suche für StudySystem nach einem Suchbegriff machen.
     *
     * @param searchterm   Suchbegriff um nachzusuchen.
     * @param dataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystemBySearchTerms(String searchterm, DataCallback<StudySystem> dataCallback) {
        //new Thread(() -> {
            try {
                List<StudySystem> studySystems = studySystemLogic.getStudySystemsBySearchterm(searchterm);
                if (studySystems.isEmpty()) {
                    dataCallback.onInfo("Es gibt keine studySystems zu diesem Suchbegriff");
                }
                dataCallback.onSuccess(studySystems);
            } catch (IllegalArgumentException ex) {
                dataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }


    /**
     * Für nachträgliches Hinzufügen von Karten. Wird an die StudySystemLogic weitergegeben
     *
     * @param cards                 die Liste von Karten, um hinzufügen
     * @param studySystem           Das StudySystem, das benötigt wird.
     * @param singleDataCallback    wird verwendet, um mögliche Fehler abzufangen. 
     */
    public void addCardsToStudySystem(List<CardOverview> cards, StudySystem studySystem, SingleDataCallback<String> singleDataCallback) {
        //new Thread(() -> {
            try {
                List<Card> existingCards = studySystemLogic.addCardsToDeck(cards, studySystem);
                if(!existingCards.isEmpty()){
                    String result = existingCards.stream().map(Card::getTitle)
                            .collect(Collectors.joining(","));
                    singleDataCallback.onSuccess("Karten bereits in Deck enthalten, nicht hinzugefügt:" + result);
                }
                else {
                    singleDataCallback.onSuccess("");
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um das StudySystem zu updaten. Wird an die StudySystemLogic weitergegeben.
     *
     * @param oldStudySystem     StudySystem im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newStudySystem     Neue StudySystem Eigenschaften
     * @param neu                Ist true, wenn das StudySystem neu angelegt wurde
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void updateDeckData(StudySystem oldStudySystem, StudySystem newStudySystem, boolean neu, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.updateStudySystemData(oldStudySystem, newStudySystem, neu);
                singleDataCallback.onSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }

    /**
     * Wird verwendet, um ein komplett neues StudySystem anzulegen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param type               Typ des StudySystems
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void addStudySystemTypeAndUpdate(StudySystem.StudySystemType type, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.addStudySystemTypeAndUpdate(type);
                singleDataCallback.onSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }


    /**
     * Wird verwendet, um eine Karte in einem studySystem mit einer bestimmten Kategorie zu erstellen. Wird an die StudySystemLogic weitergegeben
     *
     * @param category           die Kategorie für die Karte
     * @param studySystem        studySystem, um die Karte darin zu speichern
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void createBoxToCardForCategory(Category category, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        //new Thread(() -> {
            try {
                studySystemLogic.createBoxToCardForCategory(category, studySystem);
                singleDataCallback.onSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.onFailure("Ein Fehler ist aufgetreten");
            }
         //}).start();
    }




}

