# serial-exploit-sample
Example shows how to use the Java Security Manager to prevent Java serialization exploits.

1. edit my-java.policy file

 * Point the codebase to the root folder of where you installed the project source:

 ```
 grant codeBase "file:${user.home}/Development/serial-exploit-sample/-" {
 ```

2. Build and run the test program.  Make sure the -Dserial-exploit-sh points to the correct folder on your machine:

 ```
 mvn clean install
 java -cp target/serialExploitTest-1.0-SNAPSHOT.jar
      -Djava.security.manager
      -Djava.security.policy=src/main/resources/my-java.policy
      -Dserial-exploit-sh=/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-script.sh
      com.example.App
 ```

3. Examine the program output.

 ```
 myuser@ubuntu:~/Development/serial-exploit-sample$ java -cp target/serialExploitTest-1.0-SNAPSHOT.jar -Djava.security.manager -Djava.security.policy=src/main/resources/my-java.policy -Dserial-exploit-sh=/home/myuser/Development/serial-exploit-sample/src/main/resources/hacker-script.sh com.example.App
 Begin serial exploit test....
 Input: duke moscone center
 Serialized data is saved in myObject.ser
 BadCode will now run hacker script
 user.home=/home/myuser
 execute hacker command=/home/myuser/Development/serial-exploit-sample/src/main/resources/hacker-script.sh
 system command has been run....
 Result: foo fighters!
 ```

4. Examine the files in root folder:
 ```
 myuser@ubuntu:~/Development/serial-exploit-sample$ ls
 total 32
 LICENSE
 myObject.ser                <--- this file is new but supposed to be there
 pom.xml
 README.md
 src
 target
 YouveBeenHacked             <--- this file is new but should not be here.
 ```

5. View the contents of YouveBeenHacked:

 ```
 myuser@ubuntu:~/Development/serial-exploit-sample$ cat YouveBeenHacked
 root:x:0:0:root:/root:/bin/bash
 daemon:x:1:1:daemon:/usr/sbin:/usr/sbin/nologin
 ...
 ```

6. What just happened?  If the test was 'successful', a rogue script executed during the deserialzation process.  This is an example of a system command can be executed.

7. Now change the policy.  Edit my-java.policy, comment out the permission to allow the script to execute:

 ```
 grant codeBase "file:${user.home}/Development/serial-exploit-sample/-" {
 // must specifically allow file to execute:
 //permission java.io.FilePermission "./src/main/resources/hacker-script.sh", "execute";
 ```

8. Rerun the program and view the output:

 ```
 myuser@ubuntu:~/Development/serial-exploit-sample$ java -cp target/serialExploitTest-1.0-SNAPSHOT.jar -Djava.security.manager -Djava.security.policy=src/main/resources/my-java.policy -Dserial-exploit-sh=/home/myuser/Development/serial-exploit-sample/src/main/resources/hacker-script.sh com.example.App
 Begin serial exploit test....
 Input: duke moscone center
 Serialized data is saved in myObject.ser
 BadCode will now run hacker script
 user.home=/home/smckinn
 execute hacker command=/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-script.sh
 Exception in thread "main" java.security.AccessControlException: access denied ("java.io.FilePermission" "/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-script.sh" "execute")
	at java.security.AccessControlContext.checkPermission(AccessControlContext.java:472)
	at java.security.AccessController.checkPermission(AccessController.java:884)
	at jav
 ```

 The rogue program cannot execute a system command if that specific permission ha```s not been granted to the codebase.

9. Now reenable the permission in my-java.policy but remove the unix file permission to execute, rerun program.

 ```
 myuser@ubuntu:~/Development/serial-exploit-sample$ chmod a-x src/main/resources/hacker-script.sh
 myuser@ubuntu:~/Development/serial-exploit-sample$ java -cp target/serialExploitTest-1.0-SNAPSHOT.jar -Djava.security.manager -Djava.security.policy=src/main/resources/my-java.policy -Dserial-exploit-sh=/home/myuser/Development/serial-exploit-sample/src/main/resources/hacker-script.sh com.example.App
 Begin serial exploit test....
 Input: duke moscone center
 Serialized data is saved in myObject.ser
 BadCode will now run hacker script
 user.home=/home/smckinn
 execute hacker command=/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-script.sh
 ERROR: serialize caught IOException=java.io.IOException: Cannot run program "/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-script.sh"
 Exception in thread "main" java.lang.RuntimeException: serialize caught IOException=java.io.IOException: Cannot run program "/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-script.sh"
 	at com.example.App.deserialize(App.java:67)
 	at com.example.App.main(App.java:26)
 ```

 Now the program cannot execute the script because of common unix file permission.

10. The takeway?

 Usage of the Java Security Manager and strict unix file system controls can limit the damage that can be inflicted during Java object deserialzation - specifically preventing a remote code execution vulnerability.

11. Run with Java Security Manager debug enabled:

 ```
 mvn clean install
 java -cp target/serialExploitTest-1.0-SNAPSHOT.jar
 ...
      -Djava.security.debug="access,failure"     <--- add this param
      com.example.App
 ```

 and view the output:

 ```
 myuser@ubuntu:~/Development/serial-exploit-sample$ java -cp target/serialExploitTest-1.0-SNAPSHOT.jar -Djava.security.manager -Djava.security.policy=src/main/resources/my-java.policy -Dserial-exploit-sh=/home/myuser/Development/serial-exploit-sample/src/main/resources/hacker-script.sh -Djava.security.debug="access,failure" com.example.App
 Begin serial exploit test....
 Input: duke moscone center
 access: access allowed ("java.io.FilePermission" "/home/myuser/Development/serial-exploit-sample/target/serialExploitTest-1.0-SNAPSHOT.jar" "read")
 access: access allowed ("java.util.PropertyPermission" "sun.io.serialization.extendedDebugInfo" "read")
 access: access allowed ("java.lang.RuntimePermission" "reflectionFactoryAccess")
 access: access allowed ("java.lang.RuntimePermission" "accessDeclaredMembers")
 access: access allowed ("java.lang.RuntimePermission" "accessDeclaredMembers")
 access: access allowed ("java.lang.RuntimePermission" "accessDeclaredMembers")
 ...
 ```

 There is tons of useful forensic information in this output.  Take the time to understand all of the system and file commands your program is invoking. It will help you understand the req's to secure it.

 12. One more thing.  The JSM is not a perfect solution.  There are caveats.  For example for parsing data using standard parsers, you will have to add this:

 ```
 permission java.lang.reflect.ReflectPermission "suppressAccessChecks";
 ```

 Which opens a set of vulnerabilities in your program.  Use the system security facilities like Java's Security Manager, and other operating system facilities like Unix file security to lock it back down.


