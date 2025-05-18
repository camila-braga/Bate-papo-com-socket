package Entities;

//import java.util.*;
import java.io.*;
import java.net.*;

public class ClientTCP {

	public static void main(String[] args) throws Exception {
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Name: ");
		String name = inFromUser.readLine(); 
		    
        Socket clientSocket = new Socket("localhost", 6789); //localhost = testando no meu pc; conectando na porta 6789
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        outToServer.writeBytes(name + "\n");
        
        final boolean[] disconnected = {false}; //para mudar só o conteúdo
        
        // Enviando mensagens        
        new Thread(() -> {
        	try {
        		
        		while (true) {
        			String mensagem = inFromUser.readLine();
        			
        			if (mensagem == null || mensagem.trim().isEmpty()) {
        		        continue; // ignora o 'enter'
        		    }
        			
        			if (mensagem.equalsIgnoreCase("sair")) {
        				disconnected[0] = true;
        				outToServer.writeBytes(mensagem + "\n"); //avisa ao servidor que saiu do bate-papo
        				clientSocket.close();
        				break;
                        
                    }else {
                    	outToServer.writeBytes(mensagem + "\n"); //envia a mensagem ao servidor
                    }
        			
        		}
        	}catch (IOException e) {
        		if (!disconnected[0]) {
                    System.out.println("You were disconnected.");
                }
            }
        }).start();
        
        
        //Recebendo mensagens
        new Thread(() -> {
            try {
                String message;
                while ((message = inFromServer.readLine()) != null) {
                    System.out.println(message); // mensagem recebida
                }
            } catch (IOException e) {
            	if (!disconnected[0]) {
                    System.out.println("You were disconnected.");
                }
            }
        }).start();
        
    }

}
