package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table//(uniqueConstraints = @UniqueConstraint(name = "uniqueCardCategory",columnNames = {"card_uuid","category_uuid"}))
@Getter
@NamedQuery(name = "CardToCategory.allCardsOfCategory",
            query = "SELECT c FROM CardOverview c LEFT JOIN CardToCategory c2c ON c2c.card = c.uUUID WHERE c2c.category = :category")
@NamedQuery(name = "CardToCategory.allCategoriesOfCard",
            query = "SELECT c2c.category FROM CardToCategory c2c WHERE c2c.card = :card")
@NamedQuery(name = "CardToCategory.allC2CByCard",
            query = "SELECT c FROM CardToCategory c WHERE c.card = :card")
@NamedQuery(name = "CardToCategory.allC2CByCategory",
            query = "SELECT c FROM CardToCategory c WHERE c.category = :category")
@NamedQuery(name = "CardToCategory.findSpecificC2C",
        query = "SELECT c FROM CardToCategory c WHERE c.category = :category and c.card = :card")
public class CardToCategory implements Serializable
{
    /**
     * Identifier und Primärschlüssel für die
     * Karte-zu-Kategorie Verbindung.
     */
    @Id
   // @GeneratedValue(strategy = GenerationType.UUID)
    protected final String id;

    /**
     * Zugehörige Karte
     */
    @ManyToOne
    @JoinColumn(name="card_uuid", referencedColumnName = "CARD_ID")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private final Card card;

    /**
     * Zugehörige Kategorie
     */
    @ManyToOne
    @JoinColumn(name="category_uuid", referencedColumnName = "CATEGORY_ID")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private final Category category;

    /**
     * Konstruktor der Klasse CardToCategory
     * @param c: Karte
     * @param d: Kategorie
     */
    public CardToCategory(Card c, Category d)
    {
        this.card = c;
        this.category = d;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToCategory() {
        this.card = null;
        this.category = null;
        this.id = UUID.randomUUID().toString();
    }

}