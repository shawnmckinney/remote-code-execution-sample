# serial-exploit-sample
Example shows how to use the Java Security Manager to prevent serialization exploits.

## Intro to the Problem

 * The Problem, Equifax Breach, 143 million Americans’ personal info, including names, addresses, dates of birth and SSNs compromised.
 * Only a veneer of security in place.

## The Exploit

 * The vulnerability was Apache Struts, CVE-2017.
 * http://www.zdnet.com/article/equifax-confirms-apache-struts-flaw-it-failed-to-patch-was-to-blame-for-data-breach/

## How Does It Work?

 * Input data deserialized into an executable object with privilege.
 * http://blog.diniscruz.com/2013/12/xstream

## The Solution

 * Of course you need to ensure all appropriate patches are installed to cover known defects.
 * But what about unknown defects?
 * Employ mandatory access controls like Java Security Manager to your runtime environment.

## Instructions

1. Clone the serial-exploit-sample

 ```
 git clone https://github.com/shawnmckinney/serial-exploit-sample.git
 ```

2. Edit my-java.policy file, point to project source folder:

 ```
 vi src/main/resources/my-java.policy
 ...
 grant codeBase "file:${user.home}/Development/serial-exploit-sample/-" {
 ```

 For example, if you cloned into '/home/myuser/Development', it would look like this:

 ```
 grant codeBase "file:${user.home}/Development/serial-exploit-sample/-" {
 ```

 Of course this all depends on the user's home dir locale, under which the java program runs. If you're not sure, use an absolute path:
 ```
 grant codeBase "file:/home/myuser/Development/serial-exploit-sample/-" {
 ```

3. Build and run the test program.  Make sure the -Dserial-exploit-sh points to the correct folder on your machine:

 ```
 mvn clean install
 java -cp target/serialExploitTest-1.0-SNAPSHOT.jar
      -Djava.security.manager
      -Djava.security.policy=src/main/resources/my-java.policy
      -Dserial-exploit-sh=/home/myuser/Development/serial-exploit-sample/src/main/resources/hacker-script.sh
      com.example.App
 ```

4. Examine the program output.

 ```
 Begin serial exploit test....
 Input: duke moscone center
 Serialized data is saved in myObject.ser
 BadCode will now run hacker script
 user.home=/home/myuser
 execute hacker command=/home/myuser/Development/serial-exploit-sample/src/main/resources/hacker-script.sh
 system command has been run....
 Result: foo fighters!
 ```

5. Examine the files in package:
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

6. View the contents of YouveBeenHacked:

 ```
 myuser@ubuntu:~/Development/serial-exploit-sample$ cat YouveBeenHacked
 root:x:0:0:root:/root:/bin/bash
 daemon:x:1:1:daemon:/usr/sbin:/usr/sbin/nologin
 ...
 ```

7. What just happened?  If the test was 'successful', a rogue script executed during the deserialzation process and copied /etc/passwd to its file. This is an example of how a system command can be executed during that operation.

8. Now change the policy.  Edit my-java.policy, comment out the permission to allow the script to execute:

 ```
 vi src/main/resources/my-java.policy
 ...
 grant codeBase "file:${user.home}/Development/serial-exploit-sample/-" {
 // must specifically allow file to execute:
 //permission java.io.FilePermission "./src/main/resources/hacker-script.sh", "execute";
 ```

9. Rerun the program and view the output:

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

 The rogue program cannot execute a system command if that specific permission hasn't been added to its codebase in the java.policy.

10. Now reenable the permission in my-java.policy but remove the unix file permission to execute, rerun program.

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

11. The takeaway?

 Usage of the Java Security Manager and strict unix file system controls can limit the damage that can be inflicted during Java object deserialzation - specifically preventing a remote code execution vulnerability.

12. Run with Java Security Manager debug enabled:

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
 access: access allowed ("java.lang.reflect.ReflectPermission" "suppressAccessChecks")     <-- this one you should be wary of.
 ...
 ```

 There are tons of useful forensic information in this output.  Take the time to understand all of the system and file commands your program is invoking. It will help you understand the req's to secure it.

 12. One more thing.  The Java Security Manager is not a perfect solution.  There are caveats.  For example, parsing data using standard parsers means you will have to add this permission:

 ```
 permission java.lang.reflect.ReflectPermission "suppressAccessChecks";
 ```

 Which opens vulnerabilities in your program by breaking encapsulation barriers -- via reflection.  Unfortunately right now there's not much we can do about this.  Try to limit the damage using controls such as these.

