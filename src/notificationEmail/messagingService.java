package notificationEmail;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class messagingService {
	
	String Sub = "Your Update";
	String fact = "No facts to display at this time.";
	String news = "";
	LocalTime sentTime = LocalTime.now();
	
	 public void email() {
		 		
		 	String username = loadProperties("EMAIL");
	      	String password = loadProperties("PASSWORD");
	      	String target = loadProperties("TARGET_EMAIL");
	      	String recipientName = loadProperties("RECIPIENT_NAME");

	        Properties props = new Properties();
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.host", "smtp.gmail.com");
	        props.put("mail.smtp.port", "587");

	        Session session = Session.getInstance(props,
	          new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(username, password);
	            }
	          });

	        try {

	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(username));
	            message.setRecipients(Message.RecipientType.TO,
	                InternetAddress.parse(target));
	            message.setSubject(buildMessage.subject(Sub));
	            message.setText("Dear " + recipientName + ","
	                + "\n\n" + buildMessage.factData(fact)
	                	+ "\n\n" + buildMessage.newsData(news)
	            			+ "\n\n Sincerely,"
	            				+ "\n\n Jeeves");

	            Transport.send(message);

	            System.out.println("Update Sent " + sentTime);
	        } catch (MessagingException e) {
	            throw new RuntimeException(e);
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    

	}
	 
	 public static String loadProperties(String config) {
		 
		 	Properties prop = new Properties();
		 	InputStream input = null;
		 	
		 	try {
				input = new FileInputStream("config.properties");
				prop.load(input);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		 	config = prop.getProperty(config);
		 
		 return config;
	 }

}
