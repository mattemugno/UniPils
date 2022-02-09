package it.unipi.lsmdb.config;

import it.unipi.lsmdb.bean.User;

public class DataSession {
    private static User userLogged = null;
    private static String IdUserLogged=null;

    public static void setUserLogged(User userLogged){
        DataSession.userLogged = userLogged;
    }

    public static User getUserLogged(){
        return userLogged;
    }

    public static void IdUserLogged(String id){
        DataSession.IdUserLogged=id;
    }

    public static void logout() {
        userLogged = null;
    }
}
