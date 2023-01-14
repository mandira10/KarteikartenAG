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
@NamedQuery(name = "Category.getParents",
        query = "SELECT c.parents FROM Category c WHERE c = :category")
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

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "children")
    private Set<Category> parents = new HashSet<>();

    /**
     * Children der Kategorie, kann mehrere haben
     */

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Category> children = new HashSet<>();

//    /**
//     * Zugehörige UUIDs der Parents der Kategorie
//     */
//    @ElementCollection
//    @CollectionTable(name = "parent_uuids", joinColumns = @JoinColumn(name = "uuid"))
//    @Column(name = "parents_uuid")
//    private Set<String> parentUUIDs;
// TODO: needed?



    /**
     * Copy Konstruktor
     */
    public Category(Category other)
    {
        this.uuid     = other.getUuid();
        this.name     = other.getName();
        this.parents  = other.getParents();
        this.children = other.getChildren();
    }

    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     */
    public Category(String name)
    {
        this(name, new HashSet<Category>(), new HashSet<Category>());
    }
    /**
     * Konstruktor der Klasse Category
     * @param name: Name der Kategorie
     * @param parents: Parents der Kategorie
     */
    public Category(String name, Set<Category> parents)
    {
        this(name, parents, new HashSet<Category>());
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
    public Category() 
    {
        this("", new HashSet<Category>(), new HashSet<Category>());
    }

    public void setChildren(Set<Category> children){
        this.getChildren().stream().forEach(children::remove);
        for(Category c : children){
            this.addChild(c);
            //TODO: was ist bei Dopplungen?
        }
    }

    public void setParents(Set<Category> parents){
        this.getChildren().stream().forEach(parents::remove);
        for(Category c : parents){
            c.addChild(c);
        }
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
        child.getParents().add(this);
    }

}
