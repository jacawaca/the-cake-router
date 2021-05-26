/**
 * 
 */
package cakeRouter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import udp.Config;

/**
 * @author Jacek Strzałkowski jacek.strzalkowski.stud@pw.edu.pl
 *
 */
public class UDPNode {

	 public static void main(String[] args) throws Exception{
		 	System.out.println("Gałąź ruszyła!");
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
	            
	            // Na razie na chama spróbuję rozdzielić stringa na część w której znajduje się następny adres
	            // i część w której jest wiadomość. Powinniśmy to potem ulepszyć?
	            String[] partsOfString = message.split(";");
	            String nextAdress = null, sendenMessage = null;
	            if(partsOfString.length == 2) {
	            nextAdress = partsOfString[0];
	            sendenMessage = partsOfString[1];

	            System.out.println("Otrzymałem "+sendenMessage+" teraz wysyłam na "+nextAdress);
	            }
	            else if(partsOfString.length == 1) {
	            	System.out.println("Otrzymałem "+sendenMessage+" Jestem węzłem końcowym. Odsyłam na "+nextAdress);
	            }
	            
	            else System.out.println("Zła wiadomość");
	            
	            
	            
	            
	            
	            
       
	            
	            // Reszta kodu dla serwera
	            Thread.sleep(1000); //To oczekiwanie nie jest potrzebne dla
	            // obsługi gniazda

	            DatagramPacket response
	                    = new DatagramPacket(
	                        byteResponse, byteResponse.length, address, port);

	            datagramSocket.send(response);
	            
	            
	            // Wysyłam
	            // UDPClient
	            String message2Send =  sendenMessage;
	            InetAddress serverAddress = InetAddress.getByName(nextAdress); //zamiast "localhost"
	            System.out.println(serverAddress);

	            DatagramSocket socket = new DatagramSocket(); //Otwarcie gniazda
	            byte[] stringContents = message2Send.getBytes("utf8"); //Pobranie strumienia bajtów z wiadomosci

	            DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
	            sentPacket.setAddress(serverAddress);
	            sentPacket.setPort(Config.PORT);
	            socket.send(sentPacket);

	            DatagramPacket recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
	            socket.setSoTimeout(1010);

	            try{
	                socket.receive(recievePacket);
	                System.out.println("Serwer otrzymał wiadomość");
	            }catch (SocketTimeoutException ste){
	                System.out.println("Serwer nie odpowiedział, więc albo dostał wiadomość albo nie...");
	            }
	            
	            
	            
	        }
	    }
	}
