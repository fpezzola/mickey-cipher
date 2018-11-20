package ar.edu.unlam.Mickey.view;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

import ar.edu.unlam.Mickey.cipher.MickeyCipher;
import ar.edu.unlam.Mickey.utils.Action;

import java.awt.Font;
import java.awt.Color;

public class Mickey {

	private JFrame frame;
	private JTextField textField;
	private File imageFileToEncode;
	private Action action;


	/**
	 * Create the application.
	 */
	public Mickey(boolean visible) {
		initialize();
		frame.setVisible(visible);
        
	}
	
	
	
	private byte[] imageToByteArray(File imageToConvert) {
		
		try {
			
			return Files.readAllBytes(imageToConvert.toPath());

		}catch(IOException e) {
			return null;
		}
		
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 627, 551);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		final JLabel lblNewLabel = new JLabel("Previsualizaci\u00F3n de imagen");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(28, 184, 545, 226);
		frame.getContentPane().add(lblNewLabel);

		final JLabel lblNingnArchivoSeleccionado = new JLabel("Ning\u00FAn archivo seleccionado");
		lblNingnArchivoSeleccionado.setBounds(254, 153, 319, 14);
		frame.getContentPane().add(lblNingnArchivoSeleccionado);
		
		final JButton button = new JButton("Realizar operacion");
		JButton btnAbrirArchivo = new JButton("Seleccionar archivo...");
		
		//
		final JLabel lblStatus = new JLabel("");
		lblStatus.setForeground(new Color(255, 69, 0));
		lblStatus.setFont(lblStatus.getFont().deriveFont(lblStatus.getFont().getStyle() | Font.BOLD | Font.ITALIC));
		lblStatus.setBounds(28, 110, 581, 16);
		frame.getContentPane().add(lblStatus);

		final JLabel lblKey = new JLabel("");
		lblKey.setBounds(194, 464, 202, 48);
		frame.getContentPane().add(lblKey);
		
		
		btnAbrirArchivo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();

				   if(fileChooser.showOpenDialog(frame) ==  JFileChooser.APPROVE_OPTION) {
					   imageFileToEncode = fileChooser.getSelectedFile();
					   BufferedImage img;
					   lblStatus.setText("");
					   lblKey.setText("");
					   lblNingnArchivoSeleccionado.setText(imageFileToEncode.getName());
					   try {
						img = ImageIO.read(imageFileToEncode);
						Image image = img.getScaledInstance(lblNewLabel.getWidth(), lblNewLabel.getHeight(), BufferedImage.SCALE_DEFAULT);
						ImageIcon icon = new ImageIcon(image);
						lblNewLabel.setIcon(icon);
						action = Action.ENCRYPT;
						button.setText("Encriptar");
						
							   
					} catch (Exception e1) {
						lblNewLabel.setIcon(null);
						action = Action.DECRYPT;
						button.setText("Desencriptar");
						lblNewLabel.setText("El archivo ingresado no es una imagen, asi que vamos a desencriptarlo");
					}
			            
		      }
					 		
			}
		});
		btnAbrirArchivo.setBounds(28, 150, 182, 23);
		frame.getContentPane().add(btnAbrirArchivo);
		
		
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 String key = "";
				 String iv = "";
				try {
				   key = textField.getText().substring(0, 10);
				   System.out.println(key);
				   iv = textField.getText().substring(10);
				}catch(Exception e) {
					
				}
		
				
				if(imageFileToEncode == null || key == null || key.trim() == "" || key.length() !=10) {
					JOptionPane optionPane = new JOptionPane("Debe seleccionar un archivo y una clave", JOptionPane.ERROR_MESSAGE);    
					JDialog dialog = optionPane.createDialog("Error!");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
				}else {
					
				lblKey.setText("Clave utilizada:  "+key);
					
				JFileChooser fileChooser = new JFileChooser();
				   if(fileChooser.showSaveDialog(frame) ==  JFileChooser.APPROVE_OPTION) {
					   final int BITMAP_START = 154;
					   File fileToSave = fileChooser.getSelectedFile();
					   byte [] b = imageToByteArray(imageFileToEncode);
					   int [] cipherBytes = new int [b.length - BITMAP_START]; //Integer.
					   int [] headerBytes = new int [BITMAP_START]; //Integer.
					   for (int i = 0; i < BITMAP_START; i++) {
						   headerBytes[i] = b[i];
					   }
					   for (int i = BITMAP_START; i < b.length; i++) {
						   cipherBytes[i - BITMAP_START] = b[i];
					   }
					   
					   
					   
					   int [] keyBytes = new int [key.length()];
					   for (int i = 0; i < key.length(); i++) {
						   keyBytes[i] = key.charAt(i);
					   }
					   
					   int [] ivBytes = new int [iv.length()];
					   for (int i = 0; i < iv.length(); i++) {
						   ivBytes[i] = iv.charAt(i);
					   }
					   
					   MickeyCipher cipher = new MickeyCipher(keyBytes, ivBytes);
					   int[] fileContent = cipher.encrypt(cipherBytes);
					   byte[] array = new byte[fileContent.length + headerBytes.length] ;
					   for (int i = 0; i < headerBytes.length; i++) {
						   array[i] = (byte) headerBytes[i];
					   }
					   for (int i = 0; i < fileContent.length; i++) {
						   array[i + headerBytes.length] = (byte) fileContent[i];
					   }
					   try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
						   fos.write(array);
						   
						   boolean isOk = true;
						   
						   if(action == Action.DECRYPT) {
							try {
								InputStream in = new ByteArrayInputStream(array);
								BufferedImage   img = ImageIO.read(in);
								Image image = img.getScaledInstance(lblNewLabel.getWidth(), lblNewLabel.getHeight(), BufferedImage.SCALE_DEFAULT);
								ImageIcon icon = new ImageIcon(image);
								lblNewLabel.setIcon(icon);
								
							}catch(Exception e) {
								JOptionPane optionPane = new JOptionPane("Al parecer no has ingresado bien la clave y no pudimos desencriptar la imagen.", JOptionPane.ERROR_MESSAGE);    
								JDialog dialog = optionPane.createDialog("Error!");
								dialog.setAlwaysOnTop(true);
								dialog.setVisible(true);
								isOk = false;
							}
						   }
						   
						   if(isOk) {
							   lblStatus.setText("Operacion realizada con exito, para continuar seleccionar otro archivo.");
							   lblStatus.setForeground(new Color(154, 205, 50));
							   lblNingnArchivoSeleccionado.setText("Ning\u00FAn archivo seleccionado");  
							   lblNewLabel.setText("Previsualizaci\\u00F3n de imagen");
							   imageFileToEncode = null;
						   }else {
							   lblStatus.setForeground(new Color(255, 69, 0));
							   lblStatus.setText("Al parecer no has ingresado bien la clave. Por favor intente nuevamente");
						   }
						   
						   
					   } catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Save as file: " + fileToSave.getAbsolutePath());
				   }

				}
					 						
			}
		});
		button.setBounds(165, 438, 247, 23);
		frame.getContentPane().add(button);
		
		textField = new JTextField();
		textField.setBounds(253, 59, 320, 23);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblClaveCaracteres = new JLabel("Clave (0 a 10 caracteres)");
		lblClaveCaracteres.setBounds(32, 64, 176, 14);
		frame.getContentPane().add(lblClaveCaracteres);
		
		JLabel lblAlgoritmoMickey = new JLabel("Algoritmo Mickey");
		lblAlgoritmoMickey.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblAlgoritmoMickey.setBounds(210, 6, 202, 30);
		frame.getContentPane().add(lblAlgoritmoMickey);
		
		
	

	}
}