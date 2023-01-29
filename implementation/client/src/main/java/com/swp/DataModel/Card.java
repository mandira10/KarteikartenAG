package com.swp.DataModel;


import com.gumse.gui.Locale;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.swp.Validator.checkNotNullOrBlank;

/**
 * Abstrakte Superklasse für die Karten. Enthält die einzelnen Kartentypen sowie
 * weitere generische Eigenschaften jeder Karte, die alle Untertypen erben.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CardType")
@Setter
@Getter
@NamedQuery(name  = "Card.findCardByUUID",
            query = "SELECT c FROM Card c WHERE c.uuid = :uuid")
public abstract class Card implements Serializable
{
    /**
     * Enum für den Kartentypen
     */
    public enum CardType
    {
        TRUEFALSE,
        IMAGETEST,
        IMAGEDESC,
        MULITPLECHOICE,
        TEXT,
        AUDIO
    }

    /**
     * UUID der Karte
     */
    @Id
    @Column(name = "CARD_ID")
    @Setter(AccessLevel.NONE)
    protected final String uuid;

    /**
     * Kartentyp der spezifischen Karte
     */
    @Setter(AccessLevel.NONE)
    protected final CardType type;

    /**
     * Frage der Karte
     */
    protected String question;

    /**
     * Rating der Karte. Kann der Nutzer vergeben, um Karten (inhaltlich) zu bewerten.
     */
    @Column
    protected int rating;

    /**
     * String Aggregat des Inhaltes. Wird aus den String Inhalten der Karte zusammengestellt.
     * Wird verwendet für die Filterung nach Suchbegriffen.
     */
    @Column
    protected String content;

    /**
     * Die Referenzen, die zur Karte angelegt worden sind.
     */
    @Column
    protected String references;

    /**
     * Titel der Karte. Ist optional, wird verwendet im Glossar, wenn befüllt
     */
    @Column
    protected String title;

    /**
     * Erstellzeitpunkt der Karte.
     */
    @Column
    protected Timestamp creationDate;


    /**
     * Box, in der die Karte gespeichert ist.
     */
    //@ManyToOne
    //@JoinColumn(name = "studySystembox_id")
    //protected StudySystemBox box;
    @OneToMany(mappedBy = "card")
    @Cascade({CascadeType.ALL})
    protected List<CardToTag> assignedTags;

    @OneToMany(mappedBy = "card")
    @Cascade({CascadeType.ALL})
    protected List<CardToCategory> assignedCategories;

    /**
     * Konstruktor für eine einfache Karte.
     * @param type: Karteikartentyp der zu erstellenden Karte.
     */
    public Card(CardType type)
    {
        this.uuid = UUID.randomUUID().toString();
        this.type = type;
        this.rating = 0;
        this.title = "";
        this.content = "";
        this.references = "";
        this.creationDate = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Leerer Konstruktor für eine einfache Karte.
     */
    public Card()
    {
        uuid = UUID.randomUUID().toString();
        this.type = null;
        this.creationDate = new Timestamp(System.currentTimeMillis());
    }


    /**
     * Copy Konstruktor
     */
    /*public Card(Card other)
    {
        this.uuid     = other.getUuid();
        this.title    = other.getTitle();
        this.rating    = other.getRating();
        this.references = other.getReferences();
        this.creationDate = other.getCreationDate();
        this.nextLearnedAt = other.getNextLearnedAt();
        this.content = other.content;
    }*/

    public void setContent(){}

    /**
     * Manueller Setter, der prüft, ob der übergebene Wert im GUI leer ist
     * @param question: Frage der Karte
     */
    public void setQuestion(String question)
    {
        this.question = checkNotNullOrBlank(question,Locale.getCurrentLocale().getString("question"),false);
    }

    /**
     * Titel der Karte,wird auf null geprüft.
     * Da er leer sein kann, wird kein checkNotNullOrBlank ausgeführt.
     * @param title: Titel der Karte (Optional)
     */
    public void setTitle(String title)
    {
        if(title != null)
            this.title = title;
    }

    /**
     * Gibt die Antwort als String zurück. Wird in den Kartentypen überschrieben.
     * @return String, der in der GUI angezeigt wird.
     */
    public String getAnswerString() { return ""; }

    //TODO raus
    public List<Tag> getTagValues() {
        return this.getAssignedTags().stream().map(CardToTag::getTag).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rating == card.rating && Objects.equals(uuid, card.uuid) && type == card.type && Objects.equals(question, card.question) && Objects.equals(content, card.content) && Objects.equals(references, card.references) && Objects.equals(title, card.title) && Objects.equals(creationDate, card.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, type, question, rating, content, references, title, creationDate);
    }

    @Override
    public String toString() {
        return "Card{" +
                "uuid='" + uuid + '\'' +
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
