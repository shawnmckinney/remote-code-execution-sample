#!/usr/bin/env bash
# no license
echo "You've been hacked"
echo "CLASSPATH="$CLASSPATH
echo "You've been hacked!"  > YouveBeenHacked
echo "USER="$USER >> YouveBeenHacked
echo "PWD="$PWD >> YouveBeenHacked
echo "JAVA CLASSPATH="$CLASSPATH >> YouveBeenHacked
echo "YOUR PASSWORDS=" >> YouveBeenHacked
cat '/etc/passwd' >> YouveBeenHacked