package com.swp.Controller;

import java.util.List;

/**
 * Datacallback Klasse. Gibt die Rückgaben vom Controller an den MainThread weiter.
 * @param <E> je nach Rückgabewert spezifisch
 */
public abstract class DataCallback <E>
{
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    /**
     * Wird beim erfolgreichen holen der Daten ausgeführt
     *
     * @param data Die übergeben Daten
     */
    public abstract void onSuccess(List<E> data);

    /**
     * Wird bei einem Fehler
     *
     * @param msg Die Fehlernachricht
     */
    public abstract void onFailure(String msg);

    /**
     * Wird ausgeführt, wenn nicht-essentielle infos übergeben werden sollen
     *
     * @param msg Die Infonachricht
     */
    public abstract void onInfo(String msg);

    /**
     * Führt onSuccess in einem Thread aus, wenn der synchronized modus deaktiviert ist
     *
     * @param data Die zu übergebenden Daten
     */
    public void callSuccess(List<E> data)
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


    /**
     * Führt onInfo in einem Thread aus, wenn der synchronized modus deaktiviert ist
     *
     * @param msg Die Infonachricht
     */
    public void callInfo(String msg)
    {
        if(threadPool.isSynchronized())
            onInfo(msg);
        else
            threadPool.addTaskToMainThread(() -> onInfo(msg));
    }
}