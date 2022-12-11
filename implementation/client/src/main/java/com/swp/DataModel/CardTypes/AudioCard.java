package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;

import javax.sound.sampled.AudioFileFormat;

/**
 * Klasse zum Erstellen von Karteikarten des Typs AudioCard.
 * Erbt die Grundeigenschaften der Klasse Card
 */
public class AudioCard extends Card 
{
    /**
     * Die AudioFile für die Karte
     */
    private AudioFileFormat oQuestion;

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
     */
    public AudioCard(AudioFileFormat audioFile, String answer, boolean swapQA, boolean visibility)
    {
        super(CardType.AUDIO);
        setTitle("AudioCard");
        oQuestion = audioFile;
        sAnswer = answer;
        bSwapQA = swapQA;
        bVisibility = visibility;
    }

    public AudioFileFormat getQuestion() {
        return oQuestion;
    }

    public String getAnswer() {
        return sAnswer;
    }


    public boolean isSwapQA() {
        return bSwapQA;
    }

    public void setQuestion(AudioFileFormat oQuestion) {
        this.oQuestion = oQuestion;
    }

    public void setAnswer(String sAnswer) {
        this.sAnswer = sAnswer;
    }

    public void setSwapQA(boolean bSwapQA) {
        this.bSwapQA = bSwapQA;
    }
}
