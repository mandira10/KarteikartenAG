package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.AudioFileFormat;

/**
 * Klasse zum Erstellen von Karteikarten des Typs AudioCard.
 * Erbt die Grundeigenschaften der Klasse Card
 */
@Setter
@Getter
@Entity
@DiscriminatorValue("AUDIO")
public class AudioCard extends Card 
{
    /**
     * Die AudioFile für die Karte
     */
    @Column
    private String audio;

    /**
     * Textuelle Beschreibung zur AudioKarte
     */
    @Column
    private String answer;

    /**
     * Tauscht die Frage/Antwort Option, sodass die AudioFile sowohl
     * als Frage als auch als Antwort verwendet werden kann.
     */
    @Column
    private boolean swapQA;

    /**
     * Leerer Konstruktor der Klasse AudioCard
     */
    public AudioCard()
    {
        this(null, "AudioCard", "", "", false, false);
    }

    /**
     * Konstruktor der Klasse AudioCard
     * @param audioFile AudioFile der Karte
     * @param title Optionaler Titel der Karte
     * @param question Textzusatz zur AudioFile
     * @param answer Antwort
     * @param swapQA Wechsel Frage/Antwort
     * @param visibility Sichtbarkeit der Karte
     */
    public AudioCard(String audioFile, String title, String question, String answer, boolean swapQA, boolean visibility)
    {
        super(CardType.AUDIO);
        setTitle(title);
        audio = audioFile;
        this.question = question;
        this.answer = answer;
        this.swapQA = swapQA;
        this.visibility = visibility;
        setContent();
    }

    @Override
    public void setContent(){
        content =  title + "\n" +  question + "\n" + answer;
    }
}
