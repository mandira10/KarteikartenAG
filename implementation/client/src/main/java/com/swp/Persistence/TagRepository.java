package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.Tag;
import jakarta.persistence.NoResultException;

import java.util.List;

/**
 * Die Datenbank repository für Tags
 */
public class TagRepository extends BaseRepository<Tag> {
    private TagRepository() {
        super(Tag.class);
    }

    // Singleton
    private static TagRepository tagRepository = null;
    public static TagRepository getInstance() {
        if(tagRepository == null)
            tagRepository = new TagRepository();
        return tagRepository;
    }

    /**
     * Holt für einen angegebenen Text den entsprechenden Tag aus der Datenbank.
     * Gibt es kein Tag mit diesem Text, wird eine Exception geworfen.
     *
     * @param text ein String nach dem ein existierender Tag gesucht wird.
     * @return Tag der gefundene Tag mit entsprechendem Namen.
     * @throws NoResultException wenn kein entsprechender Tag gefunden wurde.
     */
    public Tag findTag(String text) {
        return getEntityManager().createNamedQuery("Tag.findTagByName", Tag.class)
                .setParameter("text", text)
                .getSingleResult();
    }

    /**
     * Die Funktion `getTagsToCard` liefer alle Tags zurück, die einer Karte zugeordnet sind.
     * Gibt es keine Tags für die angegebene Karte, dann wird eine leere Liste zurückgegeben.
     *
     * @param card eine Karte
     * @return List<Tag> eine Liste von Tags, die der Karte zugeordnet sind.
     */
    public List<Tag> getTagsToCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToTag.allTagsWithCards", Tag.class)
                .setParameter("card", card)
                .getResultList();
    }
}
