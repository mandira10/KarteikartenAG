package com.swp.Controller;

import java.util.List;

import com.gumse.tools.Output;

public abstract class DataCallback <E>
{
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    protected abstract void onSuccess(List<E> data);
    protected abstract void onFailure(String msg);
    protected abstract void onInfo(String msg);

    public void callSuccess(List<E> data)
    {
        Output.info("Calling success");
        threadPool.addTaskToMainThread(() -> { onSuccess(data); });
        Output.info("Done Calling success");
    }

    public void callFailure(String msg)
    {
        threadPool.addTaskToMainThread(() -> { onFailure(msg); });
    }

    public void callInfo(String msg)
    {
        threadPool.addTaskToMainThread(() -> { onInfo(msg); });
    }
}