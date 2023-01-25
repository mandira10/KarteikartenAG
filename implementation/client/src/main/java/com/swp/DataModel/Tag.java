package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.io.Serializable;
import java.util.List;

/**
 * Klasse für einen Tag für Karteikarten
 */
@Entity
@Table
@Getter
@Setter
//@NoArgsConstructor
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

    @OneToMany(mappedBy="tag")
    @Cascade(CascadeType.DELETE)
    private List<CardToTag> cards;

    /**
     * Konstruktor der Klasse Tag
     * @param val: Name des Tags
     */
    public Tag(String val)
    {
        this.val = val;
    }

    public Tag() {
        this.val = "NoArgConstructor-Tag?!";
    }

    @Override
    public boolean equals(Object o){
        if ( this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return val == tag.val;
    }

}
