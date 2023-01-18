package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.Persistence.StudySystemRepository;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    public List<Card> getAllCardsInStudySystem() {
        return execTransactional(() -> studySystemRepository.getAllCardsInStudySystem());
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
                    studySystemRepository.setTrueCount(studySystemRepository.getTrueCount() + 1);
                }
                else{
                    if(studySystemRepository.getQuestionCount() > 0){
                        studySystemRepository.setQuestionCount(studySystemRepository.getQuestionCount() - 2);
                    }
                }
            case TIMING:
            case VOTE:
                if(answer){
                    studySystemRepository.setTrueCount(studySystemRepository.getTrueCount() + 1);
                }

        }
    }

    //TO IMPLEMENT
    public void giveRating(StudySystem studySystem,int rating) {
        //TODO
    };

    //TO IMPLEMENT
    public void giveTime(StudySystem studySystem,float seconds) {
        if(studySystem.getType() == StudySystem.StudySystemType.TIMING){
            if(seconds > studySystemRepository.getTimeLimit()){
                studySystemRepository.setTrueCount(studySystemRepository.getTrueCount()-1);
            }
        }
    };

    //TO IMPLEMENT (is also called when the test has been canceled)
    public void finishTest(StudySystem studySystem) {
        if(studySystem.getType() == StudySystem.StudySystemType.LEITNER){
            //TODO
        }
        else{
            studySystemRepository.saveProgress(getProgress());
            studySystemRepository.setQuestionCount(0);
            studySystemRepository.setTrueCount(0);
        }
    }

    //TO IMPLEMENT (returns final score calculated in finishTest)
    public int getResult() {
        if(!studySystemRepository.getAllCardsInStudySystem().isEmpty()){
            return (100 / studySystemRepository.getAllCardsInStudySystem().size()) *  studySystemRepository.getTrueCount();
        }
        else {
            return 0;
        }
    }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @return Karte die als nächstes gelernt werden soll
     */
    public Card getNextCard(){
        studySystemRepository.setQuestionCount(studySystemRepository.getQuestionCount()+1);
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
    public float getProgress()
    {
        if(studySystemRepository.getAllCardsInStudySystem().size() > 0){
            return studySystemRepository.getTrueCount() / studySystemRepository.getAllCardsInStudySystem().size();
        }
        else{
            return 0;
        }
    }
}

