package lanmes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTextArea;

public class Server extends Thread {

	private static int PORT;
	private static ServerSocket serverSocket;
	
	public static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public Server(JTextArea chatArea) throws IOException {
    	PORT = 8080;
        serverSocket = new ServerSocket(PORT);
        chatArea.append("Система: Сервер запущен на порте " + PORT + "\n");
        this.start();
    }
    
    public Server(JTextArea chatArea, int port) throws IOException {
    	PORT = port;
    	serverSocket = new ServerSocket(PORT);
    	chatArea.append("Система: Сервер запущен на порте " + PORT + "\n");
    	this.start();
    }
    
    @Override
    public void run() {
    	while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
            } catch (IOException e) {
                if (!serverSocket.isClosed()) e.printStackTrace();
            }
        }
    }
	
	public static int getPort() { return PORT; }
    
}
