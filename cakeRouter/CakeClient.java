/**
 * @author Ryszard Michalski
 * @author Paweł Polak
 * @author Jacek Strzałkowski jacek.strzalkowski.stud@pw.edu.pl
 */
package cakeRouter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CakeClient {
	private static String adress; // wysyłka
	private static List<String> nodeAdresses;
	private static String message;
	private static boolean debug = false;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, Exception {
		// Wczytanie węzłów pośrednich
		adress = args[0];
		Path adressesFileName = Path.of(args[1]), 
		messageFileName = Path.of(args[2]);
		message = Files.readString(messageFileName);
		nodeAdresses = Files.readAllLines(adressesFileName);
		
		// Dodajemy adres docelowy na koniec listy węzłów
		nodeAdresses.add(adress);
		
		// Wysyłka na adres
		String stringServerAdress = nodeAdresses.get(0);
        InetAddress serverAddress = InetAddress.getByName(stringServerAdress); //zamiast "localhost"
        if(debug) System.out.println(serverAddress);

        DatagramSocket socket = new DatagramSocket(); //Otwarcie gniazda
        
    	// Kodujemy wiadomość. Na początku adresy potem sam msg. Obcinamy pierwszy adres
 		for(int i=nodeAdresses.size()-1;i>0;i--) {
 			message = nodeAdresses.get(i) + ";" + message;
 		}
 		
        byte[] stringContents = message.getBytes("utf8"); //Pobranie strumienia bajtów z wiadomosci

        DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
        sentPacket.setAddress(serverAddress);
        sentPacket.setPort(Config.PORT);
        if(debug) System.out.println("Wysyłam"+message+" na "+serverAddress.toString());
        socket.send(sentPacket);

        DatagramPacket recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
        socket.setSoTimeout(1010);

        try{
            socket.receive(recievePacket);
            if(debug) System.out.println("1. węzeł otrzymał wiadomość");
        }catch (SocketTimeoutException ste){
            if(debug) System.out.println("1. węzeł nie odpowiedział.");
        }
        
        // Czekanie na odpowiedź od adresata
        //Otwarcie gniazda z okreslonym portem
        
        DatagramSocket datagramSocket = new DatagramSocket(Config.PORT);

        byte[] byteResponse = "OK".getBytes("utf8");

        while (true){

            DatagramPacket receivedPacket
                    = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);

            datagramSocket.receive(receivedPacket);

            int length = receivedPacket.getLength();
            String message =
                    new String(receivedPacket.getData(), 0, length, "utf8");

            // Port i host który wysłał nam zapytanie
            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();

            System.out.println(message);
            Thread.sleep(1000); //To oczekiwanie nie jest potrzebne dla
            // obsługi gniazda

            DatagramPacket response
                    = new DatagramPacket(
                        byteResponse, byteResponse.length, address, port);

            datagramSocket.send(response);
            System.exit(0);
        }
		
	}

}
