package com.swp.DataModel.CardTypes;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * Klasse zum Erstellen von Karteikarten des Typs MultipleChoiceCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
@Entity
@DiscriminatorValue("MULITPLECHOICE")
public class MultipleChoiceCard extends Card 
{
    /**
     * Antwortmöglichkeiten der Karte
     */
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column
    private String[] answers;

    /**
     * Array mit den korrekten Antworten
     */
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column
    private int[] correctAnswers;

    /**
     * Leerer Konstruktor der Klasse MultipleChoiceCard
     */
    public MultipleChoiceCard()
    {
        this("", new String[] {}, new int[] {}, "");
    }

    /**
     * Konstruktor der Klasse MultipleChoiceCard
     * @param question Frage der Karte
     * @param answers Antwortmöglichkeiten der Karte
     * @param correctAnswers Korrekte Antworten für die Karte. Liegen die hinterlegten Indexe
     *                       außerhalb der Antwort-Array Größe, dann wird eine Exception geworfen.
     * @param title Optionaler Titel der Karte
     */
    public MultipleChoiceCard(String question, String[] answers, int[] correctAnswers, String title){
        super(CardType.MULITPLECHOICE);
        setTitle(title);
        this.question = question;
        this.answers = answers;
        if (Arrays.stream(correctAnswers).anyMatch(a -> a >= answers.length) )
            throw new IllegalArgumentException(Locale.getCurrentLocale().getString("multiplechoicecarderror"));
        this.correctAnswers = correctAnswers;
        setContent();
    }

    /**
     * Überschreibt die Grundmethode von Card. Setzt den Content, nach dem gesucht werden soll,
     * wenn ein Suchterm eingegeben wird.
     */
    @Override
    public void setContent(){
        content = title + "\n" +  question + "\n" + Arrays.toString(answers);
    }

    /**
     * Überschreibt die Grundmethode von getAnswerString in Card.
     * Gibt die Antwort zurück. In diesem Fall alle Antworten plus ihre Korrektheit.
     * @return Antwort der Karte
     */
    @Override
    public String getAnswerString() 
    {
        String correctstr = "Correct:\n";
        String incorrectstr = "Incorrect:\n";
        int i = 0;
        List<Integer> correctList = Arrays.stream(correctAnswers).boxed().toList();
        for(String answer : answers)
        {
            if(correctList.contains(i++))
                correctstr += "  " + answer + "\n";
            else
                incorrectstr += "  " + answer + "\n";
        }

        return correctstr + "\n" + incorrectstr;
    }
}
