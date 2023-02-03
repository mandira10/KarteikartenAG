package com.swp.Controller;

import java.util.List;

import com.gumse.gui.Locale;

import jakarta.persistence.NoResultException;
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

    protected <T> void failure(String localeid, String logstr, DataCallback<T> callback, String name)
    {
        if(!logstr.isEmpty())
            log.error(logstr);
        else
            log.error(Locale.getCurrentLocale().getString(localeid));

        if(callback != null) {
            if (!name.isEmpty())
                callback.callFailure(Locale.getCurrentLocale().getString(name) + " " + Locale.getCurrentLocale().getString(localeid));
            else
            callback.callFailure(Locale.getCurrentLocale().getString(localeid));
        }
    }

    protected <T> void failure(String localeid, String logstr, SingleDataCallback<T> callback, String name)
    {
        if(!logstr.isEmpty())
            log.error(logstr);
        else
            log.error(Locale.getCurrentLocale().getString(localeid));

        if(callback != null) {
            if (!name.isEmpty())
                callback.callFailure(Locale.getCurrentLocale().getString(name) + " " + Locale.getCurrentLocale().getString(localeid));
            else
                callback.callFailure(Locale.getCurrentLocale().getString(localeid));
        }
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

        if(!localeid.isEmpty() && callback != null)
            callback.callInfo(Locale.getCurrentLocale().getString(localeid));
    }

    protected <T> void info(String localeid, String logstr, SingleDataCallback<T> callback)
    {
        if(!logstr.isEmpty())
            log.info(logstr);
        else
            log.info(Locale.getCurrentLocale().getString(localeid));
    }

    protected <T> void callLogicFuncInThread(LogicFunc<T> func, String infolocale, String infolog, String failurelocale, String failurelog, DataCallback<T> callback,String name)
    {
        threadPool.exec(() -> {
            List<T> datalist = null;
            try  { datalist = func.callFunc();

                if(datalist == null || datalist.isEmpty())
                {
                    info(infolocale, infolog, callback);
                    return;
                }
                success(callback, datalist);
            }
            catch (IllegalArgumentException | IllegalStateException ex) 
            {
                log.error("Der übergebene Wert war leer oder null");
                if(ex.getMessage() != null)
                    failure(ex.getMessage(), failurelog.replace("$", ex.getMessage()), callback,name);
            }  
            catch (final Exception ex) 
            {
                if(ex.getMessage() != null)
                    failure(failurelocale, failurelog.replace("$", ex.getMessage()), callback,"");
            }

        });
    }

    protected <T> void callLogicFuncInThread(SingleLogicFunc<T> func, String infolocale, String infolog, String failurelocale, String failurelog, SingleDataCallback<T> callback,String name)
    {
        threadPool.exec(() -> {
            T data = null;
            try  {
                data = func.callFunc();

                if(data == null)
                {
                    info(infolocale, infolog, callback);
                    return;

                }
                success(callback, data);
            }
            catch (IllegalArgumentException | IllegalStateException ex) 
            {
                log.error("Der übergebene Wert war leer oder null");
                if(ex.getMessage() != null)
                    failure(ex.getMessage(),
                        failurelog.isEmpty() ?  failurelog.replace("$", ex.getMessage()) : ex.getMessage(), 
                        callback,name);
            }
            catch (NoResultException ex)
            {
                if(ex.getMessage() != null)
                    failure("noresultexception",
                            failurelog.isEmpty() ?  failurelog.replace("$", ex.getMessage()) : ex.getMessage(),
                            callback,"");
            }
            catch (final Exception ex) 
            {
                if(ex.getMessage() != null)
                    failure(failurelocale, 
                        failurelog.isEmpty() ?  failurelog.replace("$", ex.getMessage()) : ex.getMessage(), 
                        callback,"");
            }

        });
    }
};