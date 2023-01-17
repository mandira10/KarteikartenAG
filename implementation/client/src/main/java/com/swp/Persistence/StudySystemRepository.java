package com.swp.Persistence;

import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem.StudySystem;

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
}
