package com.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Hello world!
 */
public class App
{
    public static void main(String[] args)
    {
        System.out.println( "Begin serial exploit test...." );
        App myApp = new App();
        BadCode ms = new BadCode();
        ms.address = "foo";
        ms.name = "fighters";

        myApp.serialize( ms );
        myApp.deserialize();
    }

    public void serialize(BadCode e)
    {
        try
        {
            FileOutputStream fileOut = new FileOutputStream( "object.ser" );
            ObjectOutputStream out = new ObjectOutputStream( fileOut );
            out.writeObject( e );
            out.close();
            fileOut.close();
            System.out.println( "Serialized data is saved in ./employee.ser" );
        }
        catch ( IOException i )
        {
            //i.printStackTrace();
            String error = "serialize caught IOException=" + i;
            System.out.println("ERROR: " + error);
            throw new RuntimeException( error );
        }
    }

    public void deserialize()
    {
        try
        {
            //Read the serialized data back in from the file "object.ser"
            FileInputStream fis = new FileInputStream("object.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);

            //Read the object from the data stream, and convert it back to a String
            BadCode objectFromDisk = (BadCode )ois.readObject();

            //Print the result.
            System.out.println(objectFromDisk.name);
            ois.close();
        }
        catch ( IOException i )
        {
            //System.out.printf( "IOException cuaght=" + i );
            //i.printStackTrace();
            String error = "serialize caught IOException=" + i;
            System.out.println("ERROR: " + error);
            throw new RuntimeException( error );
        }
        catch ( ClassNotFoundException cff )
        {
            //System.out.printf( "ClassNotFound cuaght=" + cff );
            //cff.printStackTrace();
            String error = "serialize caught ClassNotFound=" + cff;
            System.out.println("ERROR: " + error);
            throw new RuntimeException( error );
        }
    }
}
