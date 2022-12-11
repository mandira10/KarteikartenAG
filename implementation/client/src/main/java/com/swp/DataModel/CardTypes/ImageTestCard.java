package com.swp.DataModel.CardTypes;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;


/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageTestCard
 * Erbt die Grundeigenschaften der Klasse Card
 */
public class ImageTestCard extends Card 
{
    /**
     * Textuelle Beschreibung des Bildes
     */
    private String sQuestion;

    /**
     * Bild für die Karteikarte
     */
    private Texture oAnswer;

    /**
     * Tauscht die Frage/Antwort Option, sodass das Bild sowohl
     * als Frage als auch als Antwort verwendet werden kann.
     */
    private boolean bSwapQA;

    /**
     * Leerer Konstruktor der Klasse ImageTestCard
     */
    public ImageTestCard()
    {
        super(CardType.IMAGETEST);
        setTitle("ImageTestCard");
    }

    /**
     * Konstruktor der Klasse ImageTestCard
     * @param question: Textuelle Beschreibung
     * @param answer: Bild für Karte
     * @param swapQA: Wechsel Frage/Antwort
     * @param visible: Sichtbarkeit der Karte
     */
    public ImageTestCard(String question, Texture answer, boolean swapQA, boolean visible)
    {
        super(CardType.IMAGETEST);
        setTitle("ImageTestCard");
        sQuestion = question;
        oAnswer = answer;
        bSwapQA = swapQA;
        bVisibility = visible;
    }


    public String getQuestion() {
        return sQuestion;
    }

    public Texture getAnswer() {
        return oAnswer;
    }

    public void setQuestion(String sQuestion) {
        this.sQuestion = sQuestion;
    }

    public boolean isSwapQA() {
        return bSwapQA;
    }

    public void setAnswer(Texture oAnswer) {
        this.oAnswer = oAnswer;
    }

    public void setSwapQA(boolean bSwapQA) {
        this.bSwapQA = bSwapQA;
    }
}
