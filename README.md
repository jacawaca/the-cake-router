# The cake router
Implementacja małej sieci wykorzystującej trasowanie cebulowe.
Połączenie klienta z odbiorcą wygląda następująco.
W pierwszej kolejności klient wybiera listę węzłów P1,P2,…,PN,WK
po których chce dotrzeć do adresata.
Następnie nadaje paczkę informacji {dane,adres[P2-ADRESAT]} do 
P1. Dalej P1 zapisuje adres klienta i wysyła paczkę
{dane,adres[P3-ADRESAT]} do P2. Proces się powtarza aż węzeł końcowy - WK - wyślę pakiet do adresata.

![Trasowanie cebulowe](fig/wysylka.gif)

Droga w kierunku przeciwnym jest następująca: adresat wysyła
odpowiedź do WK. Z kolei z WK jest wysyłana ta odpowiedź do PN,
do WK zapisał adres. Dalej PN przesyła odpowiedź do PN-1 itd., aż
odpowiedź wróci do klienta.
