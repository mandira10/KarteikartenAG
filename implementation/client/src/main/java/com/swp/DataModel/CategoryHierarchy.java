package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Relationen zwischen Parent und Child Kategorie
 *  @author Nadja Cordes
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uniqueParentChild",columnNames = {"parent","child"}))
@Getter
@NamedQuery(name = "CategoryH.getParents",
        query = "SELECT c.parent FROM CategoryHierarchy c WHERE c.child = :child")
@NamedQuery(name = "CategoryH.getChildren",
        query = "SELECT c.child FROM CategoryHierarchy c WHERE c.parent = :parent")
@NamedQuery(name = "CategoryH.findSpecificCH",
        query = "SELECT ch FROM CategoryHierarchy ch WHERE ch.parent = :parent AND ch.child = :child")
@NamedQuery(name = "CategoryH.getAllCHForCategory",
        query = "SELECT ch FROM CategoryHierarchy ch WHERE ch.parent = :category OR ch.child = :category")
public class CategoryHierarchy implements Serializable {
    /**
     * Zugehöriges Child
     */
    @ManyToOne
    @JoinColumn(name = "child")
    private final Category child;

    /**
     * Zugehöriger Tag
     */
    @ManyToOne
    @JoinColumn(name = "parent")
    private final Category parent;

    /**
     * Identifier und Primärschlüssel für die Kategorie-Hierarchie
     */
    @Id
    protected final String id;

    /**
     * Konstruktor der Klasse CategoryHierarchy
     *
     * @param c: child
     * @param p: parent
     */
    public CategoryHierarchy(Category c, Category p) {
        this.child = c;
        this.parent = p;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CategoryHierarchy() {
        this.child = null;
        this.parent = null;
        this.id = UUID.randomUUID().toString();
    }
}


