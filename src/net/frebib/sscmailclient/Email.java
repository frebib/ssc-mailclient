package net.frebib.sscmailclient;

import javax.mail.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void setAnswered(boolean answered) {
        this.answered = answered;
        try {
            msg.setFlag(Flags.Flag.ANSWERED, answered);
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
    }
    public void setRead(boolean read) {
        this.read = read;
        try {
            msg.setFlag(Flags.Flag.SEEN, read);
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
    }
    public void setRecent(boolean recent) {
        this.recent = recent;
        try {
            msg.setFlag(Flags.Flag.RECENT, recent);
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
    }

    private Content getContent(Part part) {
        try {
            // Is a text/html message
            if (part.isMimeType("text/*")) {
                return new TextContent(part);
            } else {
                return new MultipartContent(part);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface Content {
        String getBody();
    }
    public class TextContent implements Content {
        protected Part part;
        protected boolean downloaded = false;
        protected String body;

        public TextContent(Part part) {
            this.part = part;
        }

        public String getType() {
            try {
                if (part.isMimeType("text/html"))
                    return "text/html";
                else if (part.isMimeType("text/plain"))
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
                    downloaded = true;
                    body = (String) Email.this.msg.getContent();
                }
                return body;
            } catch (Exception e) {
                MailClient.LOG.exception(e);
            }
            return null;
        }
    }
    public class MultipartContent extends TextContent {
        private List<Content> contents;

        public MultipartContent(Part part) {
            super(part);
            contents = new ArrayList<Content>();
        }

        @Override
        public String getBody() {
            try {
                if (!downloaded) {
                    downloaded = true;
                    Multipart mp = (Multipart) part.getContent();
                    for (int i=0; i < mp.getCount(); i++) {
                        Part part = mp.getBodyPart(i);
                        if (part.isMimeType("multipart/*")) {
                            // Yay recursion :D
                            Content subch = getContent(part);
                            String txt = subch.getBody();
                            if (txt != null)
                                body += txt;

                            contents.add(subch);
                        }
                        if (part.isMimeType("text/*")) {
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
