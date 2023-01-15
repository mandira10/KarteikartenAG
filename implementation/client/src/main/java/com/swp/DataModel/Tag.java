package com.swp.DataModel;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.NamedQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.UUID;

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
    @Column(unique = true)
    private String val;

    /**
     * UUID des Tags
     */
    @Id
    @Column
    @Setter(AccessLevel.NONE)
    private final String uuid;

    /**
     * Konstruktor der Klasse Tag
     * @param val: Name des Tags
     */
    public Tag(String val)
    {
        this.val = val;
        this.uuid = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public Tag() {
        this.val = null;
        this.uuid = null;
    }

    @Override
    public boolean equals(Object o){
        if ( this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return val == tag.val;
    }

}
