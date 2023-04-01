#!/usr/bin/env bash
# © 2023 iamfortress.net
# Let's try a bit of mischief...
message="You've been hacked"
fileName=YouveBeenHacked
echo $message
echo "CLASSPATH="$CLASSPATH
echo $message'!' > $fileName
echo "USER="$USER >> $fileName
echo "PWD="$PWD >> $fileName
echo "JAVA CLASSPATH="$CLASSPATH >> $fileName
echo "YOUR PASSWORDS=" >> $fileName
cat '/etc/passwd' >> $fileName
