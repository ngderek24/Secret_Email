import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.security.Security;
import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JTextPane;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

public class Email {

	private JFrame frame;
	private JTextField recipientsField;
	private JTextField subjectField;
	private JTextField attachField;
	private String attachment_path;
	private String filename;
	
	//encryption variables
	byte[] input;
	byte[] subjInput;
	byte[] keyBytes = "eastog24".getBytes();
	byte[] ivBytes = "tinosaur".getBytes();
	SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
	IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
	Cipher cipher;
	byte[] cipherText;
	byte[] subjectText;
	int ctLength;
	int stLength;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Email window = new Email();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Email() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 597, 470);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Encrypted Mail Sender");
		
		JTextPane msgTxtPane = new JTextPane();
		msgTxtPane.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		msgTxtPane.setBounds(98, 142, 442, 244);
		frame.getContentPane().add(msgTxtPane);
		
		JLabel lblTo = new JLabel("To :");
		lblTo.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblTo.setBounds(54, 24, 34, 14);
		frame.getContentPane().add(lblTo);
		
		recipientsField = new JTextField();
		recipientsField.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		recipientsField.setBounds(98, 17, 442, 29);
		frame.getContentPane().add(recipientsField);
		recipientsField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Message :");
		lblNewLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblNewLabel.setBounds(19, 142, 61, 23);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblSubject = new JLabel("Subject :");
		lblSubject.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		lblSubject.setBounds(22, 63, 61, 23);
		frame.getContentPane().add(lblSubject);
		
		subjectField = new JTextField();
		subjectField.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		subjectField.setBounds(98, 61, 442, 29);
		frame.getContentPane().add(subjectField);
		subjectField.setColumns(10);
		
		attachField = new JTextField();
		attachField.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		attachField.setBounds(98, 101, 442, 29);
		frame.getContentPane().add(attachField);
		attachField.setColumns(10);
		
		JButton btnAttach = new JButton("Attach");
		btnAttach.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File file = chooser.getSelectedFile();
				attachment_path = file.getAbsolutePath();
				filename = file.getName();
				attachField.setText(filename);
			}
		});
		btnAttach.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnAttach.setBounds(8, 104, 78, 23);
		frame.getContentPane().add(btnAttach);
		
		JButton btnEncrypt = new JButton("Encrypt");
		btnEncrypt.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnEncrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
					input = msgTxtPane.getText().getBytes();
					subjInput = subjectField.getText().getBytes();
					SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
					IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
					cipher = Cipher.getInstance("DES/CTR/NoPadding", "BC");
					cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
					
					cipherText = new byte[cipher.getOutputSize(input.length)];
					ctLength = cipher.update(input, 0, input.length, cipherText, 0);
					ctLength += cipher.doFinal(cipherText, ctLength);
					msgTxtPane.setText(new String(cipherText));
					
					subjectText = new byte[cipher.getOutputSize(subjInput.length)];
					stLength = cipher.update(subjInput, 0, subjInput.length, subjectText, 0);
					stLength += cipher.doFinal(subjectText, stLength);
					subjectField.setText(new String(subjectText));
				} catch(Exception e){
					JOptionPane.showMessageDialog(null, e);
				}
			}
		});
		btnEncrypt.setBounds(352, 397, 89, 23);
		frame.getContentPane().add(btnEncrypt);
		
		JButton btnDecrypt = new JButton("Decrypt");
		btnDecrypt.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
					byte[] plainText = new byte[cipher.getOutputSize(ctLength)];
					int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
					ptLength += cipher.doFinal(plainText, ptLength);
					msgTxtPane.setText(new String(plainText));
					
					byte[] plainSubj = new byte[cipher.getOutputSize(stLength)];
					int psLength = cipher.update(subjectText, 0, stLength, plainSubj, 0);
					psLength += cipher.doFinal(plainSubj, psLength);
					subjectField.setText(new String(plainSubj));
				} catch(Exception err){
					JOptionPane.showMessageDialog(null, err);
				}
			}
		});
		btnDecrypt.setBounds(451, 397, 89, 23);
		frame.getContentPane().add(btnDecrypt);
		
		JButton btnMail = new JButton("Send");
		btnMail.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnMail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.port", "587");
				
				Session session = Session.getDefaultInstance(props, new Authenticator(){
					@Override
					protected PasswordAuthentication getPasswordAuthentication(){
						return new PasswordAuthentication("tincan24@gmail.com", "%optimists19");
					}
				});
				
				try{
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress("tincan24@gmail.com"));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientsField.getText()));
					message.setSubject(subjectField.getText());
					//message.setText(msgTxtPane.getText());
					
					MimeBodyPart msgBody = new MimeBodyPart();
					msgBody.setText(msgTxtPane.getText());
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(msgBody);
					
					msgBody = new MimeBodyPart();
					DataSource source = new FileDataSource(attachment_path);
					msgBody.setDataHandler(new DataHandler(source));
					msgBody.setFileName(filename);
					multipart.addBodyPart(msgBody);
					message.setContent(multipart);
					
					Transport.send(message);
					JOptionPane.showMessageDialog(null, "Email was successfully sent.");
				} catch(Exception e){
					JOptionPane.showMessageDialog(null, e);
				}
			}
		});
		btnMail.setBounds(253, 397, 89, 23);
		frame.getContentPane().add(btnMail);
	}
}
