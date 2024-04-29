import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class EmailClientApp {

    private final String username = "laboratorpr79@gmail.com";
    private final String password = "********";
    private final String smtpHost = "smtp.gmail.com";
    private final String imapHost = "imap.gmail.com";

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- Email Client Menu ---");
            System.out.println("1. Send a simple email");
            System.out.println("2. Send an email with attachment");
            System.out.println("3. Download email attachments");
            System.out.println("4. Show emails using IMAP");
            System.out.println("5. Show emails using POP3");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Recipient: ");
                    String recipient = scanner.nextLine();
                    System.out.print("Subject: ");
                    String subject = scanner.nextLine();
                    System.out.print("Body: ");
                    String body = scanner.nextLine();
                    System.out.print("Reply-to (optional): ");
                    String replyTo = scanner.nextLine();
                    sendSimpleEmail(recipient, subject, body, replyTo);
                    break;
                case "2":
                    System.out.print("Recipient: ");
                    String recipientAttachment = scanner.nextLine();
                    System.out.print("Attachment file path: ");
                    String attachmentFilePath = scanner.nextLine();
                    System.out.print("Subject: ");
                    String subjectAttachment = scanner.nextLine();
                    System.out.print("Reply-to (optional): ");
                    String replyToAttachment = scanner.nextLine();
                    sendEmailWithAttachment(recipientAttachment, attachmentFilePath, subjectAttachment, replyToAttachment);
                    break;
                case "3":
                    System.out.print("Enter email number to download attachments: ");
                    int emailNumber = Integer.parseInt(scanner.nextLine());
                    downloadAttachments(emailNumber);
                    break;
                case "4":
                    showEmailsUsingIMAP();
                    break;
                case "5":
                    showEmailsUsingPOP3();
                    break;
                case "6":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    public void showEmailsUsingIMAP() {
        try {
            Properties props = new Properties();
            props.put("mail.imaps.ssl.protocols", "TLSv1.2");
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect(imapHost, username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessages();

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                System.out.println("From: " + InternetAddress.toString(message.getFrom()));
                System.out.println("Subject: " + message.getSubject());
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEmailsUsingPOP3() {
        try {
            Properties props = new Properties();
            props.put("mail.pop3s.ssl.protocols", "TLSv1.2");
            props.setProperty("mail.store.protocol", "pop3s");

            Session session = Session.getInstance(props, null);
            Store store = session.getStore("pop3s");
            store.connect("pop.gmail.com", username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessages();

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                System.out.println("From: " + InternetAddress.toString(message.getFrom()));
                System.out.println("Subject: " + message.getSubject());
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendSimpleEmail(String recipient, String subject, String body, String replyTo) {
        Properties props = new Properties();
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            if (replyTo != null && !replyTo.isEmpty()) {
                message.setReplyTo(InternetAddress.parse(replyTo));
            }
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipient);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendEmailWithAttachment(String recipient, String attachmentFilePath, String subject, String replyTo) {
        Properties props = new Properties();
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            if (replyTo != null && !replyTo.isEmpty()) {
                message.setReplyTo(InternetAddress.parse(replyTo));
            }
            message.setSubject(subject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Please find the attachment below.");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentFilePath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(new File(attachmentFilePath).getName());
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Email with attachment sent successfully to " + recipient);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public void downloadAttachments(int emailNumber) {
        Properties props = new Properties();
        props.put("mail.imaps.ssl.protocols", "TLSv1.2");
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect(imapHost, username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessages();

            if (emailNumber > 0 && emailNumber <= messages.length) {
                Message message = messages[emailNumber - 1];
                if (message.getContent() instanceof Multipart) {
                    Multipart multipart = (Multipart) message.getContent();
                    boolean hasAttachment = false;
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                            hasAttachment = true;
                            String fileName = bodyPart.getFileName();
                            InputStream is = bodyPart.getInputStream();
                            FileOutputStream fos = new FileOutputStream( "C:/Anul3UTM/lab5PR/" + fileName);
                            byte[] buf = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buf)) != -1) {
                                fos.write(buf, 0, bytesRead);
                            }
                            fos.close();
                            System.out.println("Attachment '" + fileName + "' downloaded successfully.");
                        }
                    }
                    if (!hasAttachment) {
                        System.out.println("The email does not contain any attachments.");
                    }
                } else {
                    System.out.println("The email does not contain any attachments.");
                }
            } else {
                System.out.println("Invalid email number.");
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        EmailClientApp emailClient = new EmailClientApp();
        emailClient.showMenu();
    }
}
