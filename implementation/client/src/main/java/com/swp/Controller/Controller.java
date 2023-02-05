package com.swp.Controller;

import com.gumse.gui.Locale;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Die oberklasse aller Controller-Klassen
 *
 * @author Tom Beuke
 */
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
        if(localeid.isEmpty())
            return;

        if (!logstr.isEmpty())
            log.info(logstr);
        else
            log.info(Locale.getCurrentLocale().getString(localeid));

        if (callback != null)
            callback.callInfo(Locale.getCurrentLocale().getString(localeid));

    }

    protected <T> void callLogicFuncInThread(LogicFunc<T> func, String infolocale, String infolog, String failurelocale, String failurelog, DataCallback<T> callback,String name)
    {
        threadPool.exec(() -> {
            try  {
                List<T> datalist = func.callFunc();

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
                    failure(Locale.getCurrentLocale().getString(ex.getMessage()).equals("") ? failurelocale : ex.getMessage(), failurelog.replace("$", ex.getMessage()), callback,name);
            }  
            catch(Exception ex)
            {
                if(ex.getMessage() != null)
                    failure(failurelocale, failurelog.replace("$", ex.getMessage()), callback,"");
            }
        });
    }

    protected <T> void callLogicFuncInThread(SingleLogicFunc<T> func, String failurelocale, String failurelog, SingleDataCallback<T> callback,String name)
    {
        threadPool.exec(() -> {
            try  {
                T data = func.callFunc();
                success(callback, data);

            }
            catch (IllegalArgumentException | IllegalStateException ex) 
            {
                if(ex.getMessage() != null)
                    failure(Locale.getCurrentLocale().getString(ex.getMessage()).equals("") ? failurelocale : ex.getMessage(),
                        failurelog.isEmpty() ? ex.getMessage() : failurelog.replace("$", ex.getMessage()), 
                        callback, name);
            }
            catch (NoResultException ex)
            {
                if(ex.getMessage() != null)
                    failure("noresultexception",
                            failurelog.isEmpty() ? ex.getMessage() : failurelog.replace("$", ex.getMessage()),
                            callback,"");
            }
            catch(Exception ex)
            {
                if(ex.getMessage() != null)
                    failure(failurelocale, 
                        failurelog.isEmpty() ? ex.getMessage() : failurelog.replace("$", ex.getMessage()), 
                        callback,"");
            }
        });
    }
}