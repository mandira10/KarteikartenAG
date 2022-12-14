package com.swp.DataModel;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Klasse für eine Kategorie
 */
public class Category 
{
    /**
     * Bezeichnung der Kategorie
     */
    private String sName;

    /**
     * UUID der Kategorie
     */
    private final String sUUID;

    /**
     * Parents der Kategorie, kann mehrere haben
     */
    private Set<Category> stParents;

    /**
     * Zugehörige UUIDs der Parents der Kategorie
     */
    private Set<String> stParentUUIDs;

    /**
     * Children der Kategorie, kann mehrere haben
     */
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


    //
    // Getter
    //
    public String getName()           { return this.sName; }
    public String getUUID()           { return this.sUUID; }
    public Set<Category> getParents() { return this.stParents; }
    public void setName(String sName) {        this.sName = sName;}

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
