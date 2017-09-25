#!/usr/bin/env bash
# no license
echo "You've been hacked"
echo 'CLASSPATH='$CLASSPATH
echo "You've been hacked ur CLASSPATH="$CLASSPATH >> YouveBeenHacked
echo "ur passwords=" >> YouveBeenHacked
cat '/etc/passwd' > YouveBeenHacked