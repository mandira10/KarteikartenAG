package com.swp.DataModel.CardTypes;

import java.util.Arrays;

import com.swp.DataModel.Card;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageDescriptionCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
@Entity
@DiscriminatorValue("IMAGEDESC")
public class ImageDescriptionCard extends Card
{

    /**
     * Bilddatei für die Frage. Wird als ByteArray gespeichert.
     */
    //@Lob
    //@Column
    //private Byte[] imageBytes;

    @Column
    private String image; //Not optimal

    /**
     * Antwort der Frage
     */
    @OneToMany(mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    private ImageDescriptionCardAnswer[] answers;

    /**
     * Leerer Konstruktor der Klasse ImageDescriptionCard
     */
    public ImageDescriptionCard()
    {
        this("", new ImageDescriptionCardAnswer[] {}, "", "");
    }

    /**
     * Konstruktor der Klasse ImageDescriptionCard
     * @param question Textuelle Frage zum Bild
     * @param image: Bild für die Karte
     * @param title Optionaler Titel der Karte
     */
    public ImageDescriptionCard(String question, ImageDescriptionCardAnswer[] answers, String title, String image)
    {
        super(CardType.IMAGEDESC);
        setTitle(title);
        this.question = question;
        this.image = image;
        this.answers = answers;
        setContent();
    }

    /**
     * Überschreibt die Grundmethode von Card. Setzt den Content, nach dem gesucht werden soll,
     * wenn ein Suchterm eingegeben wird.
     */
    @Override
    public void setContent()
    {
        content = title + "\n" + question;
        for(ImageDescriptionCardAnswer answer : answers)
            content += "\n" + answer.answertext;
    }

    /**
     * Überschreibt die Grundmethode von getAnswerString in Card.
     * Gibt die Antwort zurück, in diesem Fall alle Antworten in einem String.
     * @return Antwort der Karte
     */
    @Override
    public String getAnswerString()
    {
        int i = 1;
        String retstr = "";
        for(ImageDescriptionCardAnswer answer : answers)
        {
            retstr += String.valueOf(i++) + " " + answer.answertext + "\n";
        }

        return retstr;
    }

    @Override
    public String toString() {
        return "ImageDescriptionCard{" +
                "image='" + image + '\'' +
                ", answers=" + Arrays.toString(answers) +
                ", uuid='" + uuid + '\'' +
                ", type=" + type +
                ", question='" + question + '\'' +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                ", references='" + references + '\'' +
                ", title='" + title + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}