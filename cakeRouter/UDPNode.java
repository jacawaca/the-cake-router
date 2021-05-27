/**
 * 
 */
package cakeRouter;

import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;  

/**
 * @author Jacek Strzałkowski jacek.strzalkowski.stud@pw.edu.pl
 * @author Paweł Polak
 * @author Ryszard Michalski
 *
 */
public class UDPNode {
	private static InetAddress prevAdress[] = {null,null};
	private static InetAddress nextAdress[] = {null,null};
	private static boolean debug=false;
	private static String defaultReturnMessage = "Wiadomość dotarła. Brak odpowiedzi.";
	private static String returnMessage = defaultReturnMessage;
	private static Scanner sc;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception{
		 	System.out.println("Gałąź ruszyła!");
	        //Otwarcie gniazda z okreslonym portem
	        DatagramSocket datagramSocket = new DatagramSocket(Config.PORT);

	        byte[] byteResponse = "Uzyskałem wiadomość.".getBytes("utf8");
	        
            sc = new Scanner(System.in);

            // Do skanera
            String typedMessage, fileNameOut;
            boolean doYouWant2File;
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
	            	int nchange=0;
	            	if(nextAdress[0] == null && nextAdress[1] == null &&
	            			(prevAdress[0]==null || prevAdress[1] == null)
	            			) {
	            		// sytuacja węzła docelowego. Pośrednie mają 
			            // Zapisujemy do pamięci poprzedni adres
	            		if(prevAdress[0] == null) nchange = 0;
	            		else if(prevAdress[1] == null) nchange = 1;
	            		
	            		prevAdress[nchange] = address;
			            
			            // Wyświetlamy wiadomość
			            System.out.println(partsOfString[0]);
			            System.out.println("Czy chcesz zapisać wiadomość do pliku tekstowego. Wpisz wartość logiczną");
			            doYouWant2File = sc.nextBoolean(); sc.nextLine();
			            if(doYouWant2File) {
			            	System.out.println("Wpisz nazwę pliku. Pamiętaj o końcówce .txt i uważaj, żeby nic nie nadpisać.");
			            	
			            	while(true) {
			            		fileNameOut = sc.nextLine();
			            		if(! fileNameOut.isBlank()) break;
			            	}
			            	      	
				            FileWriter msgWriter = new FileWriter(fileNameOut);
				            msgWriter.write(partsOfString[0]);
				            msgWriter.close();
			            }
			            // odpisanie
			            System.out.println("Proszę wpisać poniżej odpowiedź. Nie używaj średnika ;");
			            while(true) {
			            	typedMessage = sc.nextLine();
			            	if(! typedMessage.contains(";")) break;
			            	else System.err.println("Użyłeś średnika. Spróbuj jeszcze raz.");
			            }
			            if(typedMessage.isBlank()) returnMessage = defaultReturnMessage;
			            else returnMessage = typedMessage;
	            		System.out.println("Wysyłam:\n"+returnMessage);
	            	}
	            	else { // powrót. Węzły pośrednie
	            		returnMessage = partsOfString[0];
	            		// Z którego adresu przyszła wiadomość
	            		if(nextAdress[0] != null && address.getHostAddress() == nextAdress[0].getHostAddress()) {
	            			nextAdress[0] = null;
	            			if(nextAdress[1] != null) {
	            				nextAdress[0] = nextAdress[1]; nextAdress[1] =null;
	            			}
	            		}
	            		else if(nextAdress[1] != null && address.getHostAddress() == nextAdress[1].getHostAddress()) {
	            			nextAdress[1] = null;
	            		}
	            	}
		            
	            	System.out.println("Wiadomość dotarła. Odsyłam wiadomość na poprzedni adres: "+prevAdress);
	            	// Odsyłamy (POPRAWKA)
	            	if(debug) System.out.println("NEXT ADRESS = "+prevAdress);
	            	InetAddress serverAddress = prevAdress[nchange];
	            	
	            	// usuwamy odpowiedni prevAdress
	            	
	            	if(nchange == 1) {
	            		prevAdress[1] = null;
	            	}
	            	else if(nchange == 0) {
	            		prevAdress[0] = prevAdress[1];
	            		prevAdress[1] = null;
	            	}
	            	
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
	            	int nchange =0;
//		            // Zapisujemy do pamięci poprzedni adres
	            	if(prevAdress[0] == null) nchange=0;
	            	else if(prevAdress[1] == null) nchange = 1;
	            	else {
	            		System.err.println("Błąd! Node potrafi obsłużyć tylko dwóch klientów równolegle. Poczekaj.");
	            		System.exit(1);
	            	}
	            	prevAdress[nchange] = address;
	            	
	            	String nextAdressString = partsOfString[0];
	            	String message2Send="";
	            	for(int i=1;i<partsOfString.length;i++) message2Send += partsOfString[i]+";";
	            	// Wysyłka
	            	// UDPClient
	            	if(debug) System.out.println("Paczka do przesłania\n"+message2Send);
		            InetAddress serverAddress = InetAddress.getByName(nextAdressString); //zamiast "localhost"
		            
		            nextAdress[nchange] = serverAddress;
		            
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
		                if(debug) System.out.println("Serwer otrzymał wiadomość");
		            }catch (SocketTimeoutException ste){
		                if(debug) System.out.println("Serwer nie odpowiedział, więc albo dostał wiadomość albo nie...");
		            }
		            // Koniec wysyłki
	            }
	            
	            else System.out.println("Zła wiadomość");
 
	            
	            
	        }
	    }
	}
