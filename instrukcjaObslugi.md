## Opis działania programu
Klient będzie mógł uruchamiać program poprzez poprzez prosty alias. Na potrzeby robocze
załóżmy, że będzie to cake-router. Aby to osiągnąć, wystarczy
wykonać na przykład
```sh
$ sudo echo "export cake-router=./path/to/file/CakeClient.sh" >> ~/.bash_aliases
$ cd ~
$ . .bashrc
```
Zakładamy, że program będzi mógł być używany w następujący sposób
``` bash
usage: cake-router [options]
options:
-l --list                             Wyświetla listę dostępnych hostów.  
``````
~~Mogą posłużyć do wyboru węzłów pośrednich przy próbie wysłania~~
Będzie wczytywał plik config
```bash
-s --send [adress] ~~[trasa]~~ [num]
```
Wysyła komunikat na podany adres.
W przypadku gdyby adres nie został podany, użytkownik ma możliwość
wyboru adresu z listy. Ewentualnie wyświetli mu się taka lista,
na podstawie której będzie mógł wybrać adres.
~~Gdyby trasa nie została podana, to program - o ile to możliwe - 
użyje domyślnej (wczyta z pliku). Jest możliwość wyboru
ilości węzłów pośredniczących [num]. Wówczas te węzły są losowe.~~
```
--set-trasa                           Ustala domyślną trasę.
```
Program generujący plik konfiguracyjny (FUNKCJONALNOŚĆ OPCJONALNA)
```
--listen                              Nasłuchuj komunikatów.

--help                                Wyświetla ten komunikat.
```
Rozpatrujemy możliwość zadeklarowania trasy poprzez specjalny
plik ~~lub odpowiednio zadeklarowaną przy wywołaniu programu
listę.~~ lub poprzez miniprogram, który uruchomi na się przy użyciu opcji
--set-trasa.
## Obsługa programu
Klient uruchamia program cake-router w bashu poprzez wpisanie
``` bash
$ cake-router
```
Wyświetla mu się informacja o możliwych opcjach. Następnie
uruchamia program z opcją list
``` bash
$ cake-router -l
```
Dostaje informacje o ~~możliwych węzłach pośrednich (i docelowych).~~ węzłach
pośrednich z listy conf.

~~Przygotowuje trasę i wysyła komunikat np na adres lokalny 192.168.1.03 poprzez 3 węzły pośredniczące~~

Po wysłaniu użytkownik programu oczekuje na odpowiedź od nadawcy.
