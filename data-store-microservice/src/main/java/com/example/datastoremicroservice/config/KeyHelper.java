package com.example.datastoremicroservice.config;

import java.util.Objects;

//this class we create the lines(keys) for us, for how we will store the data in redis
public class KeyHelper {

    private final static String defaultPrefix = "app";//we will store keys with prefix "app" by default
    private static String prefix = null;

    public static void setPrefix(String keyPrefix){
        prefix = keyPrefix; // there is no "this." as it is a static variable
    }

    public static String getKey(String key){
        return getPrefix() + ":" + key;
    }

    public static String getPrefix(){
        return Objects.requireNonNullElse(prefix, defaultPrefix);
    }


}
