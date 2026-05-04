package lanmes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
	
	private Socket socket;
	private DataOutputStream dos;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.start();
    }

    @Override
    public void run() {
    	 try {
	        DataInputStream dis = new DataInputStream(socket.getInputStream());
	        while (true) {
	            int type = dis.readInt();
	            
	            if (type == 0) {
	                String msg = dis.readUTF();
	                broadcastMessage(msg); 
	            } 
	            else if (type == 1) {
	                String fileName = dis.readUTF();
	                long size = dis.readLong();
	                receiveAndBroadcastFile(dis, fileName, size);
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Система: Клиент отключился");
	    } finally {
            Server.clients.remove(this);
        }
    }
    
    private void receiveAndBroadcastFile(DataInputStream dis, String name, long size) throws IOException {
        File tempFile = new File("temp_" + name);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            long remaining = size;
            while (remaining > 0) {
                int read = dis.read(buffer, 0, (int)Math.min(buffer.length, remaining));
                fos.write(buffer, 0, read);
                remaining -= read;
            }
        }
        
        for (ClientHandler ch : Server.clients) {
        	if (ch != this) ch.sendFileToClient(tempFile);
        }
    }
    
    public void sendFileToClient(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            dos.writeInt(1);
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }
            dos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void broadcastMessage(String message) {
    	for (ClientHandler ch : Server.clients) {
            try {
                ch.dos.writeInt(0);
                ch.dos.writeUTF(message);
                ch.dos.flush();
            } catch (IOException e) {
                Server.clients.remove(ch);
            }
        }
    }
}

