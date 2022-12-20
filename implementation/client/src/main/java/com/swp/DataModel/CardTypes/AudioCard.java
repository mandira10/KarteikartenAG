package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;
import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.AudioFileFormat;

/**
 * Klasse zum Erstellen von Karteikarten des Typs AudioCard.
 * Erbt die Grundeigenschaften der Klasse Card
 */
@Setter
@Getter
public class AudioCard extends Card 
{
    /**
     * Die AudioFile für die Karte
     */
    private AudioFileFormat oAudio;

    /**
     * Zusätzliche Beschreibung zur Audiodatei
     */
    private String sQuestion;

    /**
     * Textuelle Beschreibung zur AudioKarte
     */
    private String sAnswer;

    /**
     * Tauscht die Frage/Antwort Option, sodass die AudioFile sowohl
     * als Frage als auch als Antwort verwendet werden kann.
     */
    private boolean bSwapQA;

    /**
     * Leerer Konstruktor der Klasse AudioCard
     */
    public AudioCard(){
        super(CardType.AUDIO);
        setTitle("AudioCard");
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
    public AudioCard(AudioFileFormat audioFile, String title, String question, String answer, boolean swapQA, boolean visibility)
    {
        super(CardType.AUDIO);
        setTitle(title);
        oAudio = audioFile;
        sQuestion = question;
        sAnswer = answer;
        bSwapQA = swapQA;
        bVisibility = visibility;
        sContent = getContent();
    }

    @Override
    public String getContent(){
        return sTitle + "\n" + sQuestion + "\n" + sAnswer;
    }
}
