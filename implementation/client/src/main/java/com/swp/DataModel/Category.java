package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;


/**
 * Klasse für eine Kategorie
 *  @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
 */
@Getter
@Setter
@Entity
@Table
@NamedQuery(name = "Category.findByName",
        query = "SELECT ca FROM Category ca WHERE ca.name = :name")
@NamedQuery(name = "Category.findByUUID",
        query = "SELECT ca FROM Category ca WHERE ca.uuid = :uuid")
@NamedQuery(name  = "Category.findCategoriesByContent",
        query = "SELECT ca FROM Category ca WHERE LOWER(ca.name) LIKE LOWER(:content)")
public class Category implements Serializable
{
    /**
     * Bezeichnung der Kategorie
     */
    @Column(unique = true)
    private String name;

    /**
     * UUID der Kategorie
     */
    @Id
    @Column(name = "CATEGORY_ID")
    @Setter(AccessLevel.NONE)
    private final String uuid;

    @OneToMany(mappedBy="category")
    @Cascade(CascadeType.DELETE)
    private List<CardToCategory> cards;

    /**
     * Copy Konstruktor
     * @param other Eine andere Kategorie
     */
    public Category(Category other)
    {
        this.uuid     = other.getUuid();
        this.name     = other.getName();
    }

    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     */
    public Category(String name)
    {
        this.uuid     = UUID.randomUUID().toString();
        this.name     = name;
    }


    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public Category()
    {
        this("");
    }

    @Override
    public boolean equals(Object o){
        if ( this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category cat = (Category) o;
        return name.equals(cat.name);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        return prime + (name == null ? 0 : name.hashCode());
    }

}
