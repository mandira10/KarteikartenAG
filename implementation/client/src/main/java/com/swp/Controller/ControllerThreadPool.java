package com.swp.Controller;

import com.gumse.tools.Output;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ThreadPool für die Controllerfunktionen
 *
 * @author Tom Beuke
 */
public class ControllerThreadPool
{
    private final ExecutorService pPool;
    private static final int NUM_THREADS = 3;
    private static BlockingQueue<Runnable> pRunQueue;
    private static ControllerThreadPool pInstance;
    private boolean bSynchronized;

    private ControllerThreadPool()
    {
        pRunQueue = new LinkedBlockingQueue<>();
        pPool = Executors.newFixedThreadPool(NUM_THREADS);
    }

    /**
     * Fügt eine Aufgabe dem Thread-Pool hinzu
     *
     * @param runnable Die auszuführende Aufgabe
     */
    public void exec(Runnable runnable)
    {
        if(bSynchronized)
            runnable.run();
        else
            pPool.execute(runnable);
    }

    /**
     * Führt eine aufgabe im haupt Thread aus
     *
     * @param runnable Die auszuführende Aufgabe
     */
    public void addTaskToMainThread(Runnable runnable)
    {
        pRunQueue.add(runnable);
    }


    /**
     * Führt die Aufgaben aus, welche in den haupt Thread gelegt wurden
     */
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

    /**
     * Entscheidet, ob neue Aufgaben zum Thread-Pool hinzugefügt werden sollen, oder nicht
     * @param sync Soll synchronisiert laufen?
     */
    public void synchronizedTasks(boolean sync)
    {
        this.bSynchronized = sync;
    }

    public boolean isSynchronized()
    {
        return this.bSynchronized;
    }

    public static ControllerThreadPool getInstance()
    {
        if(pInstance == null)
            pInstance = new ControllerThreadPool();
        return pInstance;
    }
}