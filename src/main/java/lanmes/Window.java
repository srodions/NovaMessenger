package lanmes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;

public class Window {

	private JFrame frmLanmes;
	private JTextField textFieldMsg;
	private ChatClient chatClient;
	private JTextArea textArea;
	private JLabel lblArtLabel;
	private JButton btnStart;
	private JProgressBar progressBar;
	
	private boolean isConnected = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
		    UIManager.setLookAndFeel(new FlatLightLaf());
		} catch( Exception ex ) {
		    System.err.println( "Failed to initialize LaF" );
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frmLanmes.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		setLook();
		initialize();
	}
	
	void setLook()
	{
		FlatDarkLaf.setup();
		
		UIManager.put("OptionPane.background", AppColors.backgroundColor);
        UIManager.put("Panel.background", AppColors.backgroundColor);
        UIManager.put("Button.background", AppColors.mainColor);
        UIManager.put("Button.foreground", AppColors.textColor);
		UIManager.put("List.background", AppColors.backgroundColor);
        UIManager.put("List.foreground", AppColors.textColor);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumb", AppColors.mainColor);
        UIManager.put("ScrollBar.hoverThumbColor", AppColors.accentColor);
        UIManager.put("ScrollBar.pressedThumbColor", AppColors.backgroundColor);
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.track", AppColors.backgroundColor);      
        UIManager.put("TextField.background", AppColors.mainColor);
        UIManager.put("TextField.foreground", AppColors.textColor);
	}
	
	void finalizeConnection() {
		btnStart.setVisible(false);
		lblArtLabel.setVisible(false);
		isConnected = true;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmLanmes = new JFrame();
		frmLanmes.setResizable(false);
		frmLanmes.getContentPane().setBackground(AppColors.backgroundColor);
		frmLanmes.setTitle("Мессенджер Нова");
		frmLanmes.setBounds(100, 100, 800, 600);
		frmLanmes.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLanmes.getContentPane().setLayout(null);
		frmLanmes.setIconImage(new ImageIcon(getClass().getResource("/images/app-icon.png")).getImage());
		
		textFieldMsg = new JTextField();
		textFieldMsg.putClientProperty(FlatClientProperties.STYLE, "arc: 35");
		textFieldMsg.putClientProperty("JTextField.placeholderText", "Введите сообщение...");
		
		JLabel lblDragAndDrop = new JLabel(new ImageIcon(getClass().getResource("/images/drag-and-drop.png")));
		lblDragAndDrop.setBounds(292, 135, 273, 210);
		lblDragAndDrop.setVisible(false);
		frmLanmes.getContentPane().add(lblDragAndDrop);
		
		lblArtLabel = new JLabel(new ImageIcon(getClass().getResource("/images/art.png")));
		lblArtLabel.setBounds(266, 135, 317, 169);
		frmLanmes.getContentPane().add(lblArtLabel);
		textFieldMsg.setFont(new Font("Arial", Font.PLAIN, 14));
		textFieldMsg.setBounds(116, 501, 576, 35);
		frmLanmes.getContentPane().add(textFieldMsg);
		textFieldMsg.setColumns(10);
		
		ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (chatClient != null) {
		            chatClient.sendMessageToServer(textFieldMsg.getText());
		            textFieldMsg.setText("");
		        }
				textArea.setEnabled(true);
            }
        };
        textFieldMsg.addActionListener(sendAction);
		
		JButton btnSend = new JButton(new ImageIcon(getClass().getResource("/images/send.png")));
		btnSend.setContentAreaFilled(false);
		btnSend.setBorderPainted(false);
	    btnSend.setFocusPainted(false);
	    btnSend.setOpaque(false); 
		btnSend.addActionListener(sendAction);
		btnSend.setBounds(702, 496, 44, 40);
		frmLanmes.getContentPane().add(btnSend);
		btnSend.setFont(new Font("Verdana", Font.PLAIN, 13));
		btnSend.setForeground(AppColors.textColor);
		btnSend.setBackground(AppColors.accentColor);
		btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		JTextField textFieldName = new JTextField(7);
		textFieldName.putClientProperty(FlatClientProperties.STYLE, "arc: 35");
		textFieldName.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JTextField textFieldHost = new JTextField(15);
		textFieldHost.putClientProperty(FlatClientProperties.STYLE, "arc: 35");
		textFieldHost.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JTextField textFieldPort = new JTextField(4);
		textFieldPort.putClientProperty(FlatClientProperties.STYLE, "arc: 35");
		textFieldPort.setFont(new Font("Arial", Font.PLAIN, 12));
		
		btnStart = new JButton("Начать общение");
		btnStart.setForeground(AppColors.textColor);
		btnStart.setFont(new Font("Arial", Font.BOLD, 14));
		btnStart.setBackground(AppColors.accentColor);
		btnStart.setBounds(292, 336, 270, 35);
		btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnStart.setBorderPainted(false);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        
		        String[] options = { "Создать", "Подключить", "Отмена" };
				JPanel startPanel = new JPanel(new GridLayout(3, 1, 5, 5));
				JLabel lblName = new JLabel("Ваше имя");
				JLabel lblHost = new JLabel("Хост");
				JLabel lblPort = new JLabel("Порт");
					
				startPanel.add(lblName);
				startPanel.add(textFieldName);
				startPanel.add(lblHost);
				startPanel.add(textFieldHost);
				startPanel.add(lblPort);
				startPanel.add(textFieldPort);
				
				int result = JOptionPane.showOptionDialog(
	                null,           
	                startPanel, 
	                "Начать общение",       
	                JOptionPane.DEFAULT_OPTION,
	                JOptionPane.PLAIN_MESSAGE,
	                null,      
	                options,
	                options[0]
		        );
				
				switch (result) {
				case 0:
			        if (textFieldName.getText().isEmpty()) return;
			        try {
			            int port = textFieldPort.getText().isEmpty() ? 0 : Integer.parseInt(textFieldPort.getText());
			            
			            if (port == 0) {
			                new Server(textArea);
			            } else {
			                new Server(textArea, port);
			            }
			            
			            Thread.sleep(100); 
			            
			            chatClient = new ChatClient("localhost", Server.getPort(), textArea, textFieldName.getText());
			            finalizeConnection();
			        } catch (Exception ex) {
			            JOptionPane.showMessageDialog(null, "Ошибка запуска сервера: " + ex.getMessage());
			        }
			        break;

			    case 1:
			        try {
			            String name = textFieldName.getText();
			            String host = textFieldHost.getText();
			            int port = Integer.parseInt(textFieldPort.getText());
			            
			            if (!name.isEmpty() && !host.isEmpty()) {
			                chatClient = new ChatClient(host, port, textArea, name);
			                finalizeConnection();
			            }
			        } catch (NumberFormatException ex) {
			            JOptionPane.showMessageDialog(null, "Порт должен быть числом!");
			        } catch (Exception ex) {
			            JOptionPane.showMessageDialog(null, "Не удалось подключиться: " + ex.getMessage());
			        }
			        break;
				}
			}
		});
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Выберите файл");
		JButton btnPickFile = new JButton("Выбрать файл");
		btnPickFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int result = fileChooser.showOpenDialog(null);
				
				if (result == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					chatClient.sendFileToServer(selectedFile, progressBar, textArea);
				}
			}
		});
		btnPickFile.setForeground(Color.WHITE);
		btnPickFile.setFont(new Font("Arial", Font.BOLD, 14));
		btnPickFile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnPickFile.setBorderPainted(false);
		btnPickFile.setBackground(AppColors.accentColor);
		btnPickFile.setBounds(292, 379, 270, 35);
		btnPickFile.setVisible(false);
		frmLanmes.getContentPane().add(btnPickFile);
		frmLanmes.getContentPane().add(btnStart);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(106, 27, 651, 446);
		frmLanmes.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEnabled(false);
		textArea.setFont(new Font("Arial", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);
		scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
		textArea.setEditable(false);
		textArea.setForeground(AppColors.textColor);
		textArea.setBackground(AppColors.mainColor);
		textArea.setLineWrap(true);
		
		JButton btnMessenger = new JButton(new ImageIcon(Window.class.getResource("/images/messenger.png")));
		JButton btnTransmit = new JButton(new ImageIcon(Window.class.getResource("/images/transmit.png")));
		
		btnMessenger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnMessenger.setBackground(AppColors.accentColor);
				btnTransmit.setBackground(AppColors.mainColor);
				// Mini-window
				btnPickFile.setVisible(false);
				lblDragAndDrop.setVisible(false);
				textArea.setVisible(true);
				scrollPane.setVisible(true);
			}
		});
		btnMessenger.setOpaque(false);
		btnMessenger.setForeground(Color.WHITE);
		btnMessenger.setFont(new Font("Verdana", Font.PLAIN, 13));
		btnMessenger.setFocusPainted(false);
		btnMessenger.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnMessenger.setBorderPainted(false);
		btnMessenger.setBackground(AppColors.accentColor);
		btnMessenger.setBounds(10, 105, 60, 60);
		frmLanmes.getContentPane().add(btnMessenger);
		
		btnTransmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isConnected) return;
				btnMessenger.setBackground(AppColors.mainColor);
				btnTransmit.setBackground(AppColors.accentColor);
				// Mini-window
				btnStart.setVisible(false);
				btnPickFile.setVisible(true);
				lblArtLabel.setVisible(false);
				lblDragAndDrop.setVisible(true);
				textArea.setVisible(false);
				scrollPane.setVisible(false);
				if (progressBar.getValue() == 100) progressBar.setValue(0);
			}
		});
		btnTransmit.setOpaque(false);
		btnTransmit.setForeground(Color.WHITE);
		btnTransmit.setFont(new Font("Verdana", Font.PLAIN, 13));
		btnTransmit.setFocusPainted(false);
		btnTransmit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnTransmit.setBorderPainted(false);
		btnTransmit.setBackground(AppColors.mainColor);
		btnTransmit.setBounds(10, 197, 60, 60);
		frmLanmes.getContentPane().add(btnTransmit);
		
		JLabel lblMessager = new JLabel("Связь");
		lblMessager.setFont(new Font("Arial", Font.BOLD, 12));
		lblMessager.setForeground(new Color(112, 113, 143));
		lblMessager.setBounds(22, 168, 35, 14);
		frmLanmes.getContentPane().add(lblMessager);
		
		JLabel lblTransmit = new JLabel("Отправить");
		lblTransmit.setForeground(new Color(112, 113, 143));
		lblTransmit.setFont(new Font("Arial", Font.BOLD, 12));
		lblTransmit.setBounds(9, 261, 61, 14);
		frmLanmes.getContentPane().add(lblTransmit);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(65, 71, 101));
		panel.setBounds(0, 0, 80, 561);
		frmLanmes.getContentPane().add(panel);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(116, 27, 630, 26);
		progressBar.setBackground(AppColors.mainColor);
		frmLanmes.getContentPane().add(progressBar);
	}
}
