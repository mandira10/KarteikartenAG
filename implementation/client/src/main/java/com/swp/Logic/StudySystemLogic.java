package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.Persistence.StudySystemRepository;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.swp.DataModel.StudySystem.StudySystem.StudySystemType.TIMING;

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

    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems
     *
     *
     * @param card        : Zu verschiebene Karte
     * @param box    : Index der Box, in den die Karte verschoben werden soll
     */
    public void moveCardToBox(Card card, StudySystemBox box)
    {
        BoxToCard boxToCard = new BoxToCard(card,box);
        studySystemRepository.addCardToBox(boxToCard);
    }

    public void moveAllCardsForDeckToBox(List<Card> cards,StudySystemBox box) {
        for(Card c : cards){
            moveCardToBox(c,box);
        }
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
                    //studySystemRepository.setTrueCount(studySystemRepository.getTrueCount() + 1);
                }
                else{
                    //if(studySystemRepository.getQuestionCount() > 0){
                      //  studySystemRepository.setQuestionCount(studySystemRepository.getQuestionCount() - 2);
                    }
                }
           // case TIMING:
           // case VOTE:
               // if(answer){
                   // studySystemRepository.setTrueCount(studySystemRepository.getTrueCount() + 1);
             //   }

      //  }
    }

    //TO IMPLEMENT
    public void giveRating(StudySystem studySystem,int rating) {
        //TODO
    };

    //TO IMPLEMENT
    public void giveTime(StudySystem studySystem,float seconds) {
        if(studySystem.getType() == StudySystem.StudySystemType.TIMING){
            TimingSystem timingSystem = (TimingSystem) studySystem;
            if(seconds > timingSystem.getTimeLimit()){
                timingSystem.setTrueCount(timingSystem.getTrueCount()-1);
            }
        }
    };

    //TO IMPLEMENT (is also called when the test has been canceled)
    public void finishTest(StudySystem studySystem) {
        if(studySystem.getType() == StudySystem.StudySystemType.LEITNER){
            //TODO
        }
        else{
            //studySystem.saveProgress(getProgress()); //hier fehlt noch ein Attribut für den Progress, wherever Logic / persistence
            studySystem.setQuestionCount(0);
           // studySystem.setTrueCount(0);
        }
    }

    //TO IMPLEMENT (returns final score calculated in finishTest)
    public int getResult(StudySystem studySystem) {
        List<Card> cards = getAllCardsInStudySystem(studySystem);
        if(!cards.isEmpty()){
            return (100 / cards.size()); //*  studySystemRepository.getTrueCount(); TODO
        }
        else {
            return 0;
        }
    }

    public List<Card> getAllCardsInStudySystem(StudySystem studySystem) {
       return studySystemRepository.getAllCardsInStudySystem(studySystem);
    }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @return Karte die als nächstes gelernt werden soll
     */
    public Card getNextCard(StudySystem studySystem){
        //take all of the box and get the next one?
       //TODO studySystemRepository.setQuestionCount(studySystemRepository.getQuestionCount()+1);
        return studySystemRepository.getNextCard();
    }
//    {
//        for (StudySystemBox box : boxes) {
//            if (!box.getBoxContent().isEmpty()){
//                return box.getBoxContent().iterator().next();
//            }
//        }
//        throw new NoResultException("No Cards in StudySystem");
//    }

    //NEEDS TO BE IMPLEMENTED
    public float getProgress(StudySystem studySystem)
    {
        if(studySystemRepository.getAllCardsInStudySystem(studySystem).size() > 0){
            return 0 ;//TODO  studySystemRepository.getTrueCount() / studySystemRepository.getAllCardsInStudySystem().size();
        }
        else{
            return 0;
        }
    }
}

