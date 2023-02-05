package com.swp.Persistence;


import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;

import java.util.List;

/**
 * StudySystemBoxRepository mit den Grundfunktionen des BaseRepos
 * @author Ole-Nikas Mahlstädt, Nadja Cordes
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

    /**
     * Gibt die StudySystemBoxen für ein StudySystem zurück
     * @param studySystem Das StudySystem für das die Boxen geholt werden sollen
     * @return Liste mit Boxen
     */
    public List<StudySystemBox> getStudySystemsBoxesForStudySystem(StudySystem studySystem) {
        return getEntityManager().createQuery("SELECT sbox FROM StudySystemBox sbox where sbox.studySystem = :studySystem")
                .setParameter("studySystem",studySystem)
                .getResultList();
    }


    /**
     * Gibt den ermittelten Progress für das Leitner System anhand Karten je Box zurück.
     * Wird als Liste an die Logik gegeben, um den Fortschritt zu ermitteln.
     * @param studySystem Das zugehörige StudySystem
     * @return Liste mit kumulierten Werten je StudySystemBox.
     */
    public List<Long> getProgressForLeitner(StudySystem studySystem){
        return getEntityManager()
                .createNamedQuery("StudySystemBox.progressLeitner", Long.class)
                .setParameter("studysystem", studySystem.getUuid())
                .getResultList();
    }

}
