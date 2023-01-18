package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;

import java.util.*;

public class StudySystemRepository extends BaseRepository<StudySystem> {
    private StudySystemRepository() {
        super(StudySystem.class);
    }

    // Singleton
    private static StudySystemRepository studySystemRepository  = null;
    public static StudySystemRepository getInstance()
    {
        if(studySystemRepository == null)
            studySystemRepository = new StudySystemRepository();
        return studySystemRepository;
    }


    public void addStudySystemType(StudySystem.StudySystemType type)
    {
        //TODO klären ob Enum, oder DiscriminatorColumn, Column mit String, etc.
    }

    public void updateStudySystemTypes()
    {
        //TODO (siehe Todo in `addStudySystemType()`)
    }

    public StudySystem getStudySystem(Deck deck)
    {
        //wenn man schon das Deck hat, könnte man auch über den @Getter an `studySystem` Attribut kommen
        return deck.getStudySystem();

        //oder falls man nur die ID von dem Deck hat
        //return getEntityManager()
        //        .createNamedQuery("Deck.getStudySystemByUUID", StudySystem.class)
        //        .setParameter("uuid", deck.getUuid())
        //        .getSingleResult();

        //return getEntityManager()
        //        .createNamedQuery("Deck.getStudySystemForDeck", StudySystem.class)
        //        .setParameter("deck", deck)
        //        .getSingleResult();
    }

    public void addCardToBox(BoxToCard boxToCard){
        //TODO
    }

    public List<Card> getAllCardsInStudySystem(){
        //TODO
        return null;
    }

    public int getTrueCount(){
        //TODO
        return 0;
    }

    public int getQuestionCount(){
        //TODO
        return 0;
    }

    public void setTrueCount(int i){
        //TODO
    }

    public void setQuestionCount(int i){
        //TODO
    }

    public int getTimeLimit(){
        return 0;
        //TODO
    }

    public void setTimeLimit(int i){
        //TODO
    }

    public Card getNextCard(){
        //TODO
        // Should return Card by QuestionCount
        return null;
    }

    public void saveProgress(float progress){
        //TODO
    }






}
