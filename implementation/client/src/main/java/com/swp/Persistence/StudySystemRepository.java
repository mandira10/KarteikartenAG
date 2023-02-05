package com.swp.Persistence;

import com.swp.Controller.StudySystemController;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;

import java.util.List;

/**
 * Die Datenbank repository für StudySystems
 */
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

    /**
     * Speichert einen angegebenen Lernsystem-Typen in der Datenbank.
     * @param type ein Lernsystem-Typ.
     */
    public void addStudySystemType(StudySystem.StudySystemType type)
    {
        getEntityManager().persist(type);

    }

    /**
     * Aktualisiert die Lernsystem-Typen, die in der Datenbank gespeichert sind.
     */
    public void updateStudySystemTypes()
    {
        getEntityManager().merge(StudySystem.StudySystemType.values());

    }

    /**
     * Für die neuen Entities, die in der Datenbank gespeichert werden sollen, wird ein neuer
     * Lernsystem-Typ angelegt. Dieser wird dann in der Datenbank gespeichert.
     * Falls es schon gibt, throws eine Exception. 
     * 
     * @param boxToCard eine BoxToCard-Instanz, welche in der Datenbank gespeichert werden soll.
     * @throws EntityExistsException TODO
     */
    public void addCardToBox(BoxToCard boxToCard) throws EntityExistsException
    {
        getEntityManager().persist(boxToCard);
    }



    /**
     * Gibt eine Liste von allen Lernsystemen zurück.
     * @return eine Liste von allen Lernsystemen.
     */
    public List<StudySystem> getStudySystems() {
        return getEntityManager().createQuery("SELECT s FROM StudySystem s ORDER BY s.name",StudySystem.class).getResultList();
    }


    /**
     * Holt ein Lernsystem aus der Datenbank, welche die angegebene UUID hat.
     * Wenn es kein Lernsystem mit dieser UUID gibt, dann wird eine Exception geworfen.
     *
     * @param uuid eine UUID von einem Lernsystem.
     * @return ein Lernsystem.
     * @throws NoResultException falls es kein Lernsystem mit dieser UUID gibt.
     */
    public StudySystem getStudySystemByUUID(String uuid) throws NoResultException {
        return getEntityManager()
                .createNamedQuery("StudySystem.getStudySystemByUUID", StudySystem.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
    }

    /**
     * Die Funktion `findStudySystemsContaining` durchsucht die Namen aller StudySystems.
     * Es werden alle StudySystems zurückgegeben, die den übergebenen Suchtext als Teilstring im Namen enthalten.
     * Gibt es keine Lernsysteme mit diesem Teilstring im Namen, so wird eine leere Liste zurückgegeben.
     *
     * @param searchterm ein String nach dem in den Namen gesucht werden soll.
     * @return eine Liste von StudySystem, welche `searchWords` als Teilstring im Inhalt hat.
     */
    public List<StudySystem> findStudySystemsContaining(String searchterm) {
        return getEntityManager()
                .createNamedQuery("StudySystem.findStudySystemByContent", StudySystem.class)
                .setParameter("name", "%" + searchterm + "%")
                .getResultList();
    }


}
