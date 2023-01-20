package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Klasse für das VoteSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@Getter
@Setter
@DiscriminatorValue("Vote")
public class VoteSystem extends StudySystem
{

    /**
     * Konstruktor der Klasse VoteSystem.
     * TODO
     */
    public VoteSystem(String name, CardOrder cardOrder, boolean visibility) {
        super(name, cardOrder, StudySystemType.VOTE, visibility);
        initStudySystemBoxes(3);
    }

    @Override
    protected void initStudySystemBoxes(int size) {
        List<Integer> daysToLearnAgain = Arrays.asList(new Integer[]{1,3,7}); //hardcoded not ideal
        for (int i = 0; i < size; i++)
            this.boxes.add(new StudySystemBox(this,daysToLearnAgain.get(i)));
    }

    public VoteSystem(VoteSystem other) {
        super(other);
    }

    public VoteSystem() {
       //
    }

//    @Override
//    public void giveAnswer(boolean answer) {
//        if(answer) {
//            trueAnswerCount++;
//        }
//    }
//
//
//    @Override
//    public void giveRating(int rating) {
//        // TODO: fix undefined `unsortedCards`
//        //unsortedCards.put(getAllCardsInStudySystem().stream().toList().get(questionCount),rating);
//    }
//
//    @Override
//    public void finishTest() {
//        // TODO: fix undefined `unsortedCards`
//        //if(questionCount++ == getAllCardsInStudySystem().size()){
//        //    sortByValue(unsortedCards);
//        //    // Deck Update by rating answers
//        //    if(trueAnswerCount == 0){
//        //        resultPoint = 0;
//        //    }
//        //    else{
//        //        pointQuestion = pointQuestion / getAllCardsInStudySystem().size();
//        //        resultPoint = pointQuestion * trueAnswerCount;
//        //    }
//        //}
//    }
//
//    public  void sortByValue(HashMap<Card, Integer> hm)
//    {
//
//        List<Map.Entry<Card, Integer> > list = new LinkedList<Map.Entry<Card, Integer> >(hm.entrySet());
//
//
//        Collections.sort(list, Map.Entry.comparingByValue());
//
//
//        for (Map.Entry<Card, Integer> aa : list) {
//            //TODO fix undefined `sortedCards`
//            //sortedCards.add(aa.getKey());
//        }
//
//    }
//
//    @Override
//    public int getResult() {
//        return resultPoint;
//    }
//
//    @Override
//    public Card getNextCard(int index) {
//        if(questionCount++ == getAllCardsInStudySystem().size()){
//            return null;
//        }
//        else{
//            questionCount++;
//            return getNextCard(questionCount);
//        }
//    }
//
//    @Override
//    public float getProgress() {
//        if(getAllCardsInStudySystem().size() == 0){
//            return 0;
//        }
//        else{
//            return trueAnswerCount / getAllCardsInStudySystem().size();
//        }
//    }
}