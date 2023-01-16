package com.swp.Persistence;

import com.swp.DataModel.Tag;
import jakarta.persistence.NoResultException;

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
}
