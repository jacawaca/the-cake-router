#!/bin/sh
# Czy java jest zainstalowana
command -v java > /dev/null ||
  { >2& echo "Java nie jest zainstalowana. Wyłączam…" && exit ; }
java=java
test -n "$JAVA_HOME" && java="$JAVA_HOME/bin/java"
# Uruchomienie programu
echo "Uruchamiam program."
exec "$java" -jar UDPNode.jar
echo "Program wyłączony."
