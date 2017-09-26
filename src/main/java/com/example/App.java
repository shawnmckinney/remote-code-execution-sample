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

    private static final String OBJ_LABEL = "myObject.ser";

    public static void main(String[] args)
    {
        System.out.println( "Begin serial exploit test...." );
        App myApp = new App();

        // Obviously out in the wild bad code won't be instantiated like this. Rather it'll be via reflection via XML parsing or file uploads.
        BadCode myObj = new BadCode();
        myObj.name = "duke";
        myObj.address = "moscone center";
        System.out.println("Input: " + myObj.name + " " + myObj.address);

        // This could happen anytime and serializes the contents of rogue object to file.
        myApp.serialize( myObj );

        // In a real exploit there's be resource activity here, e.g. HTTP invocations over the network or files being uploaded via a page.

        // It is during this method invocation the the exploit gets triggered by this hapless application.
        myObj = myApp.deserialize();

        //Print the result:
        System.out.println("Result: " + myObj.name + " " + myObj.address);
    }

    /**
     * Sample of serializing an object.
     *
     * @param e
     */
    public void serialize(BadCode e)
    {
        try
        {
            FileOutputStream fileOut = new FileOutputStream( OBJ_LABEL );
            ObjectOutputStream out = new ObjectOutputStream( fileOut );
            out.writeObject( e );
            out.close();
            fileOut.close();
            System.out.println( "Serialized data is saved in " + OBJ_LABEL );
        }
        catch ( IOException i )
        {
            String error = "serialize caught IOException=" + i;
            System.out.println("ERROR: " + error);
            throw new RuntimeException( error );
        }
    }

    /**
     * Sample of deserialzing an object.
     * @return
     */
    public BadCode deserialize()
    {
        BadCode out;
        try
        {
            //Read the serialized data back in from the file "object.ser"
            FileInputStream fis = new FileInputStream( OBJ_LABEL );
            ObjectInputStream ois = new ObjectInputStream(fis);

            //Read the object from the data stream, and convert it back to a String, when the exploit occurs:
            out = (BadCode )ois.readObject();
            ois.close();
        }
        catch ( IOException i )
        {
            String error = "serialize caught IOException=" + i;
            System.out.println("ERROR: " + error);
            throw new RuntimeException( error );
        }
        catch ( ClassNotFoundException cff )
        {
            String error = "serialize caught ClassNotFound=" + cff;
            System.out.println("ERROR: " + error);
            throw new RuntimeException( error );
        }
        return out;
    }
}