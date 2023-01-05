package com.swp.DataModel.CardTypes;

import java.util.Arrays;
import java.util.List;

import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

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
        this("", new String[] {}, new int[] {}, "", false);
    }

    /**
     * Konstruktor der Klasse MultipleChoiceCard
     * @param question Frage der Karte
     * @param answers Antwortmöglichkeiten der Karte
     * @param correctAnswers Korrekte Antworten für die Karte
     * @param title Optionaler Titel der Karte
     * @param visible Sichtbarkeit der Karte
     */
    public MultipleChoiceCard(String question, String[] answers, int[] correctAnswers, String title, boolean visible){
        super(CardType.MULITPLECHOICE);
        setTitle(title);
        this.question = question;
        this.answers = answers;
        this.correctAnswers = correctAnswers;
        this.visibility = visible;
        setContent();
    }


    @Override
    public void setContent(){
        content = title + "\n" +  question; //TODO incorporate answers?
    }

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
