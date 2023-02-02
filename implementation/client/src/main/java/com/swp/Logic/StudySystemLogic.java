package com.swp.Logic;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToBoxRepository;
import com.swp.Persistence.StudySystemRepository;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.*;

import static com.swp.Validator.checkNotNullOrBlank;

/**
 * StudySystemLogic Klasse, behandelt alle Studysystem spezifischen Aufrufe.
 * Erbt von der BaseLogic.
 */
@Slf4j
public class StudySystemLogic extends BaseLogic<StudySystem>{

    /**
     * Konstruktor der Klasse StudySystemLogic.
     */
    public StudySystemLogic() {
        super(StudySystemRepository.getInstance());
    }

    /**
     * Instanz von StudySystemLogic
     */
    private static StudySystemLogic studySystemLogic;

    /**
     * Aufruf einer Instanz von StudySystemLogic. Stellt sicher, dass nur eine Instanz zur gleichen
     * Zeit der Klasse besteht
     * @return die Instanz der Klasse
     */
    public static StudySystemLogic getInstance() {
        if (studySystemLogic == null)
            studySystemLogic = new StudySystemLogic();
        return studySystemLogic;
    }


    /**
     * Hilfsattribut fürs Testing von Karten.
     * Temporäre Liste aller Karten, die im aktuellen Lerndurchlauf gelernt werden sollen.
     * Wird beim Beenden des Tests oder bei Abbruch wieder geleert.
     */
    private List<Card> testingBoxCards = new ArrayList<>();

    /**
     * Gibt an, ob ein Testdurchlauf gerade aktiv ist,
     * wird in getNextCard() verwendet, damit keine neuen
     * Karten gezogen werden, wenn der Lerndurchlauf fertig ist.
     * Wird in finishTestAndGetResult wieder auf false gesetzt.
     */
    private boolean testingStarted = false;

    /**
     * Benutze Repositories, auf die zugegriffen wird.
     */
    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems. 
     * @param cardToBox: Zu verschiebende Karte
     * @param newBox: Index der Box, in den die Karte verschoben werden soll
     * @param studySystem Das StudySystem, das benötigt wird.
     */
    public void moveCardToBox(BoxToCard cardToBox, int newBox, StudySystem studySystem)
    {
        cardToBox.setStudySystemBox(studySystem.getBoxes().get(newBox));
        log.info("Card moved to box {}" , newBox);
    }

    /**
     * Wird nach der Erstellung eines neuen StudySystem verwendet, Hauptfunktion erfolgt über moveAllCardToFirstBoxNoExec.
     * @param cards: Karten, die StudySystem enthalten soll.
     * @param studySystem: Das StudySystem, das benötigt wird.
     */
    public List<Card> moveAllCardsForDeckToFirstBox(List<Card> cards, StudySystem studySystem) {
        return execTransactional(() -> moveAllCardsForDeckToFirstBoxNoExec(cards,studySystem));

    }

    /**
     * Wird nach der Erstellung eines neuen StudySystem verwendet und verschiebt alle Karten für das StudySystem in die erste Box.
     * Vorher wird geprüft, ob die Karte bereits im StudySystem enthalten ist, wenn ja, wird sie nicht nochmal hinzugefügt und an den Controller
     * zurückgespielt, so dass die GUI anzeigen kann, welche Karten nicht hinzugefügt werden konnten.
     * Ruft Hilfsmethode moveCardToBoxAndSave auf.
     * @param cards: Karten, die StudySystem enthalten soll.
     * @param studySystem: Das StudySystem, das benötigt wird.
     */
    public List<Card> moveAllCardsForDeckToFirstBoxNoExec(List<Card> cards, StudySystem studySystem) {
        if (cards.isEmpty()) {
            throw new IllegalStateException(Locale.getCurrentLocale().getString("moveallcardsfordecktofirstboxempty"));
        }
        List<Card> existingCards = new ArrayList<>();
        for(Card c : cards)
            try{
                Card card = cardRepository.findCardByStudySystem(studySystem,c);
                log.info("Karte bereits Teil des StudySystems");
                existingCards.add(card);
            }
            catch(NoResultException ex){
                moveCardToBoxAndSave(c,0,studySystem);
            }
        return existingCards;
    }

    /**
     * Verschiebt und speichert eine neue Karte in einer Box des StudySystems.
     * Hilfsmethode für das initiale Hinzufügen von Karten zu einem StudySystem, daher private.
     * @param card: Zu verschiebene Karte
     * @param newBox: Index der Box, in den die Karte verschoben werden soll
     * @param studySystem: Das StudySystem, das benötigt wird.
     */
    private void moveCardToBoxAndSave(Card card, int newBox, StudySystem studySystem) //Testet
    {
        BoxToCard boxToCard = new BoxToCard(card, studySystem.getBoxes().get(newBox));
        cardToBoxRepository.save(boxToCard);

    }
    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, so dass
     * je nach Antwort die Karte in den Boxen verschoben werden kann (Leitner System) oder
     * die Karte aktualisiert wird je nach Antwort (Status wird auf gelernt/ erneut Lernen gesetzt) und
     * die Karte fällt aus dem Lernstapel oder wird als neu zu lernen festgelegt.
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param answer : Frage war richtig / falsch beantwortet
     */
    public void giveAnswer(StudySystem studySystem,boolean answer) { //Testet
        log.info("Antwort wird in DB gespeichert");
        //setze Karte auf gelernt und entferne sie aus der testingBoxCards
        studySystem.incrementQuestionCount(); //Karte gelernt
        Card cardLearned = testingBoxCards.get(0); //Zieh dir aktuelle Karte
        testingBoxCards.remove(cardLearned); //entferne Karte aus dem testingBoxCards
        BoxToCard boxToCard = getBoxToCard(cardLearned, studySystem); //die BoxToCard, die aktuell gelernt wird
        int box = boxToCard.getStudySystemBox().getBoxNumber(); //needed for checks and movement in all systems
            if (studySystem.getType() == StudySystem.StudySystemType.LEITNER) {

                if (answer) {
                    studySystem.incrementTrueCount();
                    log.info("Setze True Count +1");//Setze TrueCount to 1
                    if (box < studySystem.getBoxes().size() - 1) { //avoid index out of bound exception, adapted to new Leitner Method
                        ++box;
                        moveCardToBox(boxToCard, box, studySystem);
                        changeCardDueDate(boxToCard);
                        boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);

                    } else {
                        changeCardDueDate(boxToCard);
                        boxToCard.setStatus(BoxToCard.CardStatus.SKILLED);
                    }
                }
                else { //answer was false, set card to new box
                    boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);
                    if (box > 0) { //avoid index out of bound exception
                        --box;
                        moveCardToBox(boxToCard, box, studySystem);
                        changeCardDueDate( boxToCard);
                    } else {
                        changeCardDueDate(boxToCard);
                    }
                    if (box == 0)
                        testingBoxCards.add(cardLearned);
                }
            }
            else {
                if (answer) {
                    studySystem.incrementTrueCount();
                    log.info("Setze True Count +1");
                    boxToCard.setStatus(BoxToCard.CardStatus.SKILLED);
                    ++box; //set to Box 1 where all cards are learned
                    moveCardToBox(boxToCard, box, studySystem);
                }
                else{
                    boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);
                    testingBoxCards.add(cardLearned); //Karte muss erneut angeschaut werden, füge sie hinten hinzu
                }
            }
            execTransactional(() -> { //DB action starts here
            cardToBoxRepository.update(boxToCard);
            return true;
        });
    }


    /**
     * Wird verwendet, um nächste Lernzeit einer Frage für LeitnerSystem zu ändern
     * @param boxToCard : Um Box und Card zusammen zu speichern
     */
    public void changeCardDueDate(BoxToCard boxToCard) { //done
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Timestamp(System.currentTimeMillis()));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.DATE,boxToCard.getStudySystemBox().getDaysToLearnAgain());
            boxToCard.setLearnedNextAt(new Timestamp(cal.getTimeInMillis()));
            log.info("Card date changed to {}" ,boxToCard.getLearnedNextAt());
    }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @param studySystem: Das StudySystem, das benötigt wird.
     * @return Karte die als nächstes gelernt werden soll
     */
    public Card getNextCard(StudySystem studySystem){
        return execTransactional(() -> {
        //Abfrage 1: StudySystem wurde noch nie gelernt, benutze initiale Kartenreihenfolge fürs StudySystem
        if(testingBoxCards.isEmpty() && !testingStarted && studySystem.isNotLearnedYet()) {
            log.info("Rufe Karten für Lernsystem mit initialer Kartenreihenfolge ab: {}", studySystem.getCardOrder());
            switch (studySystem.getCardOrder()) {
                case ALPHABETICAL ->
                        testingBoxCards = cardRepository.getAllCardsInitiallyOrdered(studySystem, "asc");
                case REVERSED_ALPHABETICAL ->
                        testingBoxCards = cardRepository.getAllCardsInitiallyOrdered(studySystem, "desc");
                case RANDOM -> {
                    testingBoxCards = cardRepository.getAllCardsForStudySystem(studySystem);
                    Collections.shuffle(testingBoxCards);
                }
            }
            if(testingBoxCards.isEmpty()){ //no cards learnable, no cards in studysystem yet
                throw new NoResultException(Locale.getCurrentLocale().getString("studysystemlearningerror"));
            }
            testingStarted = true;
            studySystem.setNotLearnedYet(false);
        }
        else if(testingBoxCards.isEmpty() && !testingStarted) {
            log.info("Rufe Karten für Lernsystem mit Kartenreihenfolge nach Lernsystemlogik ab");
            switch (studySystem.getType()) {
                case TIMING:
                    testingBoxCards = cardRepository.getAllCardsForTimingSystem(studySystem);
                    break;
                case LEITNER:
                    testingBoxCards = cardRepository.getAllCardsNeededToBeLearned(studySystem);
                    break;
                case VOTE:
                    testingBoxCards = cardRepository.getAllCardsSortedForVoteSystem(studySystem);
                    break;
            }
            testingStarted = true;
        }
        if (testingBoxCards.isEmpty()){
            log.info("Keine Karten mehr zu lernen");
                return null; //wird im GUI abgerufen und finishTestAndGetResult aufgerufen.
            }

            log.info("Rufe die nächste Karte zum Lernen ab");
            Card cardToLearn = testingBoxCards.get(0);
            log.info("{} wird übergeben", cardToLearn.getQuestion());
            return cardToLearn;
        });
    }


    /**
     * Wird verwendet, um eine Bewertung vom Benutzer für VoteStudySystem zu bekommen.
     *
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param card Karte, die Rating erhält
     * @param rating Bewertung von GUI
     */
    public void giveRating(StudySystem studySystem, Card card, int rating) {
        if(studySystem.getType() == StudySystem.StudySystemType.VOTE){
            BoxToCard boxToCard = getBoxToCard(card,studySystem);
            boxToCard.setRating(rating);
            execTransactional(() -> cardToBoxRepository.update(boxToCard));
        }
        else{
            throw new IllegalStateException("Falsches StudySystem");
        }
    };

    public void updateCardToBox(BoxToCard cardToBox){
        execTransactional(() -> cardToBoxRepository.update(cardToBox));
    }


    /**
     * Wird verwendet, um eine Antwortzeit vom Benutzer für TimingStudySystem zu bekommen.
     *
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param seconds     : Antwortzeit vom Benutzer für die Frage
     */
    public void giveTime(StudySystem studySystem, float seconds) { //Testet
        if(studySystem.getType() == StudySystem.StudySystemType.TIMING){
            TimingSystem timingSystem = (TimingSystem) studySystem;
            if(seconds > timingSystem.getTimeLimit()){
                studySystem.setTrueAnswerCount(studySystem.getTrueAnswerCount()-1);
                execTransactional(() -> studySystemRepository.update(studySystem));
            }
        }
            else {
                throw new IllegalStateException("Falsches StudySystem");
            }

    };

    /**
     * Wird verwendet, um den Test zu beenden und Ergebnispunkt zu berechnen
     * 
     * @param studySystem Das StudySystem, das benötigt wird.
     * @return ResultPoint von der Funktion calculateResultAndSave bekommen wird
     */
    public int finishTestAndGetResult(StudySystem studySystem) { // Testet
        double result;
        if(studySystem.getTrueAnswerCount() == 0 || studySystem.getQuestionCount() == 0) {
            testingBoxCards.removeAll(testingBoxCards);
            result = 0;
            log.info("Result is {}", result);
        }
        //everything else
        else {
            result = 100 * studySystem.getTrueAnswerCount() / studySystem.getQuestionCount();
            log.info("True Answers  {}", studySystem.getTrueAnswerCount());
            log.info("Answers  {}", studySystem.getQuestionCount());
            log.info("Result is {}", result);
            if (!testingBoxCards.isEmpty()) {
                testingBoxCards.removeAll(testingBoxCards); //remove for a fresh start

            }
            else {
                //wenn alle Karten gelernt worden sind, dann setze alle vom Timing/Vote System wieder auf Box 0
                if(!studySystem.getType().equals(StudySystem.StudySystemType.LEITNER)){
                    moveAllCardsBackToFirstBoxForRelearning(studySystem);
                }
            }
        }
        //reset variables for next learning
        studySystem.setTrueAnswerCount(0);
        studySystem.setQuestionCount(0);
        testingStarted = false;
        log.info("Speichere Progress in Database");
         execTransactional(() -> {
             calculateProgress(studySystem);
            studySystemRepository.update(studySystem); //save in Database (!)
            return null;
        });
         return (int) result;
        }

    /**
     * Wird aufgerufen, wenn ein StudySystem des Typs Vote/Timing fertig gelernt wurde.
     * Alle Karten werden wieder in Box 0 gesetzt, verlieren aber nicht ihren Status.
     * @param studySystem Das StudySystem, wo die Karten verschoben werden sollen.
     */
    private void moveAllCardsBackToFirstBoxForRelearning(StudySystem studySystem) {
          execTransactional(() -> {
              List<Card> cardsToMoveBack = cardRepository.getAllCardsInLearnedBox(studySystem);
              for (Card c : cardsToMoveBack){
                  BoxToCard cardToBox = cardToBoxRepository.getSpecific(c,studySystem);
                  moveCardToBox(cardToBox,0,studySystem);
                  cardToBoxRepository.update(cardToBox);
              }
              return null;
          });
    }

    /**
     * Hilfsmethode, wird in finishTestAndGetResult aufgerufen, um den Fortschritt des Lernens zu berechnen.
     * Dabei wird für das Leitnersystem abhängig von der Box, in der die Karte ist, der Lernfortschritt errechnet.
     * @param studySystem Das StudySystem, für das der Progress errechnet werden soll.
     */
    public void calculateProgress(StudySystem studySystem) {
        int numCardsInToTal = getAllCardsInStudySystemToReturn(studySystem).size();
        int numOfLearnedCards = getAllCardsLearnedInStudySystem(studySystem);
        log.info("Anzahl aller gelernten Karten ist {}", numOfLearnedCards);
        log.info("Anzahl aller Karten ist {}", numCardsInToTal);

        if(studySystem.getType() == StudySystem.StudySystemType.LEITNER){
            List<Long> progressPerBox = studySystemRepository.getProgressForLeitner(studySystem);
            int sum = progressPerBox.stream().mapToInt(Long::intValue).sum();
            log.info("Summe des Progresses per Box ist: {}", sum);
           studySystem.setProgress(Math.round((double) sum / (numCardsInToTal * progressPerBox.size()) * 100 ));
           log.info("Progress Leinter ist {}", studySystem.getProgress());
        }
        else {
            studySystem.setProgress(Math.round((double) numOfLearnedCards / numCardsInToTal * 100));
        }
    }


    /**
     * Wird verwendet, um alle Karten in diesem Studiensystem zu erhalten. 
     * @param studySystem Das StudySystem, für das die Karten benötigt werden.
     * @return Liste von CardOverView, die zu StudySystem gehört
     */
    public List<CardOverview> getAllCardsInStudySystem(StudySystem studySystem) {
       return execTransactional(() -> getAllCardsInStudySystemToReturn(studySystem));
    }

    /**
     * Ruft für unterschiedliche Methoden die Query im CardRepo ab, die für ein Lernsystem
     * alle zugehörigen Karten wiedergibt.
     * @param studySystem Das StudySystem, für das die Karten benötigt werden.
     * @return Liste von CardOverView, die zu StudySystem gehört
     */
    public List<CardOverview> getAllCardsInStudySystemToReturn(StudySystem studySystem){
       return cardRepository.findCardsByStudySystem(studySystem);
    }

    /**
     * Gibt alle Karten, die den Status gelernt haben im StudySystem zurück.
     * Wird bei der progress Berechnung nach Beendigung des Tests verwendet.
     * @param studySystem StudySystem, für das der Progress berechnet werden soll
     * @return Anzahl der Karten mit Status LEARNED
     */
    public int getAllCardsLearnedInStudySystem(StudySystem studySystem) {

               return cardRepository.getNumberOfLearnedCardsByStudySystem(studySystem).intValue();

    }

    /**
     * Wird verwendet, Um die alle StudySystems zu bekommen. Wird an das StudySystemRepository weitergegeben.
     @return eine Liste von StudySystems
     */
    public List<StudySystem> getStudySystems(){ return execTransactional(() -> studySystemRepository.getStudySystems()); //Testet
    }


    /**
     * Wird verwendet, um das StudySystem zu updaten. .
     * @param oldStudySystem StudySystem im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newStudySystem Neue StudySystem Eigenschaften
     * @param neu Ist true, wenn das StudySystem neu angelegt wurde
     */
    public void updateStudySystemData(StudySystem oldStudySystem, StudySystem newStudySystem, boolean neu) {
        execTransactional(() -> {
            if (newStudySystem == null) {
                throw new IllegalArgumentException("New Study System can't be null");
            } else if (neu) {
                studySystemRepository.save(newStudySystem);
            } else if (oldStudySystem != null && !newStudySystem.getType().equals(oldStudySystem.getType())) {
                resetStudySystem(oldStudySystem, newStudySystem);
            } else {
                studySystemRepository.update(newStudySystem);
            }
            return true;
        });
    }



    /**
     * Wird aufgerufen, wenn ein StudySystem im EditModus geändert wurde und setzt das gesamte Deck zurück,
     * d.h. alle Karten werden wieder in Box 1 gepackt.
     */
    public void resetStudySystem(StudySystem oldStudyS, StudySystem newStudyS) {
            //first get all Cards for specific deck
            List<CardOverview> cardsToStudySystem = cardRepository.findCardsByStudySystem(oldStudyS);
            List<Card> cards = cardRepository.getAllCardsForCardOverview(cardsToStudySystem);
            //then move them to the other StudySystem
            moveAllCardsForDeckToFirstBoxNoExec(cards,newStudyS);
            deleteStudySystemNoExec(oldStudyS);

    }

    /**
     * Wird verwendet, um ein StudySystem zu löschen. .
     * @param studySystem: StudySystem zu löschen.
     */
    public void deleteStudySystem(StudySystem studySystem) {
        if(studySystem == null){
            throw new IllegalStateException("Karte existiert nicht");
        }
        execTransactional(() -> {
            log.info("Lösche alle Card To Boxes zum StudySystem");
            cardToBoxRepository.delete(cardToBoxRepository.getAllB2CForStudySystem(studySystem));
            log.info("Lösche StudySystem");
            studySystemRepository.delete(studySystem);
            return null;
        });
    }

    /**
     * Wird verwendet, um ein StudySystem zu löschen. .
     * @param studySystem: StudySystem zu löschen.
     */
    public void deleteStudySystemNoExec(StudySystem studySystem) {
        if(studySystem == null){
            throw new IllegalStateException("TODO");
        }
        log.info("Lösche alle Card To Boxes zum StudySystem");
        cardToBoxRepository.delete(cardToBoxRepository.getAllB2CForStudySystem(studySystem));
        log.info("Lösche StudySystem");
        studySystemRepository.delete(studySystem);

    }

    /**
     * Wird verwendet, um eine Liste von StudySystem zu löschen. .
     * @param studySystems: StudySysteme zu löschen.
     */
    public void deleteStudySystem(StudySystem[] studySystems) {
        for(StudySystem d : studySystems)
            deleteStudySystem(d);
    }

    /**
     * Wird verwendet, um eine Suche für StudySystem nach einem Suchbegiff machen.
     * @param searchterm: Suchbegriff um nach zu suchen.
     * @return eine Liste von StudySystem
     */
    public List<StudySystem> getStudySystemsBySearchterm(String searchterm) { // Testet
        checkNotNullOrBlank(searchterm, Locale.getCurrentLocale().getString("searchterm"),true);
        return execTransactional(() -> studySystemRepository.findStudySystemsContaining(searchterm));
    }

    /**
     * Wird verwendet, um eine Liste von Karten in einem StudySystem zu löschen.
     * @param studySystem: Das StudySystem, das benötigt wird.
     * @param cards: die Liste der Karten zu löschen
     */
    public void removeCardsFromStudySystem(List<CardOverview> cards, StudySystem studySystem) 
    {
        List<Card> cards1 = getCardsForCardOverview(cards);
        execTransactional(() -> {
            for(Card c: cards1)
                cardToBoxRepository.delete(cardToBoxRepository.getSpecific(c, studySystem));
            return null; // Lambda braucht immer einen return
        });
    }

    /**
     * Für nachträgliches Hinzufügen von Karten. 
     * @param cards: die Liste von Karten, um hinzufügen
     * @param studySystem Das StudySystem, das benötigt wird.
     */
    public List<Card> addCardsToDeck(List<CardOverview> cards, StudySystem studySystem) {
        if(studySystem == null){
            throw new IllegalStateException("Karte existiert nicht");
        }
            List<Card> cards1 = getCardsForCardOverview(cards);
            List<Card> existingCardsInStudySystem = moveAllCardsForDeckToFirstBox(cards1,studySystem);
            execTransactional(() -> {
           studySystemRepository.update(studySystem);
            return true; // Lambda braucht immer einen return
        });
            return existingCardsInStudySystem;
    }

    /**
     * Hilfsmethode, wird verwendet, um für die CardOverview Instanzen
     * die zugehörigen Karten wiederzugeben.
     * @param cards CardOverview Instanzen
     * @return Karten
     */

    public List<Card> getCardsForCardOverview(List<CardOverview>cards){ // Testet
       return execTransactional(() -> cardRepository.getAllCardsForCardOverview(cards));
    }

    /**
     * Wird verwendet, Um ein StudySystem nach UUID zu bekommen. Prüft zunächst auf nicht leere Werte.
     * Wird an das StudySystemRepository weitergegeben.
     * @param uuid: UUID des StudySystem
     * @return Zugehöriges StudySystem
     */
    public StudySystem getStudySystemByUUID(String uuid) { // Testet
        checkNotNullOrBlank(uuid, "UUID",true);
        return execTransactional(() -> studySystemRepository.getStudySystemByUUID(uuid));
    }

    /**
     * Wird verwendet, um die Anzahl der Karten in einem studySystem zu bekommen. 
     * @param studySystem: studySystem, um die Anzahl der Karten darin zu suchen
     * @return Anzahl der Karten
     */
    public Integer numCardsInDeck(StudySystem studySystem) { // Testet
          return getAllCardsInStudySystem(studySystem).size();
    }


    /**
     * Wird verwendet, um Box für ein Card zu bekommen. 
     * @param studySystem: Das StudySystem, das benötigt wird.
     * @param card: Für Box zu suchen
     * @return gefundenes BoxToCard
     */
    public BoxToCard getBoxToCard(Card card,  StudySystem studySystem) { // Testet
      return execTransactional(() ->  cardToBoxRepository.getSpecific(card, studySystem));
    }

    /**
     * Wird verwendet, wenn ein Nutzer beschließt seinen StudySystem Lernfortschritt zurückzusetzen.
     * @param studySystem das zurückzusetzende StudySystem
     */
    public void resetLearnStatus(StudySystem studySystem) {

        if (studySystem.getProgress() > 0) { //make sure action is needed
            execTransactional(() -> {
                List<CardOverview> cardsToMoveBack = getAllCardsInStudySystemToReturn(studySystem);
                for (Card c : cardRepository.getAllCardsForCardOverview(cardsToMoveBack)) {
                    BoxToCard cardToBox = cardToBoxRepository.getSpecific(c, studySystem);
                    cardToBox.setStatus(BoxToCard.CardStatus.NEW); //reset status
                    cardToBox.setRating(0); //reset rating
                    moveCardToBox(cardToBox, 0, studySystem);
                    cardToBoxRepository.update(cardToBox);
                }
                studySystem.setProgress(0); //reset progress
                studySystem.setNotLearnedYet(true); //handle as new
                studySystemRepository.update(studySystem);

                return null;
            });
        }
        else{
            log.info("Kein Zurücksetzen notwendig, da Fortschritt 0 ist");
        }
    }
}

