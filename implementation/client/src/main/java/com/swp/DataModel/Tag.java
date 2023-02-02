package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Klasse für einen Tag für Karteikarten
 */
@Entity
@Table
@Getter
@Setter
@NamedQuery(name = "Tag.findTagByName",
        query = "SELECT t FROM Tag t WHERE LOWER(t.val) LIKE LOWER(:text)")
public class Tag implements Serializable
{
    /**
     * Value des Tags
     */
    @Id
    @Column(name = "TAG_VALUE")
    private String val;


    /**
     * Konstruktor der Klasse Tag
     * @param val: Name des Tags
     */
    public Tag(String val)
    {
        this.val = val;
    }

    /**
     * No Arg Konstruktor
     */
    public Tag() {
        this.val = "NoArgConstructor-Tag?!";
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return val.equals(tag.getVal());
    }

    @Override
    public int hashCode() {
            int prime = 31;
            return prime + (val == null ? 0 : val.hashCode());
        }

}
