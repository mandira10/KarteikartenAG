package com.swp.DataModel.CardTypes;

import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * Klasse zum Erstellen von Karteikarten des Typs AudioCard.
 * Erbt die Grundeigenschaften der Klasse Card
 *
 * @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
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
    @Lob
    @Column
    private byte[] audio;

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
        this(null, "", "", "", false);
    }

    /**
     * Konstruktor der Klasse AudioCard
     * @param audioFile AudioFile der Karte
     * @param title Optionaler Titel der Karte
     * @param question Textzusatz zur AudioFile
     * @param answer Antwort
     * @param swapQA Wechsel Frage/Antwort
     */
    public AudioCard(byte[] audioFile, String title, String question, String answer, boolean swapQA)
    {
        super(CardType.AUDIO);
        setTitle(title);
        audio = audioFile;
        this.question = question;
        this.answer = answer;
        this.swapQA = swapQA;
        setContent();
    }

    /**
     * Überschreibt die Grundmethode von Card. Setzt den Content, nach dem gesucht werden soll,
     * wenn ein Suchterm eingegeben wird.
     */
    @Override
    public void setContent(){
        content =  title + "\n" +  question + "\n" + answer;
    }

    /**
     * Setter für die Antwort. Prüft zunächst, dass die Karte nicht null oder leer ist.
     * @param answer: zu setzende Antwort
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }


    /**
     * Liest eine WAV datei in einen byte array ein
     *
     * @param filepath Dateipfad der WAV Datei
     * @return Gibt true bei erfolgreichem Laden der Audiodatei wieder
     */
    public boolean loadAudioFile(String filepath)
    {
        ByteBuffer audioBuffer = Toolbox.loadFileToByteBuffer(filepath, getClass());  
        if(audioBuffer == null)
            return false;    

        audio = new byte[audioBuffer.remaining()];
        audioBuffer.get(audio);
        MemoryUtil.memFree(audioBuffer);

        return true;
    }


    /**
     * Überschreibt die Grundmethode von getAnswerString in Card.
     * Gibt die Antwort zurück.
     * @return Antwort der Karte
     */
    @Override
    public String getAnswerString() 
    {
        return answer;
    }
}
