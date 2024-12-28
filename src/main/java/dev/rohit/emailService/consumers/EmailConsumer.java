package dev.rohit.emailService.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rohit.emailService.consumers.dtos.EmailFormatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class EmailConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics="sendEmail", id="emailConsumerGroup")
    public void sendEmail(String Message){
        EmailFormatDTO emailMessageToSend =null;
        try {
            emailMessageToSend = objectMapper.readValue(Message , EmailFormatDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        sendActuleEmail(emailMessageToSend);

        System.out.println("Method of Send Email calling with message:- "+Message);
    }

    private void sendActuleEmail(EmailFormatDTO emailMessageToSend) {
        // Sending an email using SMTP protocol
        final String fromEmail = emailMessageToSend.getFrom(); //requires valid gmail id
        final String password = "vfntgaydbekjwjhd"; // correct password for gmail id
        final String toEmail = emailMessageToSend.getTo(); // can be any email id
        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, toEmail,"TLSEmail Testing Subject, Welcome to product service "+ emailMessageToSend.getSubject(), "TLSEmail Testing Body");

    }
}
