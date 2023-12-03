/*
 * © 2023 iamfortress.net
 */
package com.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This sample demos how to prevent the Java code deserialziation vulnerability that sometimes occurs inside apps that don't run inside of a security sandbox.
 */
public class App
{

    private static final String OBJ_LABEL = "myObject.ser";

    /**
     * Three steps being shown.  1. serialize the rogue object. 2. simulate some sort of operation where the serialized data is transmitted to the host. 3. deserialize obj triggering the exploit.
     *
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println( "Begin serial exploit test...." );
        App myApp = new App();

        // This will occur long before the attack, on the hacker's machine.
        BadCode myObj = new BadCode();
        myObj.name = "duke";
        myObj.address = "moscone center";
        System.out.println("Input: " + myObj.name + " " + myObj.address);

        // 1. Also on the hacker's machine. It serializes the contents of the rogue object (trojan horse) to a stream and will be uploaded to host (intended target) via input form or web service.
        myApp.serialize( myObj );

        // 2. In a real-world exploit there'll be some sort of resource activity where the serialized object is transmitted to the host (the trojan horse is accepted).  
        // It might be an HTTP invocation.

        // 3. The data containing rogue object is parsed, instantiated, and executed on the machine being targeted.
        // It is during the deserialize method the rogue object executes.
        myObj = myApp.deserialize();

        // The damage is done, print the contents of rogue object:
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
            //Read the serialized data back in from the file "myObject.ser"
            FileInputStream fis = new FileInputStream( OBJ_LABEL );
            ObjectInputStream ois = new ObjectInputStream(fis);

            //Read the object from the data stream, and convert it back to an Object, when the exploit gets triggered:
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
