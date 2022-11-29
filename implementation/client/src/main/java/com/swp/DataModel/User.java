package com.swp.DataModel;

public class User 
{
    private final long lID;
    private final String sName;
    private String sSessionID;
    private boolean bAuthenticated;

    public User(String username)
    {
        this.lID = 0;
        this.sName = username;
    }

    public void login(String password)
    {
        //Controller.login() ?
    }

    String getName()    { return sName; }
    String getSession() { return sSessionID; }
    long getID()        { return lID; }
}
