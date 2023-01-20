package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToBoxRepository;
import com.swp.Persistence.StudySystemRepository;
import jakarta.persistence.NoResultException;

import java.util.List;

import static com.swp.Validator.checkNotNullOrBlank;

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

    List<Card> testingBoxCards; //All Cards in Current Box that need to be learned

    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems
     *
     *
     * @param card: Zu verschiebene Karte
     * @param newBox: Index der Box, in den die Karte verschoben werden soll
     */
    public void moveCardToBox(Card card, StudySystemBox newBox, StudySystem studySystem)
    {
        execTransactional(() -> {
            BoxToCard boxToCard;
            try {
                boxToCard = cardToBoxRepository.getSpecific(card, studySystem);
                boxToCard.setStudySystemBox(newBox);
                cardToBoxRepository.update(boxToCard);
            } catch (NoResultException ex) {
                boxToCard = new BoxToCard(card, newBox);
                //save new BoxToCard
                cardToBoxRepository.save(boxToCard);
            }
            return null;
        });

    }


    public void moveAllCardsForDeckToFirstBox(List<Card> cards, StudySystem studySystem) {
        for(Card c : cards)
            moveCardToBox(c,studySystem.getBoxes().get(0),studySystem);
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, so dass
     * je nach Antwort die Karte in den Boxen verschoben werden kann
     * @param answer: Frage war richtig / falsch beantwortet
     */
    public void giveAnswer(StudySystem studySystem,boolean answer) {
        switch (studySystem.getType()){
            case LEITNER:
                if(answer){
                    //TODO
                    studySystem.incrementTrueCount();
                    //studySystemRepository.setTrueCount(studySystemRepository.getTrueCount() + 1);
                }
                else{
                    if(studySystem.getQuestionCount() > 0){
                        studySystem.setQuestionCount(studySystem.getQuestionCount() -2); //warum?
                        //TODO movetoanotherbox
                    }
                    }
            case TIMING:
            case VOTE:
                if(answer){
                studySystem.incrementTrueCount();
                }

        }
    }

    //TO IMPLEMENT
    public void giveRating(StudySystem studySystem,int rating) {
        //TODO wofür?
    };

    //TO IMPLEMENT
    public void giveTime(StudySystem studySystem,float seconds) {
        if(studySystem.getType() == StudySystem.StudySystemType.TIMING){
            TimingSystem timingSystem = (TimingSystem) studySystem;
            if(seconds > timingSystem.getTimeLimit()){
                timingSystem.setTrueCount(timingSystem.getTrueCount()-1);
                //TODO: vielleicht in Box2Card speichern zu einzelnen Karte?
            }
        }
    };

    //TO IMPLEMENT (is also called when the test has been canceled)
    public int finishTestAndGetResult(StudySystem studySystem) {
        if(studySystem.getType() == StudySystem.StudySystemType.LEITNER){
            //TODO
        }
        else{
            //TODO weitere Sachen hier @Mert
            //studySystem.saveProgress(getProgress()); //hier fehlt noch ein Attribut für den Progress, wherever Logic / persistence
            studySystem.setQuestionCount(0);
           // studySystem.setTrueCount(0);
        }
        //
        return calculateResultAndSave(studySystem);
    }

    //TO IMPLEMENT (returns final score calculated in finishTest)
    public int calculateResultAndSave(StudySystem studySystem) {
        List<Card> cards = getAllCardsInStudySystem(studySystem);
        if(!cards.isEmpty()){
            return (100 / cards.size());
            //*  studySystemRepository.getTrueCount(); TODO speichern?
        }
        else {
            throw new NoResultException("Es wurden keine Karten gelernt"); //Abfangen in Controller und als Info ausspielen
        }
    }

    public List<Card> getAllCardsInStudySystem(StudySystem studySystem) {
       return   execTransactional(() -> cardRepository.findCardsByStudySystem(studySystem));
    }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @return Karte die als nächstes gelernt werden soll
     */
    public Card getNextCard(StudySystem studySystem){
        if(testingBoxCards.isEmpty()){}
           // testingBoxCards = cardRepository.getAllCardsForBox(studySystem.getBoxes().get(box), studySystem.getCardOrder());


            //Card cardToLearn = testingBoxCards.get(0);
           // testingBoxCards.remove(cardToLearn);
            //studySystem.incrementQuestionCount();
           // return cardToLearn;


            //throw new IllegalStateException("Keine Karten mehr in Box enthalten");
            //TODO take all of the box and get the next one?


return null;

    }


    public float getProgress(StudySystem studySystem)
    {
//        float progress = 0;
//        execTransactional(() -> {
//
//        if(studySystemRepository.getAllCardsInStudySystem(studySystem).size() > 0){
//            progress = 0 ;//TODO  studySystemRepository.getTrueCount() / studySystemRepository.getAllCardsInStudySystem().size();
//        }
//        else{
//            progress = 0;
//        }
//            return null;
//        });
//       return progress;
        return 0;
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

    public void updateStudySystemData(StudySystem oldStudySystem, StudySystem newStudySystem, boolean neu) {
        execTransactional(() -> {
            if(neu) {
                studySystemRepository.save(newStudySystem);
            }

            else if(!neu && newStudySystem.getType().equals(oldStudySystem.getType()))
                resetStudySystem(oldStudySystem,newStudySystem);

            else
                studySystemRepository.update(newStudySystem);
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
            List<Card> cardsToStudySystem = cardRepository.findCardsByStudySystem(oldStudyS);
            //then move them to the other StudySystem
            // newStudyS.moveAllCardsForDeckToFirstBox(cardsToDeck);
            return true;
        });
    }

    public void deleteStudySystem(StudySystem studySystem) {
        if(studySystem == null){
            throw new IllegalStateException("Karte existiert nicht");
        }
        execTransactional(() -> {
            //TODO: Card2Box Removal
            studySystemRepository.delete(studySystem);
            return null;
        });
    }

    public void deleteStudySystem(StudySystem[] studySystems) {
        for(StudySystem d : studySystems)
            deleteStudySystem(d);
    }

    public void createBoxToCardForCategory(Category category, StudySystem studySystem) {
            //TODO
    }

    public List<StudySystem> getStudySystemsBySearchterm(String searchterm) {
        checkNotNullOrBlank(searchterm,"Suchbegriff",true);
        return execTransactional(() -> studySystemRepository.findStudySystemsContaining(searchterm));
    }

    public void removeCardsFromStudySystem(List<Card> cards, StudySystem studySystem) {
        execTransactional(() -> {
            for(Card c : cards) {
                cardToBoxRepository
                        .delete(cardToBoxRepository.getSpecific(c, studySystem));
            }
            return null; // Lambda braucht immer einen return
        });
    }

    public void addCardsToDeck(List<Card> cards, StudySystem studySystem) {
        if(studySystem == null){
            throw new IllegalStateException("Karte existiert nicht");
        }
        execTransactional(() -> {
            moveAllCardsForDeckToFirstBox(cards,studySystem);
            studySystemRepository.update(studySystem);
            for (Card c : cards) {
                cardToBoxRepository.createCardToBox(c, studySystem.getBoxes().get(0));
            }
            return null; // Lambda braucht immer einen return
        });
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

    public Integer numCardsInDeck(StudySystem deck) {
        return getAllCardsInStudySystem(deck).size();
    }
}

