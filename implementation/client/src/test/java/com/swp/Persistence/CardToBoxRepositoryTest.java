
package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.StudySystem.*;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Efe Carkcioglu, Ole-Niklas Mahlstädt
 */
public class CardToBoxRepositoryTest {

    // Repositories die getestet werden
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();

    @BeforeAll
    public static void beforeAll() {
        PersistenceManager.init("KarteikartenDBTest");
    }

    @BeforeEach
    public void beforeEach() {
        // cleanup
        BaseRepository.startTransaction();
        cardToBoxRepository.delete(cardToBoxRepository.getAll());
        cardRepository.delete(cardRepository.getAll());
        studySystemRepository.delete(studySystemRepository.getAll());
        BaseRepository.commitTransaction();

        // setup
        CardRepository.startTransaction();
        cardRepository.save(exampleCards());
        CardRepository.commitTransaction();


        StudySystemRepository.startTransaction();
        studySystemRepository.save(exampleStudySystems());
        StudySystemRepository.commitTransaction();
    }


    // Hilfsfunktionen um einen Test-Datensatz an Instanzen zu haben
    public List<Card> exampleCards() {
        List<Card> exampleCards = new ArrayList<>();
        byte[] img1 = new byte[]{(byte) 1024};
        byte[] img2 = new byte[]{(byte) 4096};
        byte[] img3 = new byte[]{(byte) 8192};
        byte[] img4 = new byte[]{(byte) 16384};
        byte[] img5 = new byte[]{(byte) 32768};
        ImageDescriptionCard imagedesccard3 = new ImageDescriptionCard("Bildbeschreibung 3?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img3);
        imagedesccard3.setAnswers(new ArrayList<ImageDescriptionCardAnswer>() {{ 
            new ImageDescriptionCardAnswer("Beschreibung 3", 0, 1, imagedesccard3);
        }});
    
        ImageDescriptionCard imagedesccard4 = new ImageDescriptionCard("Bildbeschreibung 4?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img4);
        imagedesccard4.setAnswers(new ArrayList<ImageDescriptionCardAnswer>() {{ 
            new ImageDescriptionCardAnswer("Beschreibung 4", 1, 2, imagedesccard4);
            new ImageDescriptionCardAnswer("andere Beschreibung", 6, 1, imagedesccard4);
        }});
    
        ImageDescriptionCard imagedesccard5 = new ImageDescriptionCard("Bildbeschreibung 5?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img5);
        imagedesccard5.setAnswers(new ArrayList<ImageDescriptionCardAnswer>() {{ 
            add(new ImageDescriptionCardAnswer("Beschreibung", 1, 3, imagedesccard5)); 
        }});

        Collections.addAll(exampleCards,
                new AudioCard(new byte[]{4}, "Audio 1", "Frage 1", "Antwort 1", false),
                new AudioCard(new byte[]{10}, "Audio 2", "Frage 2", "Antwort 2", true),
                new AudioCard(new byte[]{100}, "Audio 3", "Frage 3", "Antwort 3", false),
                new AudioCard(new byte[]{0}, "Audio 4", "Frage 4", "Antwort 4", true),
                new AudioCard(new byte[]{1}, "Audio 5", "Frage 5", "Antwort 5", false),
                new ImageDescriptionCard("Bildbeschreibung 1?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img1),
                new ImageDescriptionCard("Bildbeschreibung 2?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img2),
                imagedesccard3,
                imagedesccard4,
                imagedesccard5,
                new ImageTestCard("Image 1", "Antwort 1", img1, "Titel 1",false),
                new ImageTestCard("Image 2", "Antwort 2", img2, "Titel 2",true),
                new ImageTestCard("Image 3", "Antwort 3", img3, "Titel 3",false),
                new ImageTestCard("Image 4", "Antwort 4",img4, "Titel 4",true),
                new ImageTestCard("Image 5", "Antwort 5",img5, "Titel 5",false),
                new MultipleChoiceCard("Multi 1", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{0,3}, "Titel 1"),
                new MultipleChoiceCard("Multi 2", new String[]{"Antwort A", "Antwort B", "Antwort C",            }, new int[]{1,2}, "Titel 2"),
                new MultipleChoiceCard("Multi 3!", new String[]{"Eine ungewöhnlich lange Antwort A", "Antwort [B]"}, new int[]{1}, "Titel 3"),
                new MultipleChoiceCard("Multi 2^2", new String[]{"SQL als Antwort...", "", "Antwort'); DROP TABLE Card"}, new int[]{1}, "Titel 4"),
                new MultipleChoiceCard("Multi 0b0101", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{2,0}, "Titel 5"),
                new TextCard("Textfrage", "Antwort", "Titel"),
                new TextCard("Textfrage 2", "Antwort auf 2", "Titel 2"),
                new TextCard("Textfrage 3", "Antwort auf 3", "Titel 3"),
                new TextCard("Textfrage 4", "Antwort auf 4", "Titel 4"),
                new TextCard("Textfrage 5", "Antwort auf 5", "Titel 5"),
                new TrueFalseCard("WahrFalsch 1", false, "Titel 1"),
                new TrueFalseCard("WahrFalsch 2", true, "Titel 2"),
                new TrueFalseCard("WahrFalsch 3", false, "Titel 3"),
                new TrueFalseCard("WahrFalsch 4", true, "Titel 4"),
                new TrueFalseCard("WahrFalsch 5", false, "Titel 5")
        );
    return exampleCards;
    }

    private List<StudySystem> exampleStudySystems() {
        List<StudySystem> exampleStudy = new ArrayList<>();
        Collections.addAll(exampleStudy,
                new LeitnerSystem("Leitner 1", StudySystem.CardOrder.ALPHABETICAL),
                new TimingSystem("Timing 2", StudySystem.CardOrder.ALPHABETICAL, 30),
                new VoteSystem("Vote 3", StudySystem.CardOrder.RANDOM,5)
            );
        return exampleStudy;
    }

    private List<StudySystemBox> exampleBoxes() {
        List<StudySystemBox> exampleBoxes = new ArrayList<>();
        StudySystemRepository.startTransaction();
        List<StudySystem> studySystems = studySystemRepository.getAll();
        StudySystemRepository.commitTransaction();

        for (StudySystem ss : studySystems) {
            for (StudySystemBox box : ss.getBoxes()) {
                exampleBoxes.add(box);
            }
        }
        return exampleBoxes;
    }

    
}

