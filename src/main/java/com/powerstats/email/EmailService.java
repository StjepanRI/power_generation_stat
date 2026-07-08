package com.powerstats.email;

import com.powerstats.entity.EmailSource;
import com.powerstats.repository.EmailSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class EmailService {

    private final EmailSourceRepository emailSourceRepository;

    @Value("${email.imap-host:imap.gmail.com}")
    private String imapHost;

    @Value("${email.imap-port:993}")
    private int imapPort;

    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.password}")
    private String emailPassword;

    @Value("${email.folder:INBOX}")
    private String emailFolder;

    public EmailService(EmailSourceRepository emailSourceRepository) {
        this.emailSourceRepository = emailSourceRepository;
    }

    /**
     * Fetches unread emails from IMAP server
     */
    public List<Message> fetchUnreadEmails() {
        List<Message> emails = new ArrayList<>();

        try {
            Properties props = new Properties();
            props.put("mail.imap.host", imapHost);
            props.put("mail.imap.port", imapPort);
            props.put("mail.imap.starttls.enable", "true");
            props.put("mail.imap.auth", "true");
            props.put("mail.debug", "false");

            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");

            store.connect(imapHost, emailUsername, emailPassword);
            log.info("Connected to IMAP server: {}", imapHost);

            Folder folder = store.getFolder(emailFolder);
            folder.open(Folder.READ_ONLY);

            // Search for unread messages
            Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            for (Message message : messages) {
                emails.add(message);
            }

            log.info("Found {} unread emails in folder: {}", emails.size(), emailFolder);

            folder.close(false);
            store.close();

        } catch (MessagingException e) {
            log.error("Error fetching emails from IMAP server", e);
        }

        return emails;
    }

    /**
     * Extracts email content (body text)
     */
    public String getEmailContent(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String) {
                return (String) content;
            } else if (content instanceof Multipart) {
                return getMultipartContent((Multipart) content);
            }
        } catch (Exception e) {
            log.error("Error extracting email content", e);
        }
        return "";
    }

    /**
     * Extracts content from multipart email
     */
    private String getMultipartContent(Multipart multipart) throws MessagingException {
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/plain")) {
                content.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                content.append(bodyPart.getContent());
            }
        }

        return content.toString();
    }

    /**
     * Saves email metadata to database
     */
    public EmailSource saveEmailSource(Message message) {
        try {
            String uniqueId = message.getMessageNumber() + "_" + message.getSentDate().getTime();
            String subject = message.getSubject();
            String from = message.getFrom()[0].toString();
            LocalDateTime receivedDate = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(message.getReceivedDate().getTime()),
                    ZoneId.systemDefault()
            );

            EmailSource emailSource = EmailSource.builder()
                    .emailId(uniqueId)
                    .subject(subject)
                    .senderEmail(from)
                    .receivedDate(receivedDate)
                    .status(EmailSource.ProcessingStatus.PENDING)
                    .build();

            return emailSourceRepository.save(emailSource);
        } catch (MessagingException e) {
            log.error("Error saving email source", e);
            return null;
        }
    }
}
