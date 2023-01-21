package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.Tag;
import jakarta.persistence.NoResultException;

import java.util.List;

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
     * Die Funktion `find` sucht nach einem Tag, der dem übergebenen Text entspricht.
     * Existiert kein Tag mit so einem Inhalt, dann wird ein leeres `Optional` zurückgegeben.
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
     * @param card eine Karte
     * @return List<Tag> eine Liste von Tags, die der Karte zugeordnet sind.
     */
    public List<Tag> getTagsToCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToTag.allTagsWithCards", Tag.class)
                .setParameter("card", card)
                .getResultList();
    }

    /**
     * Die Funktion `getTags` liefer alle gespeicherten Tags zurück.
     *
     * @return Set<Tag> eine Menge mit allen Tags
     */
    public List<Tag> getTags() {
        return getEntityManager()
                .createQuery("SELECT t FROM Tag t", Tag.class)
                .getResultList();
    }



}
