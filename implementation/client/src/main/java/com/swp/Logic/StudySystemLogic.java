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

    List<Card> testingBoxCards; //All current cards that need to be learned

    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems. 
     * @param cardToBox: Zu verschiebene Karte
     * @param newBox: Index der Box, in den die Karte verschoben werden soll
     * @param studySystem Das StudySystem, das benötigt wird.
     */
    public void moveCardToBox(BoxToCard cardToBox, int newBox, StudySystem studySystem)
    {
        execTransactional(() -> {
                cardToBox.setBoxNumber(newBox);
                cardToBox.setStudySystemBox(studySystem.getBoxes().get(newBox));
                cardToBoxRepository.update(cardToBox);
            return null;
            });
    }

    /**
     * Verschiebt und speichert spezifische Karte in eine Box des StudySystems. 
     * @param card: Zu verschiebene Karte
     * @param newBox: Index der Box, in den die Karte verschoben werden soll
     * @param studySystem: Das StudySystem, das benötigt wird.
     */
    public void moveCardToBoxAndSave(Card card, int newBox, StudySystem studySystem)
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
     * je nach Antwort die Karte in den Boxen verschoben werden kann
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param answer : Frage war richtig / falsch beantwortet
     */
    public void giveAnswer(StudySystem studySystem,boolean answer) {
        switch (studySystem.getType()){
            case LEITNER:
                BoxToCard boxToCard = getBoxToCard(testingBoxCards.get(0),studySystem);
                testingBoxCards.remove(0); //schmeiß die Karte raus
                if(answer){
                    studySystem.incrementTrueCount(); //wofür - to show resultpoint end of the test
                   /*
                        Folgendes Handling muss hier berücksichtigt werden:
                        Die Karte wird eine Box nach hinten verschoben, nextLearnTime muss gesetzt werde

                    */
                    int box = boxToCard.getBoxNumber();
                    if(box < 4) { //avoid index out of bound exception
                        ++box;
                        changeCardDueDate(box, boxToCard);
                        moveCardToBox(boxToCard, box, studySystem);
                    }
                    else{
                        changeCardDueDate(box,boxToCard);
                        //cardToBoxRepository.update(boxToCard); // Needed or auto?
                    }
                }
                else{
                    int box = boxToCard.getBoxNumber();
                    if(box >0) { //avoid index out of bound exception
                        --box;
                        changeCardDueDate(box, boxToCard);
                        moveCardToBox(boxToCard, box, studySystem);
                    }
                    else{
                        changeCardDueDate(box,boxToCard);
                        //cardToBoxRepository.update(boxToCard); // Needed or auto?
                    }
                    }
            case TIMING:
            case VOTE:
            case CUSTOM:
                if(answer){
                    studySystem.incrementTrueCount();
                }
        }
    }

    /**
     * Wird verwendet, um nächste Lernzeit einer Frage für LeitnerSystem zu ändern
     * @param box neues Box
     * @param boxToCard : Um Box und Card zusammen zu speichern
     */
    private void changeCardDueDate(int box, BoxToCard boxToCard) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Timestamp(System.currentTimeMillis()));
            cal.add(Calendar.DAY_OF_YEAR,boxToCard.getStudySystemBox().getDaysToLearnAgain());
            boxToCard.setLearnedNextAt(new Timestamp(cal.getTimeInMillis()));
    }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @param studySystem: Das StudySystem, das benötigt wird.
     * @return Karte die als nächstes gelernt werden soll
     */
    public Card getNextCard(StudySystem studySystem){
        //je nach Lernsystem ausgeben
       //first off all gib mir alle Karten, die zu lernen sind aktuell, Logik siehe Repo-funktion
        if(testingBoxCards.isEmpty()){
            switch (studySystem.getType()){
                case CUSTOM:
                case TIMING:
                    testingBoxCards = cardRepository.getAllCardsForTimingSystem(studySystem);
                case LEITNER:
                    testingBoxCards = cardRepository.getAllCardsNeededToBeLearned(studySystem);
                case VOTE:
                    testingBoxCards = cardRepository.getAllCardsSortedForVoteSystem(studySystem);

            }
            if(testingBoxCards.isEmpty()){ //falls immernoch empty nach dem Ziehen, dann gib das an das GUI weiter
                return null;}

        }
        Card cardToLearn = testingBoxCards.get(studySystem.getQuestionCount()); //gib mir die vorderste
        // QuestionCount am Anfang ist immer 0
        studySystem.incrementQuestionCount(); //Karte gelernt
        return cardToLearn;
    }


    /**
     * Wird verwendet, um eine Bewertung vom Benutzer für VoteStudySystem zu bekommen.
     * 
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param rating: Bewertung von GUI
     */
    public void giveRating(StudySystem studySystem,int rating) {
        getBoxToCard(testingBoxCards.get(studySystem.getQuestionCount()),studySystem).setRating(rating);
    };

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
     * @param seconds: Antwortzeit vom Benutzer für die Frage
     */
    public void giveTime(StudySystem studySystem,float seconds) {
        if(studySystem.getType() == StudySystem.StudySystemType.TIMING){
            TimingSystem timingSystem = (TimingSystem) studySystem;
            if(seconds > timingSystem.getTimeLimit()){
                studySystem.setTrueAnswerCount(studySystem.getTrueAnswerCount()-1);
                //TODO: vielleicht in Box2Card speichern zur einzelnen Karte?
            }
        }
    };

    /**
     * Wird verwendet, um den Test zu beenden und Ergebnispunkt zu berechnen
     * 
     * @param studySystem Das StudySystem, das benötigt wird.
     * @return ResultPoint von der Funktion calculateResultAndSave bekommen wird
     */
    public int finishTestAndGetResult(StudySystem studySystem) {

        if(!testingBoxCards.isEmpty()) {
            if (studySystem.getQuestionCount() == testingBoxCards.size() - 1) {
                int resultPoint = (100 / testingBoxCards.size()) * studySystem.getTrueAnswerCount();
                int progress = studySystem.getTrueAnswerCount() / testingBoxCards.size();
                studySystem.setProgress(progress);
                studySystem.setResultPoint(resultPoint);
                studySystem.setTrueAnswerCount(0);
                studySystem.setQuestionCount(0);
            } else {
                int resultPoint = (100 / studySystem.getQuestionCount()) * studySystem.getTrueAnswerCount();
                int progress = studySystem.getTrueAnswerCount() / testingBoxCards.size();
                studySystem.setProgress(progress);
                studySystem.setResultPoint(resultPoint);
            }
            return calculateResultAndSave(studySystem);
        }
        else {
            throw new NoResultException("Es wurden keine Karten gelernt"); //Abfangen in Controller und als Info ausspielen
        }

    }

    /**
     * Wird verwendet, um am Ende der Test Liste der Karten leer zu machen und Database zu aktualisieren
     * 
     * @param studySystem Das StudySystem, das benötigt wird.
     * @return ResultPoint
     */
    public int calculateResultAndSave(StudySystem studySystem) {
        //TODO truecount verwenden, in Zsm.hang mit dem questionCount
        //TODO wollen wir das Result immer neu berechnen, also nur für den Tag ausgeben? Ansonsten muss man das noch aufschlüsseln
        testingBoxCards.removeAll(testingBoxCards);
        studySystemRepository.update(studySystem);
        return studySystem.getResultPoint();
    }


    /**
     * Wird verwendet, um alle Karten in diesem Studiensystem zu erhalten. 
     * @param studySystem Das StudySystem, das benötigt wird.
     * @return Liste von CardOverView, die zu StudySystem gehört
     */
    public List<CardOverview> getAllCardsInStudySystem(StudySystem studySystem) {
       return  execTransactional(() -> cardRepository.findCardsByStudySystem(studySystem));
    }



    /**
     * Wird verwendet, um Progress für dieses Deck zu bekommen.
     * 
     * @param studySystem Das StudySystem, das benötigt wird.
     * @return Progress für gegebenes StudySystem
     */
    public float getProgress(StudySystem studySystem)
    {
        return studySystem.getProgress();
    }


    /**
     * Wird verwendet, Um die alle StudySystems zu bekommen. Wird an das StudySystemRepository weitergegeben.
     @return eine Liste von StudySystems
     */
    public List<StudySystem> getStudySystems(){ return execTransactional(() -> studySystemRepository.getStudySystems());
    }

    /**
     * Wird verwendet, Um StudySystem zu updaten und hinzufügen. Wird an das StudySystemRepository weitergegeben.
     @param type: StudySystem Type zu updaten und hinzufügen
     */
    public void addStudySystemTypeAndUpdate(StudySystem.StudySystemType type) {
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
    public void updateStudySystemData(StudySystem oldStudySystem, StudySystem newStudySystem, boolean neu) {
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
            //then move them to the other StudySystem
            // newStudyS.moveAllCardsForDeckToFirstBox(cardsToDeck);
            return true;
        });
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
     * Wird verwendet, um eine Liste von StudySystem zu löschen. .
     * @param studySystems: StudySysteme zu löschen.
     */
    public void deleteStudySystem(StudySystem[] studySystems) {
        for(StudySystem d : studySystems)
            deleteStudySystem(d);
    }

    public void createBoxToCardForCategory(Category category, StudySystem studySystem) {
            //TODO
    }

    /**
     * Wird verwendet, um eine Suche für StudySystem nach einem Suchbegiff machen.
     * @param searchterm: Suchbegriff um nach zu suchen.
     * @return eine Liste von StudySystem
     */
    public List<StudySystem> getStudySystemsBySearchterm(String searchterm) {
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

    public List<Card> getCardsForCardOverview(List<CardOverview>cards){
       return execTransactional(() -> cardRepository.getAllCardsForCardOverview(cards));
    }

    /**
     * Wird verwendet, Um ein StudySystem nach UUID zu bekommen. Prüft zunächst auf nicht leere Werte.
     * Wird an das StudySystemRepository weitergegeben.
     * @param uuid: UUID des StudySystem
     * @return Zugehöriges StudySystem
     */
    public StudySystem getStudySystemByUUID(String uuid) {
        checkNotNullOrBlank(uuid, "UUID",true);
        return execTransactional(() -> studySystemRepository.getStudySystemByUUID(uuid));
    }

    /**
     * Wird verwendet, um die Anzahl der Karten in einem studySystem zu bekommen. 
     * @param studySystem: studySystem, um die Anzahl der Karten darin zu suchen
     * @return Anzahl der Karten
     */
    public Integer numCardsInDeck(StudySystem studySystem) {
        return getAllCardsInStudySystem(studySystem).size();
    }


    /**
     * Wird verwendet, um Box für ein Card zu bekommen. 
     * @param studySystem: Das StudySystem, das benötigt wird.
     * @param card: Für Box zu suchen
     * @return gefundenes BoxToCard
     */
    public BoxToCard getBoxToCard(Card card,  StudySystem studySystem) {
      return execTransactional(() ->  cardToBoxRepository.getSpecific(card, studySystem));
    }

    /**
     * Wird verwendet, um ein Card in einem Box zu speichern. 
     * @param studySystem: Das StudySystem, das benötigt wird.
     * @param card: Um zu speichern
     * @param box: Box wo wird Card gespeichert
     */
    public void moveCardToBox(Card card, int box, StudySystem studySystem) {
        BoxToCard boxToCard = getBoxToCard(card,studySystem);
        moveCardToBox(boxToCard,box,studySystem);
    }
}

