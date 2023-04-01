/*
 * © 2023 iamfortress.net
 */
package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This sample demos how to prevent the executing Java runtime commands.
 */
public class App3
{
    /**
     * Access system runtime.
     *
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println( "Begin runtime command test 3..." );
        try
        {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("uname -a");
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ( ( line = b.readLine() ) != null )
            {
                System.out.println(line);
            }
            b.close();
        }
        catch(InterruptedException ie )
        {
            System.out.println("IOException calling runtime: " + ie);
        }
        catch ( java.io.IOException ie )
        {
            System.out.println("IOException calling runtime: " + ie);
        }
    }
}
