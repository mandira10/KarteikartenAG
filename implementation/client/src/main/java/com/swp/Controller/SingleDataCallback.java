package com.swp.Controller;

/**
 * Singledatacallback Klasse. Gibt die Rückgaben vom Controller an den MainThread weiter.
 * @param <E> je nach Rückgabewert spezifisch
 *
 * @author Tom Beuke, Nadja Cordes
 */
public abstract class SingleDataCallback <E>
{
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    protected abstract void onSuccess(E data);
    protected abstract void onFailure(String msg);

    /**
     * Führt onSuccess in einem Thread aus, wenn der synchronized modus deaktiviert ist
     *
     * @param data Die zu übergebenden Daten
     */
    public void callSuccess(E data)
    {
        if(threadPool.isSynchronized())
            onSuccess(data);
        else
            threadPool.addTaskToMainThread(() -> onSuccess(data));
    }

    /**
     * Führt onFailure in einem Thread aus, wenn der synchronized modus deaktiviert ist
     *
     * @param msg Die Fehlernachricht
     */
    public void callFailure(String msg)
    {
        if(threadPool.isSynchronized())
            onFailure(msg);
        else
            threadPool.addTaskToMainThread(() -> onFailure(msg));
    }
}