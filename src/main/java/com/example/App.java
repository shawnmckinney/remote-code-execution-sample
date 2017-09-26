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
     * Three steps being shown.  One serialize the object. Two simulate some sort of operation where the serialized data is transmitted to the host. Three deserialize which triggers the exploit.
     *
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println( "Begin serial exploit test...." );
        App myApp = new App();

        // Obviously, out in the wild bad code won't be instantiated like this. Rather, it'll occur during reflection that is triggered within an XML parsing or file upload operation.
        BadCode myObj = new BadCode();
        myObj.name = "duke";
        myObj.address = "moscone center";
        System.out.println("Input: " + myObj.name + " " + myObj.address);

        // 1. The object serialization might happen elsewhere. It serializes the contents of a rogue object to a stream and is the conduit for attack.
        myApp.serialize( myObj );

        // 2. In a real-world exploit there'll be some sort of resource activity that occurs here.  It might be an HTTP invocation over the network triggering XML parsing of data in the payload, or maybe files being uploaded to a Web page ( in the case of Equifax).

        // 3. The object deserialization might happen elsewhere in the system, perhaps on another tier, in another datacenter.
        // It is during this method invocation the exploit gets triggered by this hapless app.
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