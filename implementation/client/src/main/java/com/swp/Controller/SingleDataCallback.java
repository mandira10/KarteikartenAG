package com.swp.Controller;

public abstract class SingleDataCallback <E>
{
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    protected abstract void onSuccess(E data);
    protected abstract void onFailure(String msg);

    public void callSuccess(E data)
    {
        threadPool.addTaskToMainThread(() -> { onSuccess(data); });
    }

    public void callFailure(String msg)
    {
        threadPool.addTaskToMainThread(() -> { onFailure(msg); });
    }
}