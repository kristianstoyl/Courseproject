package com.ntnu.kristian.courseproject;

/**
 * Created by Kristian on 22.04.2016.
 */
public class Movies {
    public String name;
    public int id;
    public Movies(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    public String toString(){
        return name;
    }
}
