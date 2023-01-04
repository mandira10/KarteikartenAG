package com.swp.DataModel;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
public class Category implements Serializable
{
    /**
     * Bezeichnung der Kategorie
     */
    @Column
    private String name;

    /**
     * UUID der Kategorie
     */
    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column
    @Setter(AccessLevel.NONE)
    private final String uuid;

    /**
     * Parents der Kategorie, kann mehrere haben
     */
    @Column
    @OneToMany
    private Set<Category> parents;

    /**
     * Zugehörige UUIDs der Parents der Kategorie
     */
    @ElementCollection
    @CollectionTable(name = "parent_uuids", joinColumns = @JoinColumn(name = "uuid"))
    @Column(name = "parents")
    private Set<String> parentUUIDs;


    /**
     * Children der Kategorie, kann mehrere haben
     */
    @Column
    @OneToMany
    private Set<Category> children;

    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     */
    public Category(String name)
    {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
    }
    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     * @param parents: Parents der Kategorie
     */
    public Category(String name, Set<Category> parents)
    {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
        this.parents = parents;
    }

    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     * @param parents: Parents der Kategorie
     * @param children: Children der Kategorie
     */
    public Category(String name, Set<Category> parents, Set<Category> children)
    {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
        this.parents = parents;
        this.children = children;
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public Category() {
        this.name = null;
        this.uuid = UUID.randomUUID().toString();
        this.parents = null;
        this.children = null;
    }


    /**
     * Gibt die Anzahl der Parents zurück
     * @return Anzahl Parents
     */
    public int numParents(){
        return parents.size();
    }

    /**
     * Gibt die Anzahl der Children zurück
     * @return Anzahl Children
     */
    public int numChildren(){
        return children.size();
    }

    /**
     * Add a new child to a category
     */
    public void addChild(Category child)
    {
        this.children.add(child);
    }

}
