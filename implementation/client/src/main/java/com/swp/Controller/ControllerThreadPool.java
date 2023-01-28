package com.swp.Controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.gumse.tools.Output;

public class ControllerThreadPool
{
    private ExecutorService pPool;
    private static final int NUM_THREADS = 3;
    private static BlockingQueue<Runnable> pRunQueue;
    private static ControllerThreadPool pInstance;
    private boolean bSynchronized;

    private ControllerThreadPool()
    {
        pRunQueue = new LinkedBlockingQueue<Runnable>();
        pPool = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void exec(Runnable runnable)
    {
        if(bSynchronized)
            runnable.run();
        else
            pPool.execute(runnable);
    }

    public void addTaskToMainThread(Runnable runnable)
    {
        pRunQueue.add(runnable);
    }

    public void runQueue()
    {
        for(int i = 0; i < pRunQueue.size(); i++)
        {
            try { pRunQueue.take().run(); } 
            catch (InterruptedException e) 
            {
                Output.error("ControllerThreadPool: Failed to run Tasks in queue: " + e.getMessage());
            }
        }
    }

    public void synchronizedTasks(boolean sync)
    {
        this.bSynchronized = sync;
    }

    public static ControllerThreadPool getInstance()
    {
        if(pInstance == null)
            pInstance = new ControllerThreadPool();
        return pInstance;
    }
}