package com.swp.Logic;

import com.swp.Persistence.BaseRepository;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * Die BaseLogic stellt Funktionen für die spezifischen Logic-Klassen bereit.
 * Zum Beispiel wird hier der gemeinsame Ablauf für Start, Ausführung und ggf. verwerfen einer Transaktion gesammelt.
 * @author Ole-Niklas Mahlstädt
 */
abstract class BaseLogic<T> {
    /**
     * Das entsprechende `BaseRepository`, welches die Objekte vom Typ `T` enthält.
     */
    @Getter(AccessLevel.PROTECTED)
    private final BaseRepository<T> baseRepository;

    /**
     * Konstruktor für eine Logic, die BaseLogic erweitert.
     * @param baseRepository ein BaseRepository vom entsprechendem Typ.
     */
    public BaseLogic(BaseRepository<T> baseRepository) {
        this.baseRepository = baseRepository;
    }

    /**
     * Führt eine übergebene (Lambda-) Funktion transactional aus.
     * Das heißt, es wird eine Transaktion gestartet, ausgeführt und im Fehlerfall verworfen.
     * @param function Eine Funktion die transactional ausgeführt werden soll.
     * @return ein Objekt mit dem Ergebnis der Transaktion
     */
    protected static <RV> RV execTransactional(final Supplier<RV> function) {
        // Locking um mögliche Nebenläufigkeitsprobleme zu vermeiden.
        // Falls sequentielle DB-Zugriffe nicht schnell genug sind, muss hier etwas umgebaut werden.
        // Siehe z.B. https://en.wikibooks.org/wiki/Java_Persistence/Locking#Optimistic_Locking
        synchronized (BaseRepository.transaction) {
            try {
                BaseRepository.startTransaction();
                return function.get();
            } catch (final Exception ex) {
                BaseRepository.rollbackTransaction();
                throw ex;
            } finally {
                if (BaseRepository.isTransactionActive()) {
                    BaseRepository.commitTransaction();
                }
            }
        }
    }
}
