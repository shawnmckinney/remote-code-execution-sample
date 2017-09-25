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
      -Djava.security.debug="access,failure"
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
 myuser@ubuntu:~/Development/serial-exploit-sample$ ls -l
 total 32
 -rw-rw-r-- 1 myuser myuser 1211 Sep 23 16:37 LICENSE
 -rw-rw-r-- 1 myuser myuser  107 Sep 24 20:59 myObject.ser                <--- this file is new but supposed to be there
 -rw-rw-r-- 1 myuser myuser  819 Sep 23 16:40 pom.xml
 -rw-rw-r-- 1 myuser myuser 2162 Sep 24 21:01 README.md
 drwxrwxr-x 4 myuser myuser 4096 Sep 23 16:37 src
 drwxrwxr-x 9 myuser myuser 4096 Sep 23 19:44 target
 -rw-rw-r-- 1 myuser myuser 2031 Sep 24 20:59 YouveBeenHacked             <--- this file is new but should not be here.
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

 The rogue program cannot execute a system command if that specific permission has not been granted to the codebase.

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