package com.swp.DataModel.CardTypes;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

/**
 * Klasse für die ImageDescriptionCard.
 */
@Entity
@Table
@NoArgsConstructor
public class ImageDescriptionCardAnswer
{
    /**
     * Id der Antwort für die ImageDescriptionCard
     */
    @Id
    private String id;

    /**
     * Zugehörige ImageDescriptionCard
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ImageDescCard_ID")
    private ImageDescriptionCard attachedCard;

    /**
     * Antworttext
     */
    @Column
    public String answertext;

    /**
     * Position x
     */
    @Column
    public int xpos;

    /**
     * Position y
     */
    @Column
    public int ypos;

    /**
     * Konstruktor der ImageDescriptionAnswer
     * @param text  Text der Antwort
     * @param x Position x
     * @param y Position y
     * @param card Karte zu der diese Antwort gehört
     */
    public ImageDescriptionCardAnswer(String text, int x, int y, ImageDescriptionCard card)
    {
        this.id = UUID.randomUUID().toString();
        this.answertext = text;
        this.xpos = x;
        this.ypos = y;
        this.attachedCard = card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageDescriptionCardAnswer that = (ImageDescriptionCardAnswer) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ImageDescriptionCardAnswer{" +
                "id='" + id + '\'' +
                ", answertext='" + answertext + '\'' +
                ", xpos=" + xpos +
                ", ypos=" + ypos +
                '}';
    }
}