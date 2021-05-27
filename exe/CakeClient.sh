#!/bin/bash
# Czy java jest zainstalowana

N_PARAMETERS="$#"

command -v java > /dev/null ||
  { >2& echo "Java nie jest zainstalowana. Wyłączam…" && exit ; }
java=java
[ -z "$JAVA_HOME" ] && echo 'Zmienna JAVA_HOME nie ustawiona.
Biorę z /bin/java'
test -n "$JAVA_HOME" && java="$JAVA_HOME/bin/java"

# Czy argumenty zostały dobrze przekazane?
# [ "$#" -eq 2 ] && echo '2' ||
#  { >2& echo "Zła liczba argumentów" && exit ; }

TASK=$1
ARG2=$2
ARG3=$3
CONFIG_FILE="adressess.config"
JAR_FILE="CakeClient.jar"

fListen () {
  [ -f "$CONFIG_FILE" ] && cat "$CONFIG_FILE" ||
    { >2& echo "Błąd uruchomiania pliku."  && exit ; };
  printf "\nKoniec.\n"
}

fSend () {
  # Czy argumenty zostały dobrze przekazane?
  [ "$N_PARAMETERS" -eq 3 ]  && [ -f "$ARG3" ] ||
   { >2& echo "Błąd parametrów." && exit ; }
  ADRES="$ARG2"; MSG_FILE="$ARG3"
  ! [ -f "$CONFIG_FILE" ] && echo "Brak pliku konfiguracyjnego" &&
    exit
  exec "$java" -jar $JAR_FILE $ADRES $CONFIG_FILE $MSG_FILE
}

fSetTrasa () {
  # check if select installed
  echo "Upewnij się czy pakiet select jest zainstalowany.
  Inaczej poniższe działanie jest nieprzewidywalne."
  # Generacja pliku konfiguracyjnego
  echo "Podaj pierwszy adres."
  read ADRES1 && echo "Wczytano $ADRES1"
  echo "Podaj drugi adres."
  read ADRES2 && echo "Wczytano $ADRES2"
  [ -f "$CONFIG_FILE" ] && echo "Plik konfiguracyjny już istnieje.
  Czy chcesz go nadpisać?"
  select yn in "Yes" "No"
  do
  case $yn in
    No) exit ;;
    Yes) rm $CONFIG_FILE && echo "$ADRES1" >> $CONFIG_FILE &&
      echo "$ADRES2" && echo "$ADRES2" >> $CONFIG_FILE && exit
      ;;
  esac
  done
}

# Obsługa argumentów
case "$TASK" in
  -l | --list) fListen ;;
  -s | --send) fSend ;;
  --set-trasa) fSetTrasa ;;
  -h | --help) printf "usage CakeClient.sh [opt] message.txt
    HELP:
    Program wysyłający wiadomość poprzez sieć Tor. W pliku
    konfiguracyjnym znajdują się adresy kolejnych węzłów pośrednich.
    Przygotuj plik tekstowy zawierający wiadomość. Upewnij się,
    że nie zawiera on średników ;.
    Możesz sprawdzić, czy plik został wysłany poprawnie używając
    sha256sum msg.txt
    u klienta oraz adresata.
    opt:
    -l --list\t Odczytuje plik konfiguracyjny
    -s --send adres msg.txt\t Wysyła plik msg.txt na adres,
    który ma mieć formę adresu IPv4
    --set-trasa\t Wygeneruj plik konfiguracyjny, który umożliwi
    wybór trasy";;
  *) echo "Błąd. Użyj --help" ;;
esac

printf "\n"
