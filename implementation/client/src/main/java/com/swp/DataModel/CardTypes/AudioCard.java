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
    }

    public AudioFileFormat getAudio() {
        return oAudio;
    }

    public String getAnswer() {
        return sAnswer;
    }

    public boolean isSwapQA() {
        return bSwapQA;
    }

    public String getsQuestion() {return sQuestion;}
    public void setQuestion(AudioFileFormat oAudio) {
        this.oAudio = oAudio;
    }

    public void setAnswer(String sAnswer) {
        this.sAnswer = sAnswer;
    }

    public void setSwapQA(boolean bSwapQA) {        this.bSwapQA = bSwapQA;}

    public void setsQuestion(String sQuestion) {this.sQuestion = sQuestion;}

}
