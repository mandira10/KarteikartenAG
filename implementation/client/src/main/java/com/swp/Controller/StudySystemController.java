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
    private final StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    public static StudySystemController getInstance() 
    {
        if (studySystemController == null)
            studySystemController = new StudySystemController();
        return studySystemController;
    }


    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems. Wird an die StudySystemLogic weitergegeben
     *
     * @param card  Zu verschiebende Karte
     * @param box  Index der Box, in den die Karte verschoben werden soll
     * @param studySystem  Das StudySystem, das benötigt wird.
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void moveCardToSpecificBox(Card card, int box, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.moveCardToBox(card, box, studySystem);
                singleDataCallback.callSuccess(true);
            } catch (IllegalStateException s) {
                singleDataCallback.callFailure("Null Value Gegeben");
                log.error("Card nicht gefunden");
            } catch (Exception exception) {
                singleDataCallback.callFailure("Ein Fehler aufgetreten");
                log.error("Bei MovingCard in einem Box ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird nach der Erstellung eines neuen StudySystem verwendet und verschiebt alle Karten für das StudySystem in die erste Box.
     *
     * @param cards              Karten, die StudySystem enthalten soll.
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void moveAllCardsForDeckToFirstBox(List<Card> cards, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                if (cards.isEmpty()) {
                    throw new IllegalStateException();
                } else {
                    studySystemLogic.moveAllCardsForDeckToFirstBox(cards, studySystem);
                    singleDataCallback.callSuccess(true);
                }
            }
            catch (IllegalStateException e){
                singleDataCallback.callFailure("Null Value Gegeben");
                log.error("Card nicht gefunden");
            }
            catch (Exception e) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei MovingCard in erstem Box ist ein Fehler aufgetreten");
            }
        });
    }


    /**
     * Wird verwendet, um alle Karten in diesem Studiensystem zu erhalten. Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem  Das StudySystem, das benötigt wird.
     * @param dataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getAllCardsInStudySystem(StudySystem studySystem, DataCallback<CardOverview> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<CardOverview> cards = studySystemLogic.getAllCardsInStudySystem(studySystem);

                if (cards.isEmpty()) {
                    dataCallback.callInfo("Es gibt keine Karten zu diesem StudySystem");
                } else {
                    dataCallback.callSuccess(cards);
                }
            } catch (Exception ex) {
                dataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei Bekommen alle Karte in StudySystem ist ein Fehler aufgetreten");
            }
        });
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
        threadPool.exec(() -> {
            try {
                studySystemLogic.giveAnswer(studySystem, answer);
                singleDataCallback.callSuccess(true);
            } catch (Exception e) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei Antworten einer Karte ist ein Fehler aufgetreten");
            }
        });
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
        threadPool.exec(() -> {
            try {
                studySystemLogic.giveRating(studySystem, rating);
                singleDataCallback.callSuccess(true);
            } catch (Exception e) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei Geben Rating für einer Karte ist ein Fehler aufgetreten");
            }
        });
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
        threadPool.exec(() -> {
            try {
                studySystemLogic.giveTime(studySystem, seconds);
                singleDataCallback.callSuccess(true);
            } catch (Exception e) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei Geben Time für einer Karte ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird verwendet, um den Test zu beenden und Ergebnispunkt zu berechnen
     * wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void finishTestAndGetResult(StudySystem studySystem, SingleDataCallback<Integer> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                singleDataCallback.callSuccess(studySystemLogic.finishTestAndGetResult(studySystem));
            } catch (Exception e) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei Beenden Test ist ein Fehler aufgetreten");
            }
        });
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
        threadPool.exec(() -> {
            try {
                singleDataCallback.callSuccess(studySystemLogic.getNextCard(studySystem));
            } catch (Exception e) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei Bekommen nächste Karte ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird verwendet, um Progress für dieses Deck zu bekommen.
     * Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getProgress(StudySystem studySystem, SingleDataCallback<Double> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                singleDataCallback.callSuccess(studySystemLogic.getProgress(studySystem));
            } catch (Exception e) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei Bekommen Progress ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird verwendet, um die Anzahl der Karten in einem studySystem zu bekommen. Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem         studySystem, um die Anzahl der Karten darin zu suchen
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void numCardsInDeck(StudySystem studySystem, SingleDataCallback<Integer> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                singleDataCallback.callSuccess(studySystemLogic.numCardsInDeck(studySystem));
            } catch (Exception ex) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Beim Abrufen von der Anzahl der studySystems ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird verwendet, um ein StudySystem nach UUID zu bekommen. Wird an die StudySystemLogic weitergegeben
     *
     * @param uuid   UUID zu suchen
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystemByUUID(String uuid, SingleDataCallback<StudySystem> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                singleDataCallback.callSuccess(studySystemLogic.getStudySystemByUUID(uuid));
            } catch (IllegalArgumentException ex) {//übergebener Wert ist leer
                singleDataCallback.callFailure("UUID darf nicht null sein");
                log.error("UUID darf nicht null sein");
            } catch (NoResultException ex) {
                singleDataCallback.callFailure("Es konnte kein studySystem zur UUID gefunden werden");
                log.error("Es konnte kein StudySystem zur UUID gefunden werden");
            } catch (Exception ex) {
                singleDataCallback.callFailure("Beim Abrufen des studySystems ist ein Fehler aufgetreten");
                log.error("Beim Abrufen des StudySystems nach UUID ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird verwendet, um eine Liste von Karten in einem StudySystem zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     * @param studySystem         Das StudySystem, das benötigt wird.
     * @param list                die Liste der Karten zu löschen
     */
    public void removeCardsFromStudySystem(List<CardOverview> list, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.removeCardsFromStudySystem(list, studySystem);
                singleDataCallback.callSuccess(true);
            } catch (IllegalStateException ex) {
                singleDataCallback.callFailure("Null Variable gegeben");
                log.error("Bei RemoveCard in einem StudySystem hat Null Variable Gegeben");
            } catch (Exception ex) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei RemoveCards in einem StudySystem ist ein Fehler aufgetreten");
            }
        });
    }


    /**
     * Wird verwendet, um ein StudySystem zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     * @param studySystem   StudySystem zu löschen.
     */
    public void deleteStudySystem(StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.deleteStudySystem(studySystem);
                singleDataCallback.callSuccess(true);
            } catch (IllegalStateException ex) {
                singleDataCallback.callFailure(ex.getMessage());
                log.error("Bei Löschen ein StudySystem hat Null Variable Gegeben");
            } catch (Exception ex) {
                singleDataCallback.callFailure("Beim Löschen der StudySystem ist ein Fehler aufgetreten");
                log.error("Beim Löschen der StudySystem ist ein Fehler aufgetreten");
            }
        });
    }


    /**
     * Wird verwendet, um eine Liste von studySystems zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void deleteDecks(StudySystem[] studySystems, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.deleteStudySystem(studySystems);
                singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Beim Löschen einer Liste von StudySystems ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird verwendet, um StudySystem zu bekommen
     *
     * @param dataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystems(DataCallback<StudySystem> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<StudySystem> studySystems = studySystemLogic.getStudySystems();
                if (studySystems.isEmpty()) {
                    dataCallback.callInfo("Es gibt derzeit keine studySystems");
                }

                dataCallback.callSuccess(studySystems);
            } catch (Exception ex) {
                dataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Beim Bekommen StudySystems ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird verwendet, um eine Suche für StudySystem nach einem Suchbegriff machen.
     *
     * @param searchterm   Suchbegriff um nachzusuchen.
     * @param dataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystemBySearchTerms(String searchterm, DataCallback<StudySystem> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<StudySystem> studySystems = studySystemLogic.getStudySystemsBySearchterm(searchterm);
                if (studySystems.isEmpty()) {
                    dataCallback.callInfo("Es gibt keine studySystems zu diesem Suchbegriff");
                }
                dataCallback.callSuccess(studySystems);
            } catch (IllegalArgumentException ex) {
                dataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Beim Bekommen StudySystems nach Suchgriff ist ein Fehler aufgetreten");
            }
        });
    }


    /**
     * Für nachträgliches Hinzufügen von Karten. Wird an die StudySystemLogic weitergegeben
     *
     * @param cards                 die Liste von Karten, um hinzufügen
     * @param studySystem           Das StudySystem, das benötigt wird.
     * @param singleDataCallback    wird verwendet, um mögliche Fehler abzufangen. 
     */
    public void addCardsToStudySystem(List<CardOverview> cards, StudySystem studySystem, SingleDataCallback<String> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                List<Card> existingCards = studySystemLogic.addCardsToDeck(cards, studySystem);
                if(!existingCards.isEmpty()){
                    String result = existingCards.stream().map(Card::getTitle)
                            .collect(Collectors.joining(","));
                    singleDataCallback.callSuccess("Karten bereits in Deck enthalten, nicht hinzugefügt:" + result);
                }
                else {
                    singleDataCallback.callSuccess("");
                }
            } catch (Exception ex) {
                log.error("Beim Hinzufügen eine Liste von Karten in einem StudySystem ist ein Fehler aufgetreten");
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
            }
        });
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
        threadPool.exec(() -> {
            try {
                studySystemLogic.updateStudySystemData(oldStudySystem, newStudySystem, neu);
                singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Bei Update StudySystem Data ist ein Fehler aufgetreten");
            }
        });
    }

    /**
     * Wird verwendet, um ein komplett neues StudySystem anzulegen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param type               Typ des StudySystems
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void addStudySystemTypeAndUpdate(StudySystem.StudySystemType type, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.addStudySystemTypeAndUpdate(type);
                singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Beim Hinzufügen und Update ein StudySystemType ist ein Fehler aufgetreten");
            }
        });
    }


    /**
     * Wird verwendet, um eine Karte in einem studySystem mit einer bestimmten Kategorie zu erstellen. Wird an die StudySystemLogic weitergegeben
     *
     * @param category           die Kategorie für die Karte
     * @param studySystem        studySystem, um die Karte darin zu speichern
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void createBoxToCardForCategory(Category category, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.createBoxToCardForCategory(category, studySystem);
                singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                singleDataCallback.callFailure("Ein Fehler ist aufgetreten");
                log.error("Beim Erstellen Box to Card für Kategorie ist ein Fehler aufgetreten");
            }
        });
    }




}

