package com.swp.DataModel;

public class User 
{
    private static User pLoggedInUser = null;
    private String sUsername, sPassword;

    public User(String username, String password)
    {
        sUsername = username;
        sPassword = password;
    }

    public static void loginUser(User user)
    {
        pLoggedInUser = user;
    }

    public static User getLoggedInUser()
    {
        return pLoggedInUser;
    }

    public static boolean isLoggedIn()
    {
        return pLoggedInUser != null;
    }
}