package com.swp.DataModel;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AccessLevel;
import lombok.Getter;
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
public class Tag implements Serializable
{
    /**
     * Value des Tags
     */
    @Column
    private String sValue;

    /**
     * UUID des Tags
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    @Setter(AccessLevel.NONE)
    private final String sUUID;

    /**
     * Konstruktor der Klasse Tag
     * @param val: Name des Tags
     */
    public Tag(String val)
    {
        this.sValue = val;
        this.sUUID = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public Tag() {
        this.sValue = null;
        this.sUUID = null;
    }

    //
    // Getter
    //
    public String getValue() { return this.sValue; }
    public String getUUID()  { return this.sUUID; }

    public void setValue(String sValue) {
        this.sValue = sValue;
    }
}
