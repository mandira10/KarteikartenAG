package com.swp.DataModel;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
/**
 * Klasse für eine Kategorie
 */
@Entity
@Table
public class Category implements Serializable
{
    /**
     * Bezeichnung der Kategorie
     */
    @Column
    private String sName;

    /**
     * UUID der Kategorie
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    @Setter(AccessLevel.NONE)
    private final String sUUID;

    /**
     * Parents der Kategorie, kann mehrere haben
     */
    @Column
    @OneToMany
    private Set<Category> stParents;

    /**
     * Zugehörige UUIDs der Parents der Kategorie
     */
    private Set<String> stParentUUIDs;

    /**
     * Children der Kategorie, kann mehrere haben
     */
    @Column
    @OneToMany
    private Set<Category> stChildren;

    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     */
    public Category(String name)
    {
        this.sName = name;
        this.sUUID = UUID.randomUUID().toString();
    }
    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     * @param parents: Parents der Kategorie
     */
    public Category(String name, Set<Category> parents)
    {
        this.sName = name;
        this.sUUID = UUID.randomUUID().toString();
        this.stParents = parents;
    }

    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     * @param parents: Parents der Kategorie
     * @param children: Children der Kategorie
     */
    public Category(String name, Set<Category> parents, Set<Category> children)
    {
        this.sName = name;
        this.sUUID = UUID.randomUUID().toString();
        this.stParents = parents;
        this.stChildren = children;
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public Category() {
        this.sName = null;
        this.sUUID = UUID.randomUUID().toString();
        this.stParents = null;
        this.stChildren = null;
    }


    /**
     * Gibt die Anzahl der Parents zurück
     * @return Anzahl Parents
     */
    public int numParents(){
        return stParents.size();
    }

    /**
     * Gibt die Anzahl der Children zurück
     * @return Anzahl Children
     */
    public int numChildren(){
        return stChildren.size();
    }

    /**
     * Add a new child to a category
     */
    public void addChild(Category child)
    {
        this.stChildren.add(child);
    }

}
