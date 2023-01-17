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
        //TODO
    }

    public void updateStudySystemTypes()
    {
        //TODO
    }

    public StudySystem getStudySystem(Deck deck)
    {
        //TODO
        return null;
    }
}
