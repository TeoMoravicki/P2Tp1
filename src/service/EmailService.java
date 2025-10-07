package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final boolean enabled;

    public EmailService(String host, String port, String username, String password, boolean enabled) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public void enviarNotificacionPago(String destinatario, String asunto, String mensaje) {
        if (!enabled) {
            System.out.println("Servicio de email deshabilitado. Mensaje que se hubiera enviado:");
            System.out.println("Para: " + destinatario);
            System.out.println("Asunto: " + asunto);
            System.out.println("Mensaje: " + mensaje);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(username));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            email.setSubject(asunto);
            email.setText(mensaje);

            Transport.send(email);
            System.out.println("✅ Email enviado exitosamente a: " + destinatario);

        } catch (Exception e) {
            System.err.println("❌ Error enviando email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}