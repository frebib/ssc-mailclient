package net.frebib.sscmailclient;

import javax.mail.*;
import java.util.Date;

public class Email {
    private Address from;
    private Address[] to;
    private String subject;
    private Date receivedDate;
    private boolean read, recent, answered;
    private Content content;

    private Message msg;

    public Email(Message msg) {
        this.msg = msg;
        try {
            from = msg.getFrom()[0];
            to = msg.getAllRecipients();
            subject = msg.getSubject();
            receivedDate = msg.getReceivedDate();
            read = msg.isSet(Flags.Flag.SEEN);
            recent = msg.isSet(Flags.Flag.RECENT);
            answered = msg.isSet(Flags.Flag.ANSWERED);
            content = getContent(msg);
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
    }

    public Address[] getTo() {
        return to;
    }
    public Address getFrom() {
        return from;
    }
    public Content getContent() {
        return content;
    }
    public Date getReceivedDate() {
        return receivedDate;
    }
    public String getSubject() {
        return subject;
    }
    public String getBody() {
        return content.getBody();
    }
    public boolean isAnswered() {
        return answered;
    }
    public boolean isRead() {
        return read;
    }
    public boolean isRecent() {
        return recent;
    }

    private Content getContent(Message msg) {
        try {
            // Is a text/html message
            if (msg.isMimeType("text/*")) {
                return new TextContent(msg);
            } else {
                return new MultipartContent(msg);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface Content {
        String getBody();
    }
    public static class TextContent implements Content {
        protected Message msg;
        protected boolean downloaded = false;
        protected String body;

        public TextContent(Message msg) {
            this.msg = msg;
        }

        public String getType() {
            try {
                if (msg.isMimeType("text/html"))
                    return "text/html";
                else if (msg.isMimeType("text/plain"))
                    return "text/plain";
                else return null;
            } catch (MessagingException e) {
                MailClient.LOG.exception(e);
            }
            return null;
        }

        @Override
        public String getBody() {
            try {
                if (!downloaded) {
                    MailClient.LOG.fine("Downloading content for message: "
                            + msg.toString());
                    body = (String) msg.getContent();
                }
                return body;
            } catch (Exception e) {
                MailClient.LOG.exception(e);
            }
            return null;
        }
    }
    public static class MultipartContent extends TextContent {

        public MultipartContent(Message msg) {
            super(msg);
        }

        @Override
        public String getBody() {
            try {
                if (!downloaded) {
                    MailClient.LOG.fine("Downloading content for message: "
                            + msg.toString());

                    Multipart mp = (Multipart) msg.getContent();
                    for (int i=0; i < mp.getCount(); i++) {
                        Part part = mp.getBodyPart(i);
                        if (part.isMimeType("text/*")) {
                            MailClient.LOG.finer(Boolean.toString(part.getContent() instanceof String));
                            body = (String) part.getContent();
                            break;
                        }
                    }
                }
                return body;
            } catch (Exception e) {
                MailClient.LOG.exception(e);
            }
            return null;
        }

        public Object[] getAttachments() {
            return new Object[0];
        }
    }
}
