package com.swp.Logic;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToBoxRepository;
import com.swp.Persistence.StudySystemRepository;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.swp.Validator.checkNotNullOrBlank;

@Slf4j
public class StudySystemLogic extends BaseLogic<StudySystem>{



    public StudySystemLogic() {
        super(StudySystemRepository.getInstance());
    }

    private static StudySystemLogic studySystemLogic;
    public static StudySystemLogic getInstance() {
        if (studySystemLogic == null)
            studySystemLogic = new StudySystemLogic();
        return studySystemLogic;
    }



    public List<Card> testingBoxCards = new ArrayList<>(); //All current cards that need to be learned



    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems. 
     * @param cardToBox: Zu verschiebene Karte
     * @param newBox: Index der Box, in den die Karte verschoben werden soll
     * @param studySystem Das StudySystem, das benötigt wird.
     */
    public void moveCardToBox(BoxToCard cardToBox, int newBox, StudySystem studySystem) //Testet
    {
                cardToBox.setBoxNumber(newBox);
                cardToBox.setStudySystemBox(studySystem.getBoxes().get(newBox));
    }

    /**
     * Verschiebt und speichert spezifische Karte in eine Box des StudySystems.
     * @param card: Zu verschiebene Karte
     * @param newBox: Index der Box, in den die Karte verschoben werden soll
     * @param studySystem: Das StudySystem, das benötigt wird.
     */
    public void moveCardToBoxAndSave(Card card, int newBox, StudySystem studySystem) //Testet
    {
        BoxToCard boxToCard = new BoxToCard(card, studySystem.getBoxes().get(newBox), newBox);
        cardToBoxRepository.save(boxToCard);

    }


    /**
     * Wird nach der Erstellung eines neuen StudySystem verwendet und verschiebt alle Karten für das StudySystem in das erste Box.
     * Vorher wird geprüft, ob die Karte bereits im StudySystem enthalten ist, wenn ja, wird sie nicht nochmal hinzugefügt und das GUI
     * zeigt eine Sammlung aller Karten an, die nciht hinzugefügt worden sind.
     * @param cards: Karten, die StudySystem enthalten soll.
     * @param studySystem: Das StudySystem, das benötigt wird.
     */
    public List<Card> moveAllCardsForDeckToFirstBox(List<Card> cards, StudySystem studySystem) {
        return execTransactional(() -> {
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
        });
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, so dass
     * je nach Antwort die Karte in den Boxen verschoben werden kann (Leitner System) oder
     * die Karte aktualisiert wird je nach Antwort.
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param answer : Frage war richtig / falsch beantwortet
     */
    public void giveAnswer(StudySystem studySystem,boolean answer) { //Testet
        log.info("Antwort wird in DB gespeichert");
        //setze Karte auf gelernt und entferne sie aus der testingBoxCards
        studySystem.incrementQuestionCount(); //Karte gelernt
        Card cardLearned = testingBoxCards.get(0); //Zieh dir aktuelle Karte
        testingBoxCards.remove(cardLearned); //entferne Karte aus dem testingBoxCards (wenn Probleme, dann erst danach)
        BoxToCard boxToCard = getBoxToCard(cardLearned, studySystem);

            if (studySystem.getType() == StudySystem.StudySystemType.LEITNER) {
                int box = boxToCard.getBoxNumber(); //needed for checks and movement in Leitner system
                if (answer) {
                    studySystem.incrementTrueCount(); //Setze TrueCount to 1
                    if (box < studySystem.getBoxes().size() - 1) { //avoid index out of bound exception, adapted to new Leitner Method
                        ++box;
                        moveCardToBox(boxToCard, box, studySystem);
                        changeCardDueDate(boxToCard);
                    } else {
                        changeCardDueDate(boxToCard);
                        //nada mas
                    }
                }
                else { //answer was false, set card to new box
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
                boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);
            }
            else {
                if (answer) {
                    studySystem.incrementTrueCount();
                    boxToCard.setStatus(BoxToCard.CardStatus.LEARNED);
                    //Karte fällt aus aktueller Liste raus, kein Relearn //TODO ggfs. aufschlüsseln?
                }
                else{
                    boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);
                    testingBoxCards.add(cardLearned); //Karte muss erneut angeschaut werden
                }
            }
            execTransactional(() -> { //DB action starts here
            studySystemRepository.update(studySystem);
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
    }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @param studySystem: Das StudySystem, das benötigt wird.
     * @return Karte die als nächstes gelernt werden soll
     */
    public Card getNextCard(StudySystem studySystem){
        //TODO: initiale Reihenfolge der Karten fehlt
        //TODO: Endlosschleife aktuell, muss noch angepasst werden
        return execTransactional(() -> {
        if(testingBoxCards.isEmpty()) {
            log.info("Rufe Karten für Lernsystem ab");
                switch (studySystem.getType()) {
                    case CUSTOM:
                    case TIMING:
                        testingBoxCards = cardRepository.getAllCardsForTimingSystem(studySystem);
                    case LEITNER:
                        testingBoxCards = cardRepository.getAllCardsNeededToBeLearned(studySystem);
                    case VOTE:
                        testingBoxCards = cardRepository.getAllCardsSortedForVoteSystem(studySystem);

                }
                if (testingBoxCards.isEmpty()) { //falls immernoch empty nach dem Ziehen, dann gib das an das GUI weiter
                    log.info("Keine Karten für Lernsystem gefunden");
                    return null;
                    //TODO check if it works that way, currently out of index exception
                }
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
     * @param card
     * @param rating      : Bewertung von GUI
     */
    public void giveRating(StudySystem studySystem, Card card, int rating) {
        if(studySystem.getType() == StudySystem.StudySystemType.VOTE){
            BoxToCard boxToCard = getBoxToCard(card,studySystem);
            boxToCard.setRating(rating);
            updateCardToBox(boxToCard);
        }
        else{
            throw new IllegalStateException("Falsches StudySystem");
        }
    };

    public void updateCardToBox(BoxToCard cardToBox){
        execTransactional(() -> cardToBoxRepository.update(cardToBox));
    }

    public void updateStudySystem(StudySystem studySystem){
        execTransactional(() -> studySystemRepository.update(studySystem));
    }

    /*
    //GIb mir all Karten für das VoteSystem
    public List <Card> getAllCardsSortedForVoteSystem(StudySystem studySystem) {
        //testingBoxCards
      return cardRepository.getAllCardsSortedForVoteSystem(studySystem);
    }
    Do we need this here?
     */

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
                updateStudySystem(timingSystem);
            }
            else {
                throw new IllegalStateException("Falsches StudySystem");
            }
        }
    };

    /**
     * Wird verwendet, um den Test zu beenden und Ergebnispunkt zu berechnen
     * 
     * @param studySystem Das StudySystem, das benötigt wird.
     * @return ResultPoint von der Funktion calculateResultAndSave bekommen wird
     */
    public int finishTestAndGetResult(StudySystem studySystem) { // Testet
        //2 Szenarios
        //Szenario 1: nothing was learned
        int result = 0;
        if(studySystem.getTrueAnswerCount() == 0 || studySystem.getQuestionCount() == 0) {
            testingBoxCards.removeAll(testingBoxCards);
            return result;
        }
        //everything else
        else{
            int numofCardsInTotal =numCardsInDeck(studySystem);
            int numOfLearnedCards = getAllCardsLearnedInStudySystem(studySystem);
            double progress = studySystem.getProgress();

                result = (100 / studySystem.getQuestionCount() * studySystem.getTrueAnswerCount());
                studySystem.setProgress(Math.round((double) numOfLearnedCards /numofCardsInTotal *  100));

            if(!testingBoxCards.isEmpty()){
                testingBoxCards.removeAll(testingBoxCards); //remove for a fresh start
            }
        //reset variables for next learning
        studySystem.setTrueAnswerCount(0);
        studySystem.setQuestionCount(0);
        }


        updateStudySystem(studySystem); //save in Database (!)
        return result; //local variable, why should we save it in Database as we only need it for GUI
    }


    /**
     * Wird verwendet, um alle Karten in diesem Studiensystem zu erhalten. 
     * @param studySystem Das StudySystem, das benötigt wird.
     * @return Liste von CardOverView, die zu StudySystem gehört
     */
    public List<CardOverview> getAllCardsInStudySystem(StudySystem studySystem) { //Testet
       return  execTransactional(() -> cardRepository.findCardsByStudySystem(studySystem));
    }

    public int getAllCardsLearnedInStudySystem(StudySystem studySystem) {
        return  execTransactional(() -> {
                try{ return (cardRepository.getNumberOfLearnedCardsByStudySystem(studySystem)).intValue();}
                catch ( NoResultException ex){
                    return 0;
                }
        });
    }

    /**
     * Wird verwendet, Um die alle StudySystems zu bekommen. Wird an das StudySystemRepository weitergegeben.
     @return eine Liste von StudySystems
     */
    public List<StudySystem> getStudySystems(){ return execTransactional(() -> studySystemRepository.getStudySystems()); //Testet
    }

    /**
     * Wird verwendet, Um StudySystem zu updaten und hinzufügen. Wird an das StudySystemRepository weitergegeben.
     @param type: StudySystem Type zu updaten und hinzufügen
     */
    public void addStudySystemTypeAndUpdate(StudySystem.StudySystemType type) { // No Need to Test
        execTransactional(() -> {
            studySystemRepository.addStudySystemType(type);
            studySystemRepository.updateStudySystemTypes();
            return null;
        });
    }

    /**
     * Wird verwendet, um das StudySystem zu updaten. .
     * @param oldStudySystem StudySystem im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newStudySystem Neue StudySystem Eigenschaften
     * @param neu Ist true, wenn das StudySystem neu angelegt wurde
     */
    public void updateStudySystemData(StudySystem oldStudySystem, StudySystem newStudySystem, boolean neu) { //Testet Half
        execTransactional(() -> {
            if (newStudySystem == null) {
                throw new IllegalArgumentException("New Study System can't be null");
            } else if (neu) {
                studySystemRepository.save(newStudySystem);
            } else if (oldStudySystem != null && newStudySystem.getType().equals(oldStudySystem.getType())) {
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
    private void resetStudySystem(StudySystem oldStudyS, StudySystem newStudyS) {
        execTransactional(() -> {
            //first get all Cards for specific deck
            List<CardOverview> cardsToStudySystem = cardRepository.findCardsByStudySystem(oldStudyS);
            List<Card> cards = cardRepository.getAllCardsForCardOverview(cardsToStudySystem);
            //then move them to the other StudySystem
            moveAllCardsForDeckToFirstBox(cards,newStudyS);
            deleteStudySystem(oldStudyS);
            return true;
        });
    }

    /**
     * Wird verwendet, um ein StudySystem zu löschen. .
     * @param studySystem: StudySystem zu löschen.
     */
    public void deleteStudySystem(StudySystem studySystem) { // Testet
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
     * Wird verwendet, um eine Liste von StudySystem zu löschen. .
     * @param studySystems: StudySysteme zu löschen.
     */
    public void deleteStudySystem(StudySystem[] studySystems) { // Testet
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
     * Wird verwendet, um eine Liste von Karten in einem StudySystem zu löschen. .
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
        //TODO Prüfung, ob Karte schon in Deck enthalten, dann throw Exception und abfangen im Controller @Mert
            List<Card> cards1 = getCardsForCardOverview(cards);
            List<Card> existingCardsInStudySystem = moveAllCardsForDeckToFirstBox(cards1,studySystem);
            execTransactional(() -> {
           studySystemRepository.update(studySystem);
            return true; // Lambda braucht immer einen return
        });
            return existingCardsInStudySystem;
    }

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


}

