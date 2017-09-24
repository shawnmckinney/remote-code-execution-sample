# serial-exploit-sample
Example shows how to use the Java Security Manager to prevent Java serialization exploits.


1. Edit MySystem.java

2. Edit hacker-scripts-sh

3. edit my-java.policy file

 * examine the permissions
 * set path as needed

 ```
 grant codeBase "file:${user.home}/Development/serial-exploit-sample/-" {
 ```

4. Run the test

5. hacker-xscript a+x

6. Execute the program from the root folder:

 ```
 java -cp target/serialExploitTest-1.0-SNAPSHOT.jar
      -Djava.security.manager
      -Djava.security.policy=src/main/resources/my-java.policy
      -Djava.security.debug="access,failure"
      -Dserial-exploit-sh=/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-script.sh
      com.example.App
 ```


 ```
 java -cp target/serialExploitTest-1.0-SNAPSHOT.jar -Djava.security.manager -Djava.security.policy=src/main/resources/my-java.policy -Djava.security.debug="access,failure" -Dserial-exploit-sh=/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-script.sh com.example.App
 ```


 * java security exception

 ```
 Exception in thread "main" java.security.AccessControlException: access denied ("java.io.FilePermission" "/home/smckinn/Development/serial-exploit-sample/src/main/resources/hacker-scripts.sh" "execute")
	at java.security.AccessControlContext.checkPermission(AccessControlContext.java:472)
 ```

 * unix file security









