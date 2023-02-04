package com.swp.Persistence;


import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;

import java.util.List;

/**
 * StudySystemBoxRepository mit den Grundfunktionen des BaseRepos
 */
public class StudySystemBoxRepository extends BaseRepository{

    private StudySystemBoxRepository() {
        super(StudySystemBox.class);
    }
    private static StudySystemBoxRepository studySystemBoxRepository  = null;
    public static StudySystemBoxRepository getInstance()
    {
        if(studySystemBoxRepository == null)
            studySystemBoxRepository = new StudySystemBoxRepository();
        return studySystemBoxRepository;
    }
    public List<StudySystemBox> getStudySystemsBoxesForStudySystem(StudySystem studySystem) {
        return getEntityManager().createQuery("SELECT sbox FROM StudySystemBox sbox where sbox.studySystem = :studySystem")
                .setParameter("studySystem",studySystem)
                .getResultList();
    }

    /**
     * Gibt die Anzahl der Karten in einer Box zurück
     * @param studySystemBox
     * @return
     */
    public List<Long> getProgressForLeitner(StudySystem studySystem){
        return getEntityManager()
                .createNamedQuery("StudySystemBox.progressLeitner", Long.class)
                .setParameter("studysystem", studySystem.getUuid())
                .getResultList();
    }

}
