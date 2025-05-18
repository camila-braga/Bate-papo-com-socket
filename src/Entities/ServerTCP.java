package Entities;

import java.util.*;
import java.io.*;
import java.net.*;

public class ServerTCP {
	
	private static class ConnectedClient {
        String name;
        DataOutputStream out;

        ConnectedClient(String nome, DataOutputStream out) {
            this.name = nome;
            this.out = out;
        }
    }
		
	private static List<ConnectedClient> clients = Collections.synchronizedList(new ArrayList<>()); //lista de clientes conectados no bate-papo
	
	public static void main(String[] args) throws Exception {
		
		ServerSocket welcomeSocket = new ServerSocket(6789); //porta 6789;
		System.out.println("Server ready...");
		
		while (true) {
        	Socket connectionSocket = welcomeSocket.accept(); //esperando
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
            
            String clientName = inFromClient.readLine(); //pega o nomo do cliente
            System.out.println(clientName + " joined the chat.");
            
            ConnectedClient client = new ConnectedClient(clientName, outToClient);
            clients.add(client); //add na lista de clientes
                       
            
            new Thread(() -> {
            	String message;
            	final boolean[] disconnectedClient = {false};  //para mudar só o conteúdo
            	            	
            	try {
                                        
                    while ((message = inFromClient.readLine()) != null) { //enquanto recebe mensagem do cliente
                    	
                    	if (message.equalsIgnoreCase("sair")) {
                    		disconnectedClient[0] = true;
                    		break;
                            
                        }
                    	for (ConnectedClient aux : clients) {
                        	aux.out.writeBytes(clientName + " says: " + message + "\n"); //envia para todos os clientes
                        }
                    	
                    }
                } catch (IOException e) {
                	
                }finally {
                    
                    clients.remove(client);
                    
                    try {
                        connectionSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (disconnectedClient[0]) {
                        for (ConnectedClient aux : clients) {
                            try {
                                aux.out.writeBytes(clientName + " has left the chat.\n"); // Notifica os outros clientes de que um deles saiu
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println(clientName + " exited the chat.");
                    } else {
                        System.out.println(clientName + " has disconnected.");
                    }
                }
            }).start();
        }
    }	

}
