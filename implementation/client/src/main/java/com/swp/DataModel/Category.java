package com.swp.DataModel;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Klasse für eine Kategorie
 */
@Getter
@Setter
@Entity
@Table
@NamedQuery(name = "Category.findByName",
        query = "SELECT c FROM Category c WHERE c.name = :name")
@NamedQuery(name = "Category.findByUUID",
        query = "SELECT c FROM Category c WHERE c.uuid = :uuid")
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
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CATEGORY_ID")
    @Setter(AccessLevel.NONE)
    private final String uuid;

    @OneToMany(mappedBy="category")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<CardToCategory> cards;

    /**
     * Copy Konstruktor
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

}
