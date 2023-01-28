package com.swp.Controller;

public abstract class SingleDataCallback <E>
{
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    protected abstract void onSuccess(E data);
    protected abstract void onFailure(String msg);

    public void callSuccess(E data)
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
}