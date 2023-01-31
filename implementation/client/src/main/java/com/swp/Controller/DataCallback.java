package com.swp.Controller;

import java.util.List;

import com.gumse.tools.Output;

/**
 * Datacallback Klasse. Gibt die Rückgaben vom Controller an den MainThread weiter.
 * @param <E> je nach Rückgabewert spezifisch
 */
public abstract class DataCallback <E>
{
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    public abstract void onSuccess(List<E> data);
    public abstract void onFailure(String msg);
    public abstract void onInfo(String msg);

    public void callSuccess(List<E> data)
    {
        if(threadPool.isSynchronized())
            onSuccess(data);
        else
            threadPool.addTaskToMainThread(() -> { onSuccess(data); });
    }

    public void callFailure(String msg)
    {
        if(threadPool.isSynchronized())
            onFailure(msg);
        else
            threadPool.addTaskToMainThread(() -> { onFailure(msg); });
    }

    public void callInfo(String msg)
    {
        if(threadPool.isSynchronized())
            onInfo(msg);
        else
            threadPool.addTaskToMainThread(() -> { onInfo(msg); });
    }
}