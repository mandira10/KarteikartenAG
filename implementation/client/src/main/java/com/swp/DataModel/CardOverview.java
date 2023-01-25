package com.swp.DataModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import java.sql.Timestamp;

/**
 * Zeigt die einzelnen Karten mitsamt ihrem Erstellzeitpunkt und in wievielen Desks sie derzeit sind.
 */
@Entity
@Immutable
@Getter
@Subselect(value = "SELECT c.CARD_ID as uUUID," +
        "c.CREATIONDATE as cardCreated," +
        "c.content as content,"+
        "CASE WHEN c.TITLE = '' THEN c.QUESTION ELSE c.TITLE END AS titelToShow," +
        "(SELECT COUNT(DISTINCT b2C.ID) FROM BOXTOCARD b2C WHERE b2C.CARD_UUID = c.CARD_ID) AS countDecks, " +
        "FROM CARD c " +
        "GROUP BY c.CARD_ID " +
        "ORDER BY c.TITLE")
@Synchronize({"CARD", "BOXTOCARD"})
@NamedQuery(name  = "CardOverview.findCardsByContent",
           query = "SELECT co FROM CardOverview co WHERE LOWER(co.content) LIKE LOWER(:content)")
@NamedQuery(name = "CardOverview.allCardsWithTag",
           query = "SELECT co FROM CardOverview co LEFT JOIN CardToTag c2t ON c2t.card = co.uUUID WHERE c2t.tag = :tag")
@NamedQuery(name = "CardOverview.allCardsWithStudySystem",
           query = "SELECT co FROM CardOverview co LEFT JOIN BoxToCard b2c ON b2c.card = co.uUUID LEFT JOIN StudySystemBox sbox ON sbox.id = b2c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem")
@NamedQuery(name = "CardOverview.allCardsWithTagName",
           query = "SELECT co FROM CardOverview co LEFT JOIN CardToTag c2t ON c2t.card = co.uUUID LEFT JOIN Tag t on c2t.tag = t.val where LOWER(t.val) LIKE LOWER(:tagName)")
@NamedQuery(name = "CardOverview.allCardsWithCategoryName",
           query = "SELECT co FROM CardOverview co LEFT JOIN CardToCategory c2c ON c2c.card = co.uUUID LEFT JOIN Category cat on c2c.category = cat.uuid where LOWER(cat.name) LIKE LOWER(:categoryName)")
@NamedQuery(name = "CardOverview.getCardsForUUIDs",
       query = "SELECT c FROM Card c LEFT JOIN CardOverview co on c.uuid = co.uUUID WHERE co IN (:uuids)"
)
public class CardOverview  {


    @Id
    @Column(name = "uUUID")
    private String uUUID;

    @Column
    private int countDecks;

    @Column
    private Timestamp cardCreated;

    @Column
    private String titelToShow;
    @Column
    private String content;


    public CardOverview(){}

    //For Test reasons
    public CardOverview(String uUUID){
        this.uUUID = uUUID;
    }

}
