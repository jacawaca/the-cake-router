/**
 * 
 */
package cakeRouter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import udp.Config;

import java.util.ArrayList;  
import java.util.Arrays;  
import java.util.List;  

/**
 * @author Jacek Strzałkowski jacek.strzalkowski.stud@pw.edu.pl
 *
 */
public class UDPNode {
	static String prevAdress;
	private static boolean debug=true;
	// TODO możliwość ręcznego wpisania?
	// Wyświetla się pytanie: czy chesz ręcznie wpisać
	// i wybierasz. Jak nie, to idzie domyślna.
	private static String returnMessage = "Paczka dotarła cała i zdrowa.";
	
	 public static void main(String[] args) throws Exception{
		 	System.out.println("Gałąź ruszyła!");
	        //Otwarcie gniazda z okreslonym portem
	        DatagramSocket datagramSocket = new DatagramSocket(Config.PORT);

	        byte[] byteResponse = "Uzyskałem wiadomość.".getBytes("utf8");

	        while (true){

	            DatagramPacket receivedPacket
	                    = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);

	            datagramSocket.receive(receivedPacket);

	            int length = receivedPacket.getLength();
	            String message =
	                    new String(receivedPacket.getData(), 0, length, "utf8");

	            // Port i host który wysłał nam zapytanie
	            InetAddress address = receivedPacket.getAddress();

	            
	            System.out.println("Przyjąłem od "+address.toString());
	            
	            int port = receivedPacket.getPort();
	            
	            
	            // Reszta kodu dla serwera
	            Thread.sleep(1000); //To oczekiwanie nie jest potrzebne dla
	            // obsługi gniazda

	            DatagramPacket response
	                    = new DatagramPacket(
	                        byteResponse, byteResponse.length, address, port);

	            datagramSocket.send(response);
	            
	            // Na razie spróbuję rozdzielić stringa na część w której znajduje się następny adres
	            // i część w której jest wiadomość. Powinniśmy to potem ulepszyć?
	            String[] partsOfString = message.split(";"); // senden message - 1
	                     
	            if(partsOfString.length == 1) { // Powrót
	            	
	            	if(prevAdress==null) {
			            // Zapisujemy do pamięci poprzedni adres
			            prevAdress = address.toString(); // TODO może to jakoś polepszyć. Zrobić pole z adress?
			            //Na razie InetAdress::toString dodaje / na początku. Usunę to ręcznie
			            prevAdress = prevAdress.substring(1);
	            	}
		            
	            	System.out.println("Wiadomość dotarła. Odsyłam wiadomość na poprzedni adres: "+prevAdress);
	            	// Odsyłamy (POPRAWKA)
	            	if(debug) System.out.println("NEXT ADRESS = "+prevAdress);
	            	InetAddress serverAddress = InetAddress.getByName(prevAdress);
		            System.out.println(serverAddress);
	
		            DatagramSocket socket = new DatagramSocket(); //Otwarcie gniazda
		            byte[] stringContents = returnMessage.getBytes("utf8"); //Pobranie strumienia bajtów z wiadomosci
	
		            DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
		            sentPacket.setAddress(serverAddress);
		            sentPacket.setPort(Config.PORT);
		            socket.send(sentPacket);
		            
		            // Czekamy na odpowiedź
		            
		            DatagramPacket recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
		            socket.setSoTimeout(1010);
	
		            try{
		                socket.receive(recievePacket);
		                System.out.println("Serwer otrzymał wiadomość");
		            }catch (SocketTimeoutException ste){
		                System.out.println("Serwer nie odpowiedział, więc albo dostał wiadomość albo nie...");
		            }
		            // Koniec wysyłki
	            	
	            }
	            else if(partsOfString.length > 1) { // Gdy są podane adresy
	            	
		            // Zapisujemy do pamięci poprzedni adres
		            prevAdress = address.toString(); // TODO Może ulepszyć jak wyżej
		            //Na razie InetAdress::toString dodaje / na początku. Usunę to ręcznie
		            prevAdress = prevAdress.substring(1);         	
	            	
	            	String nextAdress = partsOfString[0];
	            	String message2Send="";
	            	for(int i=1;i<partsOfString.length;i++) message2Send += partsOfString[i]+";";
	            	// Wysyłka
	            	// UDPClient
	            	if(debug) System.out.println("Paczka do przesłania\n"+message2Send);
		            InetAddress serverAddress = InetAddress.getByName(nextAdress); //zamiast "localhost"
		            System.out.println(serverAddress);
	
		            DatagramSocket socket = new DatagramSocket(); //Otwarcie gniazda
		            byte[] stringContents = message2Send.getBytes("utf8"); //Pobranie strumienia bajtów z wiadomosci
	
		            DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
		            sentPacket.setAddress(serverAddress);
		            sentPacket.setPort(Config.PORT);
		            socket.send(sentPacket);
		            
		            // Czekamy na odpowiedź
		            
		            DatagramPacket recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
		            socket.setSoTimeout(1010);
	
		            try{
		                socket.receive(recievePacket);
		                System.out.println("Serwer otrzymał wiadomość");
		            }catch (SocketTimeoutException ste){
		                System.out.println("Serwer nie odpowiedział, więc albo dostał wiadomość albo nie...");
		            }
		            // Koniec wysyłki
	            }
	            
	            else System.out.println("Zła wiadomość");
 
	            
	            
	        }
	    }
	}
