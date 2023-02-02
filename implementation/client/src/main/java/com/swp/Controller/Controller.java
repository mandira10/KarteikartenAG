package com.swp.Controller;

import java.util.List;

import com.gumse.gui.Locale;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Controller 
{
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();

    protected interface LogicFunc <T>
    {
        List<T> callFunc();
    }

    protected interface SingleLogicFunc <T>
    {
        T callFunc();
    }

    protected <T> void failure(String localeid, String logstr, DataCallback<T> callback)
    {
        if(!logstr.isEmpty())
            log.error(logstr);
        else
            log.error(Locale.getCurrentLocale().getString(localeid));

        if(callback != null)
            callback.callFailure(Locale.getCurrentLocale().getString(localeid));
    }

    protected <T> void failure(String localeid, String logstr, SingleDataCallback<T> callback)
    {
        if(!logstr.isEmpty())
            log.error(logstr);
        else
            log.error(Locale.getCurrentLocale().getString(localeid));

        if(callback != null)
            callback.callFailure(Locale.getCurrentLocale().getString(localeid));
    }

    protected <T> void success(DataCallback<T> callback, List<T> data)
    {
        if(callback != null)
            callback.callSuccess(data);
    }

    protected <T> void success(SingleDataCallback<T> callback, T data)
    {
        if(callback != null)
            callback.callSuccess(data);
    }

    protected <T> void info(String localeid, String logstr, DataCallback<T> callback)
    {
        if(!logstr.isEmpty())
            log.info(logstr);
        else
            log.info(Locale.getCurrentLocale().getString(localeid));

        if(callback != null)
            callback.callInfo(Locale.getCurrentLocale().getString(localeid));
    }

    protected <T> void info(String localeid, String logstr, SingleDataCallback<T> callback)
    {
        if(!logstr.isEmpty())
            log.info(logstr);
        else
            log.info(Locale.getCurrentLocale().getString(localeid));
    }

    protected <T> void callLogicFuncInThread(LogicFunc<T> func, String infolocale, String infolog, String failurelocale, String failurelog, DataCallback<T> callback)
    {
        threadPool.exec(() -> {
            List<T> datalist = null;
            try  { datalist = func.callFunc(); } 
            catch (IllegalArgumentException | IllegalStateException ex) 
            {
                log.error("Der übergebene Wert war leer oder null");
                failure(failurelocale, failurelog.replace("$", ex.getMessage()), callback);
            }  
            catch (final Exception ex) 
            {
                failure(failurelocale, failurelog.replace("$", ex.getMessage()), callback);
            }

            if(datalist.isEmpty()) 
            {
                info(infolocale, infolog, callback);
                return;
            }
            
            success(callback, datalist);
        });
    }

    protected <T> void callLogicFuncInThread(SingleLogicFunc<T> func, String infolocale, String infolog, String failurelocale, String failurelog, SingleDataCallback<T> callback)
    {
        threadPool.exec(() -> {
            T data = null;
            try  { data = func.callFunc(); } 
            catch (IllegalArgumentException | IllegalStateException ex) 
            {
                log.error("Der übergebene Wert war leer oder null");
                failure(failurelocale, failurelog.replace("$", ex.getMessage()), callback);
            }  
            catch (final Exception ex) 
            {
                failure(failurelocale, failurelog.replace("$", ex.getMessage()), callback);
            }

            if(data == null) 
            {
                info(infolocale, infolog, callback);
                return;
            }
            
            success(callback, data);
        });
    }
};