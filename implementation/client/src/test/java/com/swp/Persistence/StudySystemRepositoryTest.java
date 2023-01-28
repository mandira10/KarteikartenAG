package com.swp.Persistence;

import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;

import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudySystemRepositoryTest {
    
    // Repositories die getestet werden
    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();

    @Test
    public void studySystemCrudTest() {
        List<StudySystem> studySystems = exampleStudySystems2();

        // CREATE
        StudySystemRepository.startTransaction();
        for (final StudySystem studySystem : studySystems) {
            studySystemRepository.save(studySystem);
        }
        StudySystemRepository.commitTransaction();

        // READ
        List<StudySystem> allReadStudySystems = new ArrayList<>();
        StudySystemRepository.startTransaction();
        for (final StudySystem studySystem : studySystems) {
            final StudySystem readStudySystem = studySystemRepository.getStudySystemByUUID(studySystem.getUuid());
            assertEquals(studySystem, readStudySystem);
            allReadStudySystems.add(readStudySystem);
        }
        StudySystemRepository.commitTransaction();
        assertEquals(studySystems.size(), allReadStudySystems.size(), "same length");
        assertTrue(allReadStudySystems.containsAll(studySystems));

        // UPDATE
        //TODO

        // DELETE
        //TODO

    }

    @Test
    public void getStudySystemByUUID() {
        StudySystem studySystem = new LeitnerSystem("Name", StudySystem.CardOrder.ALPHABETICAL);
        StudySystem notSavedStudySystem = new LeitnerSystem("Anderes", StudySystem.CardOrder.ALPHABETICAL);
        StudySystemRepository.startTransaction();
        studySystemRepository.save(studySystem);
        StudySystemRepository.commitTransaction();
        StudySystemRepository.startTransaction();
        assertEquals(studySystem, studySystemRepository.getStudySystemByUUID(studySystem.getUuid()));
        StudySystemRepository.commitTransaction();
        StudySystemRepository.startTransaction();
        assertThrows(NoResultException.class, () -> studySystemRepository.getStudySystemByUUID(notSavedStudySystem.getUuid()));
        StudySystemRepository.commitTransaction();
    }


    // Hilfsfunktionen um einen Test-Datensatz an Instanzen zu haben
    public List<StudySystem> exampleStudySystems2() {
        List<StudySystem> exampleStudySystems2 = new ArrayList<>();
        Collections.addAll(exampleStudySystems2,
        new LeitnerSystem("Leitner 1", StudySystem.CardOrder.RANDOM),
        new LeitnerSystem("Leitner 2", StudySystem.CardOrder.ALPHABETICAL),
        new LeitnerSystem("Leitner 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL),
        new LeitnerSystem("Leitner 4", StudySystem.CardOrder.RANDOM),
        new LeitnerSystem("Leitner 5", StudySystem.CardOrder.ALPHABETICAL),
        new TimingSystem("Timing 1", StudySystem.CardOrder.RANDOM, 10),
        new TimingSystem("Timing 2", StudySystem.CardOrder.ALPHABETICAL, 20),
        new TimingSystem("Timing 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL, 1),
        new TimingSystem("Timing 4", StudySystem.CardOrder.RANDOM, 100),
        new TimingSystem("Timing 5", StudySystem.CardOrder.ALPHABETICAL, 5),
        new VoteSystem("Vote 1", StudySystem.CardOrder.RANDOM),
        new VoteSystem("Vote 2", StudySystem.CardOrder.ALPHABETICAL),
        new VoteSystem("Vote 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL),
        new VoteSystem("Vote 4", StudySystem.CardOrder.RANDOM),
        new VoteSystem("Vote 5", StudySystem.CardOrder.ALPHABETICAL)
    );

        return exampleStudySystems2;
    }




}
