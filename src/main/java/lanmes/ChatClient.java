package lanmes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ChatClient {
	
	public static int sendedDataType;
	
	private String name;
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    
    public ChatClient(String host, int port, JTextArea chatArea, String username) {
    	this.name = username;
        try {
        	this.socket = new Socket(host, port);
            this.dataOut = new DataOutputStream(socket.getOutputStream());
            this.dataIn = new DataInputStream(socket.getInputStream());
            
            sendMessageToServer("подключился(ась) к чату");

            new Thread(() -> {
                try {
                    while (true) {
                        
                        int type = dataIn.readInt(); 
                        
                        if (type == 0) {
                            String msg = dataIn.readUTF();
                            chatArea.append(msg + "\n");
                        } 
                        else if (type == 1) {
                            String fileName = dataIn.readUTF();
                            long fileSize = dataIn.readLong();
                            
                            saveFile(fileName, fileSize);
                            chatArea.append("Система: Получен файл " + fileName + "\n");
                        }
                    }
                } catch (IOException e) {
                    chatArea.append("Система: Соединение разорвано.\n");
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveFile(String name, long size) throws IOException {
        File file = new File("downloads/" + name);
        file.getParentFile().mkdirs();
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long remaining = size;
            while (remaining > 0) {
                int read = dataIn.read(buffer, 0, (int)Math.min(buffer.length, remaining));
                fos.write(buffer, 0, read);
                remaining -= read;
            }
        }
    }
    
    public void sendMessageToServer(String text) {
    	if (text.isEmpty()) return;
        try {
            dataOut.writeInt(0);
            dataOut.writeUTF(name + ": " + text);
            dataOut.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public void sendFileToServer(File file, JProgressBar progressBar, JTextArea chatArea) {
        try (FileInputStream fis = new FileInputStream(file)) {
        	long fileSize = file.length();
        	String fileName = file.getName();
             
            SwingUtilities.invokeLater(() -> {
                progressBar.setMaximum(100);
                progressBar.setValue(0);
            });
             
            dataOut.writeInt(1);
            dataOut.writeUTF(fileName);
            dataOut.writeLong(fileSize);

            byte[] buffer = new byte[4096];
            int read;
            long totalSent = 0;
            
            while ((read = fis.read(buffer)) != -1) {
                dataOut.write(buffer, 0, read);
                totalSent += read;
                int progress = (int) ((totalSent * 100) / fileSize);

                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
            }
            dataOut.flush();
            
            chatArea.append("Система: Отправлен файл " + fileName + "\n");
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Файл успешно отправлен!");
            });
        } catch (IOException e) { e.printStackTrace(); }
    }
}

