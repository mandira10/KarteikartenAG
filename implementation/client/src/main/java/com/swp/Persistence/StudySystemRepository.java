package com.swp.Persistence;

import com.swp.DataModel.Card;
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
        //TOTEST klären ob Enum, oder DiscriminatorColumn, Column mit String, etc.
        getEntityManager().persist(type);

    }

    public void updateStudySystemTypes()
    {
        //TOTEST (siehe Todo in `addStudySystemType()`)
        getEntityManager().merge(StudySystem.StudySystemType.values());

    }

    public void addCardToBox(BoxToCard boxToCard){
        getEntityManager().persist(boxToCard);
    }


    public List<StudySystem> getStudySystems() {
        return getEntityManager().createQuery("SELECT s FROM StudySystem s ORDER BY s.name",StudySystem.class).getResultList();
    }

        public StudySystem getStudySystemByUUID(String uuid) {
            return getEntityManager()
                    .createNamedQuery("StudySystem.getStudySystemByUUID", StudySystem.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
    }

    /**
     * Die Funktion `findStudySystemsContaining` durchsucht die Namen aller StudySystems.
     * Es werden alle StudySystems zurückgegeben, die den übergebenen Suchtext als Teilstring im Namen enthalten.
     * @param searchterm ein String nach dem in den Namen gesucht werden soll.
     * @return Set<StudySystem> eine Menge von StudySystem, welche `searchWords` als Teilstring im Inhalt hat.
     */
    public List<StudySystem> findStudySystemsContaining(String searchterm) {
        return getEntityManager()
                .createNamedQuery("StudySystem.findStudySystemByContent", StudySystem.class)
                .setParameter("name", "%" + searchterm + "%")
                .getResultList();
    }
}
